package ru.spbau.resemblance;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SendMessageModule {
    final private static String LOG_TAG = "Messenger log";
    final private static int maxCntToReconnect = 20;

    final private static String serverIP = "192.168.1.14";
    final private static int serverPort = 6662;

    private final static int sleepTime = 1000;

    private static Socket socket = null;
    private static DataInputStream in = null;
    private static DataOutputStream out = null;

    final private static int STATUS_NOT_OPEN = 0;
    final private static int STATUS_IS_OPENING = 1;
    final private static int STATUS_OPEN = 2;
    final private static int STATUS_CLOSE = 3;

    private static Integer connectionStatus = STATUS_NOT_OPEN;
    private static Executor messageWriter;

    public static boolean isAlive() {
        synchronized (connectionStatus) {
            return connectionStatus == STATUS_OPEN;
        }
    }

    private static Thread readThread = new Thread() {
        @Override
        public void run() {
            while (true) {
                while (true) {
                    synchronized (connectionStatus) {
                        if (connectionStatus == STATUS_OPEN) {
                            break;
                        }
                        if (connectionStatus == STATUS_CLOSE) {
                            return;
                        }
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
                    synchronized (connectionStatus) {
                        connectionStatus = STATUS_CLOSE;
                    }
                }
            }
        }
    };

    public static void connectToServer() {
        Thread initThread = new Thread() {
            @Override
            public void run() {
                synchronized (connectionStatus) {
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
                }
            }
        };
        initThread.start();
        messageWriter = Executors.newSingleThreadExecutor();
    }

    private static boolean tryToConnect() {
        try {
            InetAddress ipAddress = InetAddress.getByName(serverIP);
            socket = new Socket(ipAddress, serverPort);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean tryToSendMessage(byte[] message) {
        try{
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
                    synchronized (connectionStatus) {
                        if (connectionStatus == STATUS_OPEN) {
                            break;
                        }
                        if (connectionStatus == STATUS_CLOSE) {
                            return;
                        }
                    }
                }
                boolean messageIsSent = false;
                for (int i = 0; i < maxCntToReconnect && !messageIsSent; i++) {
                    messageIsSent = tryToSendMessage(message);
                }
                if (!messageIsSent) {
                    synchronized (connectionStatus) {
                        connectionStatus = STATUS_CLOSE;
                    }
                }
            }
        });
    }
}
