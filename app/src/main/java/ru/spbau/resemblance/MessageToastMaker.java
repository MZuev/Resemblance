package ru.spbau.resemblance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class MessageToastMaker extends BroadcastReceiver {
    public static final String TOAST_TEXT_PARAM = "toast_text";

    private final Context context;
    private final String message;

    MessageToastMaker(Context context, String message) {
        this.context = context;
        this.message = message;
        IntentFilter filter = new IntentFilter(message);
        LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
    }

    public void showToast(String text) {
        Intent showText = new Intent(message);
        showText.putExtra(MessageToastMaker.TOAST_TEXT_PARAM, text);
        LocalBroadcastManager.getInstance(context).sendBroadcast(showText);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getStringExtra(TOAST_TEXT_PARAM), Toast.LENGTH_SHORT).show();
    }

    public void close() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }
}
