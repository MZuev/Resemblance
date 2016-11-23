package ru.spbau.resemblance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ListOfSetsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_sets);

        int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
        int matchParent = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayout mainLL = (LinearLayout)findViewById(R.id.ListOfCardsLayout);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(matchParent, wrapContent);

        ArrayList<ImageStorage.SetCardsWrapped> listSetCards = ImageStorage.getAllSetsCards();
        for (ImageStorage.SetCardsWrapped curSet : listSetCards) {
            TextView newSetTxtView = new TextView(this);
            newSetTxtView.setText(curSet.getNameSetCards() + ", размер: " + curSet.getSizeSetCards());
            mainLL.addView(newSetTxtView, lParams);
        }
    }
}
