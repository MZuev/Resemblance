package ru.spbau.resemblance;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

//This is an activity for manipulations with cards and sets

public class CardSetsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    final private static int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_sets);

        ListView optionsList = (ListView)findViewById(R.id.cardSetsOptionsList);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.cards_control_options));

        optionsList.setAdapter(adapter);
        optionsList.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                Intent listOfSets = new Intent(this, ListOfSetsActivity.class);
                startActivity(listOfSets);
                break;
            }
            case 1: {
                Intent showCards = new Intent(this, GalleryActivity.class);
                ArrayList<ImageStorage.ImageWrapped> cards = ImageStorage.getAllImages();
                long[] cardIds = new long[cards.size()];
                for (int i = 0; i < cardIds.length; i++) {
                    cardIds[i] = cards.get(i).getIdImage();
                }
                showCards.putExtra(GalleryActivity.CARDS_PARAM, cardIds);
                startActivity(showCards);
                break;
            }
            case 2: {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                break;
            }
        }
    }
}
