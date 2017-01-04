package ru.spbau.resemblance;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

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
