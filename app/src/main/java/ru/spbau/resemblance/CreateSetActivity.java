package ru.spbau.resemblance;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;

public class CreateSetActivity extends AppCompatActivity {
    private final static int GALLERY_REQUEST = 1;
    private final ArrayList<ImageStorage.ImageWrapped> cards = new ArrayList<>();
    private CardsAdapter adapter;
    private final static int COLUMNS_NUMBER = 3;
    private EditText nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_set);

        adapter = new CardsAdapter(this, new ArrayList<ImageStorage.ImageWrapped>(),
                getResources().getDisplayMetrics().widthPixels / COLUMNS_NUMBER);
        GridView cardsGrid = (GridView)findViewById(R.id.createSetCardsGrid);
        cardsGrid.setNumColumns(COLUMNS_NUMBER);
        cardsGrid.setAdapter(adapter);
        nameField = (EditText)findViewById(R.id.createSetNameField);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    ImageStorage.ImageWrapped newCard =
                            ImageStorage.addImageByUri(selectedImage.toString(), this);
                    new ImageView(this).setImageURI(selectedImage);
                    cards.add(newCard);
                    adapter.addImage(newCard);
                }
        }
    }

    public void onAddCardClick(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    public void onDoneClick(View v) {
        ImageStorage.SetCardsWrapped setCards = new ImageStorage.SetCardsWrapped();
        setCards.setName(nameField.getText().toString());
        setCards.addSetCards();
        for (int i = 0; i < cards.size(); i++) {
            setCards.addCardToSet(cards.get(i));
        }
        finish();
    }
}
