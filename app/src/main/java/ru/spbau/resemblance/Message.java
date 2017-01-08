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
    final public static int CREATE_FIEND_GAME_TYPE = 15;
    final public static int JOIN_FRIEND_GAME_TYPE = 16;
    final public static int FRIEND_GAME_PLAYER_TYPE = 17;
    final public static int REMOVE_PLAYER_TYPE = 18;
    final public static int CANCEL_FRIEND_GAME_TYPE = 19;
    final public static int START_FRIEND_GAME_TYPE = 20;
    final public static int GAME_FINISH_TYPE = 21;
    final public static int GAME_CANCELED_TYPE = 22;
    final public static int QUIT_FIEND_GAME_TYPE = 23;
    final public static int PASSWORD_CHANGE_REQUEST_TYPE = 24;
    final public static int PASSWORD_CHANGE_RESPONSE_TYPE = 25;

    private final String MESSAGE_LOG_TAG = "Message";

    private static volatile LoginMessageListener loginListener = null;
    private static volatile RegisterMessageListener registerListener = null;
    private static volatile RatingMessageListener ratingListener = null;
    private static volatile GameMessageListener gameListener = null;
    private static volatile GameExpectationMessageListener gameExpectationListener = null;
    private static volatile FriendGamePreparationListener preparationListener = null;
    private static volatile PasswordChangeListener passwordChangeListener = null;

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
            case FRIEND_GAME_PLAYER_TYPE:
                readFriendGamePlayerMessage(in);
                break;
            case GAME_FINISH_TYPE:
                readGameFinishMessage(in);
                break;
            case GAME_CANCELED_TYPE:
                readGameCancelledMessage(in);
                break;
            case PASSWORD_CHANGE_RESPONSE_TYPE:
                readPasswordChangeMessage(in);
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
            while (gameExpectationListener == null) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {}
            }
            long expectationTime = stream.readLong();
            gameExpectationListener.onStartGameMessage(roundsNumber, playersNumber, names, expectationTime);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFriendGamePlayerMessage(DataInputStream stream) {
        try {
            boolean joined = stream.readBoolean();
            String playerName = stream.readUTF();
            Log.d(MESSAGE_LOG_TAG, "FriendGamePlayerMessage received: " + joined);
            while (preparationListener == null) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {}
            }
            if (joined) {
                preparationListener.onNewPlayer(playerName);
            } else {
                preparationListener.onGonePlayer(playerName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readGameFinishMessage(DataInputStream stream) {
        try {
            long lastAnswer = stream.readLong();
            int playersNumber = stream.readInt();
            ArrayList<Integer> finalScores = new ArrayList<>();
            for (int i = 0; i < playersNumber; i++) {
                finalScores.add(stream.readInt());
            }
            ArrayList<Integer> oldRatings = new ArrayList<>();
            for (int i = 0; i < playersNumber; i++) {
                oldRatings.add(stream.readInt());
            }
            ArrayList<Integer> newRatings = new ArrayList<>();
            for (int i = 0; i < playersNumber; i++) {
                newRatings.add(stream.readInt());
            }
            gameListener.onGameFinish(lastAnswer, finalScores, oldRatings, newRatings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readGameCancelledMessage(DataInputStream stream) {
        while (gameExpectationListener == null) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {}
        }
        gameExpectationListener.onGameCancelled();
    }

    private void readPasswordChangeMessage(DataInputStream stream) {
        try {
            int code = stream.readInt();
            passwordChangeListener.onPasswordChangeResponse(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void sendCreateGameMessage(int roundsNumber, ArrayList<ImageStorage.ImageWrapped> cards,
                                             long expectationTime) {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(150);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(CREATE_FIEND_GAME_TYPE);
            out.writeInt(roundsNumber);
            out.writeInt(cards.size());
            for (ImageStorage.ImageWrapped image: cards) {
                out.writeLong(image.getIdImage());
            }
            out.writeLong(expectationTime);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendMessageModule.sendMessage(byteOS.toByteArray());
    }

    public static void sendJoinFriendGameMessage(String gameCreatorName) {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(150);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(JOIN_FRIEND_GAME_TYPE);
            out.writeUTF(gameCreatorName);
            out.flush();
            SendMessageModule.sendMessage(byteOS.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendStartFriendGameMessage() {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(150);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(START_FRIEND_GAME_TYPE);
            out.flush();
            SendMessageModule.sendMessage(byteOS.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendCancelFriendGameMessage() {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(150);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(CANCEL_FRIEND_GAME_TYPE);
            out.flush();
            SendMessageModule.sendMessage(byteOS.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendRemovePlayerMessage(String playerName) {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(150);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(REMOVE_PLAYER_TYPE);
            out.writeUTF(playerName);
            out.flush();
            SendMessageModule.sendMessage(byteOS.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendQuitFriendGameMessage() {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(150);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(QUIT_FIEND_GAME_TYPE);
            out.flush();
            SendMessageModule.sendMessage(byteOS.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendPasswordChangeMessage(String oldPassword, String newPassword) {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream(150);
        DataOutputStream out = new DataOutputStream(byteOS);
        try {
            out.writeInt(PASSWORD_CHANGE_REQUEST_TYPE);
            out.writeUTF(oldPassword);
            out.writeUTF(newPassword);
            out.flush();
            SendMessageModule.sendMessage(byteOS.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    protected static void setFriendGamePreparationListener(FriendGamePreparationListener listener) {
        preparationListener = listener;
    }

    protected static void setPasswordChangeListener(PasswordChangeListener listener) {
        passwordChangeListener = listener;
    }

    protected static void unsetGameListener() {
        gameListener = null;
    }

    protected static void unSetGameExpectationListener() {
        gameExpectationListener = null;
    }

    protected static void unSetFriendGamePreparationListener() {
        preparationListener = null;
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

        void onGameFinish(long lastAnswer, ArrayList<Integer> finalScores,
                          ArrayList<Integer> oldRatings, ArrayList<Integer> newRatings);
    }

    protected interface GameExpectationMessageListener {
        void onStartGameMessage(int roundsNumber, int playersNumber, ArrayList<String> names,
                                long expectatioTime);

        void onGameCancelled();
    }

    protected interface FriendGamePreparationListener {
        void onNewPlayer(String name);

        void onGonePlayer(String name);
    }

    protected interface PasswordChangeListener {
        void onPasswordChangeResponse(int code);
    }
}
