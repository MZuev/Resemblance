package ru.spbau.resemblance;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

public class ShowImageFromListActivity extends AppCompatActivity {
    final static int maxColumn = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_from_list);
        LinearLayout vertLayout = (LinearLayout)findViewById(R.id.showImageVerticalLayout);
        Intent curIntent = getIntent();
        int curColumn = 0;
        //TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.3f);
        ArrayList<Integer> allIdImageList = curIntent.getIntegerArrayListExtra("listImage");
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        //LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        LinearLayout curLinear = new LinearLayout(this);
        vertLayout.addView(curLinear, params1);
        for (Integer curIdImage : allIdImageList) {
            ImageStorage.ImageWrapped curImage = ImageStorage.ImageWrapped.createById(curIdImage);
            ImageView imageView = curImage.getImageView(this);
            if (curColumn == maxColumn) {
                curLinear = new LinearLayout(this);
                vertLayout.addView(curLinear, params1);
                curColumn = 0;
            }
            curLinear.addView(imageView, params1);
            imageView.requestLayout();
            //imageView.setMaxWidth(100);
            imageView.setClickable(true);
           // imageView.setMaxHeight(100);
            //imageView.getLayoutParams().width = 200;
            //imageView.getLayoutParams().height = 200;
            //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            curColumn++;
        }
    }
}
