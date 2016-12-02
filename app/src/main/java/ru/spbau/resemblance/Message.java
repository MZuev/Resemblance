package ru.spbau.resemblance;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message {
    final private static String LOG_TAG = "Message";

    final public static int TEST_TYPE = 0;
    final public static int REGISTER_TYPE = 1;
    final public static int LOGIN_TYPE = 2;

    private int type = 0;

    Message(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int newType) {
        type = newType;
    }

    public void readMessage(DataInputStream in) {
        switch (type) {
            case TEST_TYPE:
                readTestMessage(in);
                break;
            case REGISTER_TYPE:
                readRegisterMessage(in);
                break;
            case LOGIN_TYPE:
                readLoginMessage(in);
                break;
        }
    }

    private void readTestMessage(DataInputStream in) {
        String testMessage = null;
        try {
            synchronized (in) {
                testMessage = in.readUTF();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        applyTest(testMessage);
    }

    private void readRegisterMessage(DataInputStream in) {
        String login = null;
        long hashPassword = 0;
        try {
            synchronized (in) {
                login = in.readUTF();
                hashPassword = in.readLong();
            }
            //applyRegister(login, hashPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLoginMessage(DataInputStream in) {
        String login = null;
        long hashPassword = 0;
        try {
            synchronized (in) {
                login = in.readUTF();
                hashPassword = in.readLong();
            }
            //applyLogin(login, hashPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyTest(String textMessage) {
        Log.d(LOG_TAG, textMessage);
    }

    public void sendTestMessage(String textMessage) {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(100);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(TEST_TYPE);
            out.writeUTF(textMessage);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessageModule.sendMessage(byteOS.toByteArray());
    }
}
