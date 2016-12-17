package ru.spbau.resemblance;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
    final public static int ROUND_END_TYPE = 13;
    final public static int RATING_TYPE = 14;

    private final String MESSAGE_LOG_TAG = "Message";

    private static volatile LoginMessageListener loginListener = null;
    private static volatile RegisterMessageListener registerListener = null;
    private static volatile RatingMessageListener ratingListener = null;
    private static volatile GameMessageListener gameListener = null;
    private static volatile GameExpectationMessageListener gameExpectationListener = null;

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
            case ROUND_END_TYPE:
                readRoundEndMessage(in);
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
        Log.d(MESSAGE_LOG_TAG, "Register message received.");
        int resultCode = -1;
        try {
            synchronized (in) {
                resultCode = in.readInt();
            }
            registerListener.onRegisterResponse(resultCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLoginMessage(DataInputStream in) {
        Log.d(MESSAGE_LOG_TAG, "Login message received.");
        int resultCode = -1;
        try {
            synchronized (in) {
                resultCode = in.readInt();
            }
            loginListener.onLoginResponse(resultCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readStartGameMessage(DataInputStream stream) {
        Log.d(MESSAGE_LOG_TAG, "Start game message received.");
        try {
            int roundsNumber = stream.readInt();
            int playersNumber = stream.readInt();
            ArrayList <String> names = new ArrayList<>();
            for (int i = 0; i < playersNumber; i++) {
                names.add(stream.readUTF());
            }
            gameExpectationListener.onStartGameMessage(roundsNumber, playersNumber, names);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readSendCardMessage(DataInputStream stream) {
        Log.d(MESSAGE_LOG_TAG, "Send card message received.");
        try {
            long card = stream.readLong();
            while (gameListener == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
            gameListener.onSendCard(card);
        } catch (IOException e) {}
    }

    private void readLeadRequestMessage(DataInputStream stream) {
        Log.d(MESSAGE_LOG_TAG, "Lead message received.");
        gameListener.onLeadRequest();
    }

    private void readChoiceRequestMessage(DataInputStream stream) {
        Log.d(MESSAGE_LOG_TAG, "Choice message received.");
        try {
            String association = stream.readUTF();
            gameListener.onChoiceRequest(association);
        } catch (IOException e) {}
    }

    private void readVoteRequestMessage(DataInputStream stream) {
        Log.d(MESSAGE_LOG_TAG, "Vote message received.");
        try {
            String association = stream.readUTF();
            int cardsNumber = stream.readInt();
            long[] candidates = new long[cardsNumber];
            for (int i = 0; i < cardsNumber; i++) {
                candidates[i] = stream.readLong();
            }
            //GameIntermediateActivity.vote(association, candidates);
            gameListener.onVoteRequest(association, candidates);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readRatingMessage(DataInputStream stream) {
        Log.d(MESSAGE_LOG_TAG, "Rating message received.");
        try {
            int rating = stream.readInt();
            ratingListener.onRatingMessage(rating);
        } catch (IOException e) {}
    }

    public void readRoundEndMessage(DataInputStream stream) {
        try {
            long leaderAssociation = stream.readLong();
            int[] scores = new int[stream.readInt()];
            for (int i = 0; i < scores.length; i++) {
                scores[i] = stream.readInt();
            }
            gameListener.onRoundEnd(leaderAssociation, scores);
        } catch (IOException e) {}
    }

    //----------------------------------------------------


    private void applyTest(String textMessage) {
        System.out.println(textMessage);
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
        //Log.d("asd", "1");

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

    protected static void setLoginListener(LoginMessageListener listener) {
        loginListener = listener;
    }

    protected static void setRegisterListener(RegisterMessageListener listener) {
        registerListener = listener;
    }

    protected static void setRatingListener(RatingMessageListener listener) {
        ratingListener = listener;
    }

    protected static void setGameListener(GameMessageListener listener) {
        gameListener = listener;
    }

    protected static void setGameExpectationListener(GameExpectationMessageListener listener) {
        gameExpectationListener = listener;
    }

    protected interface LoginMessageListener {
        void onLoginResponse(int code);
    }

    protected interface RegisterMessageListener {
        void onRegisterResponse(int code);
    }

    protected interface RatingMessageListener {
        void onRatingMessage(int rating);
    }

    protected interface GameMessageListener {
        void onSendCard(long card);

        void onLeadRequest();

        void onChoiceRequest(String association);

        void onVoteRequest(String association, long[] candidates);

        void onRoundEnd(long leadersAssociation, int[] scores);
    }

    protected interface GameExpectationMessageListener {
        void onStartGameMessage(int roundsNumber, int playersNumber, ArrayList<String> names);
    }
}
