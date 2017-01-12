package ru.spbau.resemblance;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Chat {
    private static final ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
    private static volatile Activity activity;
    private static final Lock activityLock = new ReentrantLock();

    private Chat(){}

    private static final BaseAdapter ADAPTER = new BaseAdapter() {
        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int position) {
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView != null) {
                return convertView;
            }

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            ChatMessage message = messages.get(position);
            View messageView = inflater.inflate(R.layout.message_list_item, parent, false);
            TextView authorView = (TextView) messageView.findViewById(R.id.messageAuthor);
            TextView timeView = (TextView) messageView.findViewById(R.id.messageTime);
            TextView textView = (TextView) messageView.findViewById(R.id.messageText);
            authorView.setText(message.getAuthor());
            timeView.setText(message.getTime());
            textView.setText(message.getText());
            if (message.isOurs()) {
                LinearLayout head = (LinearLayout) messageView.findViewById(R.id.messageHeadline);
                head.setGravity(Gravity.END);
                textView.setGravity(Gravity.RIGHT);
            }
            return messageView;
        }
    };

    public static BaseAdapter setActivity(Activity newActivity) {
        activityLock.lock();
        try {
            activity = newActivity;
        } finally {
            activityLock.unlock();
        }
        return ADAPTER;
    }

    public static void unsetActivity() {
        activityLock.lock();
        try {
            activity = null;
        } finally {
            activityLock.unlock();
        }
    }

    public static void receiveMessage(String author, String time, String text) {
        messages.add(new ChatMessage(author, time, text));
        activityLock.lock();
        try {
            if (activity != null) {
                activity.runOnUiThread(updateScreen);
            }
        } finally {
            activityLock.unlock();
        }
    }

    public static void sendMessage(String ourName, String text) {
        ChatMessage message = new ChatMessage(ourName, text);
        messages.add(message);
        activityLock.lock();
        try {
            if (activity != null) {
                activity.runOnUiThread(updateScreen);
            }
        } finally {
            activityLock.unlock();
        }
        Message.sendChatMessage(message);
    }

    public static void clear() {
        messages.clear();
    }

    public static class ChatMessage {
        private final String author;
        private final String time;
        private final String text;
        private final boolean ours;

        ChatMessage(String author, String time, String text) {
            this.author = author;
            this.time = time;
            this.text = text;
            ours = false;
        }

        ChatMessage(String ourName, String text) {
            author = ourName;
            Calendar calendar = Calendar.getInstance();
            time = " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" +
                    calendar.get(Calendar.SECOND);
            this.text = text;
            ours = true;
        }

        public String getAuthor() {
            return author;
        }

        public String getTime() {
            return time;
        }

        public String getText() {
            return text;
        }

        public boolean isOurs() {
            return ours;
        }
    }

    private static Runnable updateScreen = new Runnable() {
        @Override
        public void run() {
            ADAPTER.notifyDataSetChanged();
        }
    };
}
