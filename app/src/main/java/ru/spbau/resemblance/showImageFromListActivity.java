package ru.spbau.resemblance;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

public class ShowImageFromListActivity extends AppCompatActivity {
    final static int maxColumn = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_from_list);
        TableLayout showImageTable = (TableLayout)findViewById(R.id.showImageTable);
        Intent curIntent = getIntent();
        int curColumn = 0;
        //TableRow.LayoutParams rowLParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        TableLayout.LayoutParams tableLParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        ArrayList<Integer> allIdImageList = curIntent.getIntegerArrayListExtra("listImage");
        TableRow curTableRow = new TableRow(this);
        showImageTable.addView(curTableRow);
        for (Integer curIdImage : allIdImageList) {
            ImageStorage.ImageWrapped curImage = ImageStorage.ImageWrapped.createById(curIdImage);
            ImageView imageView = curImage.getImageView(this);
            if (curColumn == maxColumn) {
                curTableRow = new TableRow(this);
                showImageTable.addView(curTableRow, tableLParams);
                curColumn = 0;
            }
            curTableRow.addView(imageView);
            imageView.requestLayout();
            imageView.getLayoutParams().width = 200;
            imageView.getLayoutParams().height = 200;
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            curColumn++;
        }
    }
}
