package ru.spbau.resemblance;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class CardSetsActivity extends AppCompatActivity {
    final private static int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_sets);
    }

    public void onListOfSetsClick(View v) {
        Intent listOfSets = new Intent(this, ListOfSetsActivity.class);
        startActivity(listOfSets);
    }

    public void onAddSetsCardsClick(View v) {
        //TODO
    }

    public void onListOfCardsClick(View v) {
        Intent showCards = new Intent(this, GalleryActivity.class);
        ArrayList<ImageStorage.ImageWrapped> cards = ImageStorage.getAllImages();
        long[] cardIds = new long[cards.size()];
        for (int i = 0; i < cardIds.length; i++) {
            cardIds[i] = cards.get(i).getIdImage();
        }
        showCards.putExtra(GalleryActivity.CARDS_PARAM, cardIds);
        startActivity(showCards);
    }

    public void onAddCardsClick(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        ImageView imageView = (ImageView) new ImageView(this);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageView.setImageURI(selectedImage);
                    ImageStorage.addImageByUri(selectedImage.toString(), this);
                }
        }
    }
}
