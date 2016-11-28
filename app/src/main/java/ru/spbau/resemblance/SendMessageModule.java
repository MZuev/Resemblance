package ru.spbau.resemblance;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;


public class SendMessageModule {
    final private static String LOG_TAG = "Messager log";
    final private static int serverPort = 6662;
    final private static String address = "10.0.0.2";
    private static InetAddress ipAddress = null;
    final private static int maxNumberOfAttempts = 10;
    private static Socket socket = null;
    private static DataInputStream sin = null;
    private static DataOutputStream sout = null;

    public static boolean isOpen = false;

    static  {
        for (int i = 0; i < maxNumberOfAttempts && socket == null; i++) {
            try {

                ipAddress = InetAddress.getByName(address);
                socket  = new Socket(address, serverPort);
               // sin = new DataInputStream(socket.getInputStream());
               // sout = new DataOutputStream(socket.getOutputStream());
            }
            catch (Exception e) {
                Log.d(LOG_TAG, "Bad try to connect to " + address);
            }
        }
        if (socket != null) {
            Thread curThread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String textNewMessage = sin.readUTF();
                            Message newMessage = new Message(textNewMessage);
                            newMessage.applyMessage();
                        } catch (Exception e) {
                            Log.d(LOG_TAG, "Bad message from server");
                            //TODO
                        }
                    }
                }
            };
            curThread.start();
            isOpen = true;
        }
    }

    public static void sendMessage(String message) {
        if (message != null) {
            try {
                sout.writeUTF(message);
                sout.flush();
            }
            catch (Exception e) {
                Log.d(LOG_TAG, "Error of the connection to the server");
                //TODO;
            }
        }
    }

    public static void sendMessage(Message message) {
        if (message != null) {
            sendMessage(message.getStringMessage());
        }
    }
}
