package ru.spbau.resemblance;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;

public class CreateSetCardsActivity extends AppCompatActivity {
    final private static int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_set_cards);
    }

    public void onListOfSetsClick(View v) {
        Intent listOfSets = new Intent(this, ListOfSetsActivity.class);
        startActivity(listOfSets);
    }

    public void onAddSetsCardsClick(View v) {
        //TODO
    }

    public void onListOfCardsClick(View v) {
        /*
        Intent showCards = new Intent(this, ShowImageFromListActivity.class);
        ArrayList<ImageStorage.ImageWrapped> listImages = ImageStorage.getAllImages();
        ArrayList<Integer> idImagesList = new ArrayList<Integer>();
        for (ImageStorage.ImageWrapped curImage : listImages) {
            idImagesList.add(curImage.getIdImage());
        }
        showCards.putExtra("listImage", idImagesList);
        startActivity(showCards);
        */
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

        Log.d("asd", "a");
        switch (requestCode) {
            case GALLERY_REQUEST:
                Log.d("asd", "b");
                if (resultCode == RESULT_OK) {
                    Log.d("asd", "c");
                    Uri selectedImage = imageReturnedIntent.getData();
                    Log.d("asd", "d");
                        imageView.setImageURI(selectedImage);
                        Log.d("asd", "e");

                    Log.d("asd", "f");
                    ImageStorage.addImageByUri(selectedImage.toString(), this);
                    Log.d("asd", "g");
                }
        }
    }

}
