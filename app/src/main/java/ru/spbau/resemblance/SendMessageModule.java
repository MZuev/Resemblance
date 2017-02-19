package ru.spbau.resemblance;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SendMessageModule {
    final private static String LOG_TAG = "Messenger log";
    final private static int maxCntToReconnect = 20;

    final private static String serverIP = "192.168.1.180";
    final private static int serverPort = 6662;

    private final static int sleepTime = 1000;

    private static DataInputStream in = null;
    private static DataOutputStream out = null;

    final private static int STATUS_NOT_OPEN = 0;
    final private static int STATUS_IS_OPENING = 1;
    final private static int STATUS_OPEN = 2;
    final private static int STATUS_CLOSE = 3;

    private static int connectionStatus = STATUS_NOT_OPEN;
    private static final Lock STATUS_LOCK = new ReentrantLock();
    private static final Executor messageWriter = Executors.newSingleThreadExecutor();

    public static boolean isAlive() {
        STATUS_LOCK.lock();
        try {
            return connectionStatus == STATUS_OPEN;
        } finally {
            STATUS_LOCK.unlock();
        }
    }

    private static Thread readThread = new Thread() {
        @Override
        public void run() {
            while (true) {
                while (true) {
                    STATUS_LOCK.lock();
                    try {
                        if (connectionStatus == STATUS_OPEN) {
                            break;
                        }
                        if (connectionStatus == STATUS_CLOSE) {
                            return;
                        }
                    } finally {
                        STATUS_LOCK.unlock();
                    }
                }
                int messageType = -1;
                for (int i = 0; i < maxCntToReconnect; i++) {
                    try {
                        messageType = in.readInt();
                        Message newMessage = new Message(messageType);
                        newMessage.readMessage(in);
                        break;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (messageType == -1) {
                    STATUS_LOCK.lock();
                    try {
                        connectionStatus = STATUS_CLOSE;
                    } finally {
                        STATUS_LOCK.unlock();
                    }
                }
            }
        }
    };

    public static void connectToServer() {
        Thread initThread = new Thread() {
            @Override
            public void run() {
                STATUS_LOCK.lock();
                try {
                    connectionStatus = STATUS_IS_OPENING;
                    for (int i = 0; i < maxCntToReconnect; i++) {
                        if (tryToConnect()) {
                            connectionStatus = STATUS_OPEN;
                            break;
                        }
                        try {
                            sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connectionStatus == STATUS_IS_OPENING) {
                        connectionStatus = STATUS_CLOSE;
                    }
                    else {
                        readThread.start();
                    }
                } finally {
                    STATUS_LOCK.unlock();
                }
            }
        };
        initThread.start();
    }

    private static boolean tryToConnect() {
        try {
            InetAddress ipAddress = InetAddress.getByName(serverIP);
            Socket socket = new Socket(ipAddress, serverPort);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean tryToSendMessage(byte[] message) {
        try {
            out.write(message);
            out.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void sendMessage(final byte[] message) {
        messageWriter.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    STATUS_LOCK.lock();
                    try {
                        if (connectionStatus == STATUS_OPEN) {
                            break;
                        }
                        if (connectionStatus == STATUS_CLOSE) {
                            return;
                        }
                    } finally {
                        STATUS_LOCK.unlock();
                    }
                }
                boolean messageIsSent = false;
                for (int i = 0; i < maxCntToReconnect && !messageIsSent; i++) {
                    messageIsSent = tryToSendMessage(message);
                }
                if (!messageIsSent) {
                    STATUS_LOCK.lock();
                    try {
                        connectionStatus = STATUS_CLOSE;
                    } finally {
                        STATUS_LOCK.unlock();
                    }
                }
            }
        });
    }
}
