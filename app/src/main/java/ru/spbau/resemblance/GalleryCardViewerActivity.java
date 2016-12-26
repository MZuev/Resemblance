package ru.spbau.resemblance;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class GalleryCardViewerActivity extends AppCompatActivity {
    public final static String IMAGE_PARAM = "image";

    private ImageView imageView;
    private ImageStorage.ImageWrapped image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_card_ciewer);

        imageView = (ImageView)findViewById(R.id.cardViewerImage);

        image = ImageStorage.ImageWrapped.createById((int)getIntent().getLongExtra(IMAGE_PARAM, -1L));
        imageView.setImageURI(Uri.parse(image.getUriImage()));
    }
}
