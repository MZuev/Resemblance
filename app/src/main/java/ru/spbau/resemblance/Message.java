package ru.spbau.resemblance;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

public class Message {
    final public static int TEST_TYPE = 0;
    final public static int REGISTER_TYPE = 1;
    final public static int LOGIN_TYPE = 2;
    final public static int JOIN_RANDOM_GAME_TYPE = 3;
    final public static int QUIT_RANDOM_GAME_TYPE = 4;
    final public static int START_GAME_TYPE = 5;
    final public static int SEND_CARD_TYPE = 6;
    final public static int LEAD_REQUEST_TYPE = 7;
    final public static int LEAD_ASSOCIATION_TYPE = 8;
    final public static int CHOICE_REQUEST_TYPE = 9;
    final public static int CHOICE_TYPE = 10;
    final public static int VOTE_REQUEST_TYPE = 11;
    final public static int VOTE_TYPE = 12;
    final public static int LEADERS_ASSOCIATION_TYPE = 13;
    final public static int RATING_TYPE = 14;

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
            case START_GAME_TYPE:
                readStartGameMessage(in);
                break;
            case SEND_CARD_TYPE:
                readSendCardMessage(in);
                break;
            case LEAD_REQUEST_TYPE:
                readLeadRequestMessage(in);
                break;
            case CHOICE_REQUEST_TYPE:
                readChoiceRequestMessage(in);
                break;
            case VOTE_REQUEST_TYPE:
                readVoteRequestMessage(in);
                break;
            case LEADERS_ASSOCIATION_TYPE:
                readLeadersAssociationMessage(in);
                break;
            case RATING_TYPE:
                readRatingMessage(in);
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
        Log.d("asd", "2");
        int resultCode = -1;
        try {
            synchronized (in) {
                resultCode = in.readInt();
            }
            applyRegister(resultCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLoginMessage(DataInputStream in) {
        int resultCode = -1;
        long hashPassword = 0;
        try {
            synchronized (in) {
                resultCode = in.readInt();
            }
            applyLogin(resultCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readStartGameMessage(DataInputStream stream) {
        GameExpectationActivity.startGame();
    }

    private void readSendCardMessage(DataInputStream stream) {
        try {
            long newCard = stream.readLong();
            GameIntermediateActivity.addCard(newCard);
        } catch (IOException e) {}
    }

    private void readLeadRequestMessage(DataInputStream stream) {
        Log.d("Message", "readLeadRequestMessage: ");
        GameIntermediateActivity.lead();
    }

    private void readChoiceRequestMessage(DataInputStream stream) {
        try {
            String association = stream.readUTF();
            GameIntermediateActivity.chooseCard(association);
        } catch (IOException e) {}
    }

    private void readVoteRequestMessage(DataInputStream stream) {
        Log.d("Message", "vote");
        try {
            String association = stream.readUTF();
            int cardsNumber = stream.readInt();
            long[] candidates = new long[cardsNumber];
            for (int i = 0; i < cardsNumber; i++) {
                candidates[i] = stream.readLong();
            }
            GameIntermediateActivity.vote(association, candidates);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readRatingMessage(DataInputStream stream) {
        try {
            int newRating = stream.readInt();
            //TODO process
            //SharedPreferences prefs = getContext().getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
            //SharedPreferences.Editor editor = prefs.edit();
            //editor.putInt(SettingsActivity.RATING_PREF, newRating);
            //editor.commit();
        } catch (IOException e) {}
    }

    public void readLeadersAssociationMessage(DataInputStream stream) {
        try {
            long leaderAssociation = stream.readInt();
            //TODO
        } catch (IOException e) {}
    }

    //----------------------------------------------------


    private void applyTest(String textMessage) {
        System.out.println(textMessage);
    }

    private void applyRegister(int resultCode) {
        /*
        Log.d("asd", "3");
        Log.d("qwe", "" + resultCode);
        final int networkError = -1;
        final int successfulRegistration = 0;
        final int nicknameError = 1;

        switch (resultCode) {
            case successfulRegistration:
                //// TODO: 05.12.2016
                break;
            case networkError:
                // TODO: 05.12.2016
                break;
            case nicknameError:
                // TODO: 05.12.2016
                break;
        }
        */
        RegistrationActivity.onResponse(resultCode);
    }

    private void applyLogin(int resultCode) {
        /*
        final int networkError = -1;
        final int successfulLogin = 0;
        final int nicknameError = 1;
        final int passwordError = 2;

        switch (resultCode) {
            case successfulLogin:
                //// TODO: 05.12.2016
                break;
            case networkError:
                // TODO: 05.12.2016
                break;
            case nicknameError:
                // TODO: 05.12.2016
                break;
            case passwordError:
                // TODO: 05.12.2016
                break;
        }
        */
        LoginActivity.onResponse(resultCode);
    }

    //----------------------------------------------------

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

    public static void sendJoinRandomGameMessage() {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(100);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(JOIN_RANDOM_GAME_TYPE);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessageModule.sendMessage(byteOS.toByteArray());
    }

    public static void sendQuitRandomGameMessage() {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(100);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(QUIT_RANDOM_GAME_TYPE);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessageModule.sendMessage(byteOS.toByteArray());
    }

    public static void sendLeadAssociationMessage(long card, String association) {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(100);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(LEAD_ASSOCIATION_TYPE);
            out.writeLong(card);
            out.writeUTF(association);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessageModule.sendMessage(byteOS.toByteArray());
    }

    public static void sendChoiceMessage(long card) {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(100);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(CHOICE_TYPE);
            out.writeLong(card);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessageModule.sendMessage(byteOS.toByteArray());
    }

    public static void sendVoteMessage(long card) {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(100);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(VOTE_TYPE);
            out.writeLong(card);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessageModule.sendMessage(byteOS.toByteArray());
    }

    public static void sendLoginMessage(String nickname, String password) {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(150);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(LOGIN_TYPE);
            out.writeUTF(nickname);
            out.writeUTF(password);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessageModule.sendMessage(byteOS.toByteArray());
    }

    public static void sendRegisterMessage(String nickname, String password) {
        Log.d("asd", "1");

        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(150);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(REGISTER_TYPE);
            out.writeUTF(nickname);
            out.writeUTF(password);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessageModule.sendMessage(byteOS.toByteArray());
    }
}
