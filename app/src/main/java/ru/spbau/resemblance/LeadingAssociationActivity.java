package ru.spbau.resemblance;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;

public class LeadingAssociationActivity extends AppCompatActivity {
    public final static String IMAGE_PARAM = "image";

    private Button doneButton;
    private EditText associationField;
    private ImageView imageView;
    private ImageStorage.ImageWrapped image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leading_association);

        doneButton = (Button)findViewById(R.id.leadingAssociationButton);
        associationField = (EditText)findViewById(R.id.leadingAssociationField);
        imageView = (ImageView)findViewById(R.id.leadingAssociationImage);

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
        String nullString = null;
        ret.putExtra(LeadingCardsGridActivity.ASSOCIATION_PARAM, nullString);
        setResult(RESULT_OK, ret);
        finish();
    }
}
