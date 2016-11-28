package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ListOfSetsActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_sets);

        int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
        int matchParent = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayout mainLL = (LinearLayout)findViewById(R.id.ListOfCardsLayout);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(matchParent, wrapContent);

        ArrayList<ImageStorage.SetCardsWrapped> listSetCards = ImageStorage.getAllSetsCards();

        final ListOfSetsActivity curActivity = this;
        for (final ImageStorage.SetCardsWrapped curSet : listSetCards) {
            Button newSetTxtView = new Button(this);
            newSetTxtView.setText("Название: " + curSet.getNameSetCards() + ", размер: " + curSet.getSizeSetCards());
            newSetTxtView.setGravity(Gravity.CENTER_HORIZONTAL);
            newSetTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent showCards = new Intent(curActivity, ShowImageFromListActivity.class);
                    ArrayList<ImageStorage.ImageWrapped> listImages = curSet.getListOfCards();
                    ArrayList<Integer> idImagesList = new ArrayList<Integer>();
                    for (ImageStorage.ImageWrapped curImage : listImages) {
                        idImagesList.add(curImage.getIdImage());
                    }
                    showCards.putExtra("listImage", idImagesList);
                    startActivity(showCards);
                }
            });
            mainLL.addView(newSetTxtView, lParams);
        }
    }
}
