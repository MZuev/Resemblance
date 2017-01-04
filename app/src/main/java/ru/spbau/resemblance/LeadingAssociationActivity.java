package ru.spbau.resemblance;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class LeadingAssociationActivity extends AppCompatActivity {
    public final static String IMAGE_PARAM = "image";

    private EditText associationField;
    private ImageStorage.ImageWrapped image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leading_association);

        associationField = (EditText)findViewById(R.id.leadingAssociationField);
        ImageView imageView = (ImageView) findViewById(R.id.leadingAssociationImage);

        image = ImageStorage.ImageWrapped.createById((int)getIntent().getLongExtra(IMAGE_PARAM, -1L));
        imageView.setImageURI(Uri.parse(image.getUriImage()));
    }

    public void onDoneClick(View v) {
        Intent ret = new Intent();
        ret.putExtra(LeadingCardsGridActivity.PICTURE_PARAM, (long)image.getIdImage());
        ret.putExtra(LeadingCardsGridActivity.ASSOCIATION_PARAM, associationField.getText().toString());
        setResult(RESULT_OK, ret);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent ret = new Intent();
        ret.putExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1);
        ret.putExtra(LeadingCardsGridActivity.ASSOCIATION_PARAM, (String)null);
        setResult(RESULT_OK, ret);
        finish();
    }
}
