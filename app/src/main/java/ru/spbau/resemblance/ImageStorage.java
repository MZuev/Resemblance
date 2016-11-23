package ru.spbau.resemblance;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


public class ImageStorage {
    final private static String LOG_TAG = "DBLog";
    final private static String IMAGE_TABLE = "imageTable";
    final private static String SET_CARDS_TABLE = "setCardsTable";
    final private static String MAP_TABLE = "mapSetAndImage";
    final private static long baseInHash = 257;
    private static byte[] imageBuffer = null;

    private ImageStorage(){}

    private static ImageDB imageDB = null;

    public static void createImageStorage(Context context) {
        if (imageDB == null) {
            imageDB = new ImageDB(context);
        }
        if (imageBuffer == null) {
            imageBuffer = new byte[context.getResources().getInteger(R.integer.maxImageSize)];
        }
        SetCardsWrapped setCards = new SetCardsWrapped();
        setCards.setNameSetCards("testSet");
        setCards.addSetCards();
        for (int i = 1; i < 9; i++) {
            int curId = context.getResources().getIdentifier("a" + i,"drawable", context.getPackageName());
            Resources resources = context.getResources();
            ImageWrapped curImage = addImageByUri(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(curId) + '/' + resources.getResourceTypeName(curId) + '/' + resources.getResourceEntryName(curId), context);
            setCards.addCardToSet(curImage);
        }
    }

    private static long calcHashByUri(String uriImage, Context context) {
        long hashImage = 0;
        try {
            Uri uri = Uri.parse(uriImage);
            InputStream stream = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
            int len = stream.read(imageBuffer);
            hashImage = uriImage.hashCode();
            for (int i = 0; i < len; i++) {
                hashImage *= baseInHash;
                hashImage += imageBuffer[i];
            }
            //TODO
        }
        catch (Exception e) {
            return 0;
        }

        return hashImage;
    }

     private static class ImageDB extends SQLiteOpenHelper{

        private ImageDB(Context context) {
            super(context, "imageDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "  ---  Create DB  ---  ");
            db.execSQL("create table " + IMAGE_TABLE + " (id integer primary key autoincrement, uri text, hash integer);");
            Log.d(LOG_TAG, "asd");
            db.execSQL("create table " + SET_CARDS_TABLE + " (id integer primary key autoincrement, hash integer, name text, size integer);");
            Log.d(LOG_TAG, "asd");
            db.execSQL("create table " + MAP_TABLE + " (idSet integer, idImage integer);");
            Log.d(LOG_TAG, "asd");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //TODO
        }
    }

    public static ImageWrapped addImageByUri(String uriImage, Context context) {
        ImageWrapped newImage = new ImageWrapped();
        long hashImage = 0;
        for (int cnt = 0; cnt < 3 && hashImage == 0; cnt++) {
            hashImage = calcHashByUri(uriImage, context);
        }
        if (hashImage == 0) {
            Log.d(LOG_TAG, " -- Bad Uri: " + uriImage + " -- ");
            return null;
        }
        newImage.setHashImage(hashImage);
        newImage.setNotEmpty();
        newImage.setUriImage(uriImage);
        newImage.addImage();
        return newImage;
    }

    private static boolean checkExistenceByHash(long hashObject, String nameTable) {
        return checkExistenceByStringField(String.valueOf(hashObject), "hash", nameTable);
    }

    private static boolean checkExistenceByStringField(String valueField, String nameField, String nameTable) {
        if (valueField == null || nameField == null) {
            return false;
        }
        return checkExistenceByStringField(nameField + " = '" + valueField + "'", nameTable);
    }
    private static boolean checkExistenceByStringField(String nameAndValueField, String nameTable ) {
        if (nameAndValueField == null) {
            return false;
        }
        Cursor c = imageDB.getReadableDatabase().query(nameTable, null, nameAndValueField, null, null, null,null);
        boolean ans = c.moveToFirst();
        c.close();
        return ans;
    }

    private static Cursor getImageInfoByHash(long imageHash) {
        Log.d(LOG_TAG, " -- Get image info by Hash -- ");
        return imageDB.getReadableDatabase().query(IMAGE_TABLE, null, "hash = " + imageHash, null, null, null, null);
    }

    private static Cursor getSetCardsInfoByHash(long setCardsHash) {
        Log.d(LOG_TAG, " -- Get set cards info by Hash -- ");
        return imageDB.getReadableDatabase().query(SET_CARDS_TABLE, null, "hash = " + setCardsHash, null, null, null, null);
    }

    private static Cursor getIdCardsByIdSet(long idSetCards) {
        return imageDB.getReadableDatabase().query(MAP_TABLE, null, "idSet = " + idSetCards, null, null, null, null);
    }

    private static Cursor getIdCardsByIdImage(long idImage) {
        return imageDB.getReadableDatabase().query(MAP_TABLE, null, "idImage = " + idImage, null, null, null, null);
    }

    private static long getHashImageById(long idImage) {
        Cursor c = imageDB.getReadableDatabase().query(IMAGE_TABLE, null, "id = " + idImage, null, null, null, null);
        long hashImage = c.getLong(c.getColumnIndex("hash"));
        c.close();
        return hashImage;
    }


    public static class ImageWrapped{
        private long hashImage = 0;
        private String uriImage = null;
        private long idImage = 0;
        private boolean isEmptyImage = true;

        ImageWrapped(){}

        public ImageWrapped(long newHash) {
            hashImage = newHash;
        }

        private ImageWrapped(Cursor c) {
            hashImage = c.getLong(c.getColumnIndex("hash"));
            uriImage = c.getString(c.getColumnIndex("uri"));
            idImage = c.getLong(c.getColumnIndex("id"));
            isEmptyImage = false;
        }

        public  void setHashImage(long newHash) {
            hashImage = newHash;
        }

        public void setUriImage(String add) {
            uriImage = add;
        }

        public void setIdImage(long id) {
            idImage = id;
        }

        public void setNotEmpty() { isEmptyImage = false; }

        public boolean checkExistenceImage() {
            return !isEmptyImage && (checkExistenceByHash(hashImage, IMAGE_TABLE) || checkExistenceByStringField(uriImage, "uri", IMAGE_TABLE));
        }

        private void setImageInfoByHash() {
            if (!isEmptyImage) {
                return;
            }
            Cursor c = getImageInfoByHash(hashImage);
            if (c.moveToFirst()) {
                uriImage = c.getString(c.getColumnIndex("uri"));
                idImage = c.getLong(c.getColumnIndex("id"));
                c.close();
                isEmptyImage = false;
            }
            else {
                Log.d(LOG_TAG, "Wrong image hash " + hashImage);
            }
            imageDB.close();
        }

        public ImageView getViewImage(Context context) {
            if (!checkExistenceImage()) {
                return null;
            }
            if (isEmptyImage) {
                setImageInfoByHash();
            }
            ImageView imageView = new ImageView(context);
            imageView.setImageURI(Uri.parse(uriImage));
            return imageView;
        }

        public void addImage() {
            if (checkExistenceImage()) {
                return;
            }
            ContentValues cv = new ContentValues();
            cv.put("uri", uriImage);
            cv.put("hash", hashImage);
            setIdImage(imageDB.getWritableDatabase().insert(IMAGE_TABLE, null, cv));
            imageDB.close();
        }

        public void deleteImage() {
            if (!checkExistenceImage()) {
                return;
            }
            if (isEmptyImage) {
                setImageInfoByHash();
            }
            imageDB.getWritableDatabase().delete(IMAGE_TABLE, "hash = " + hashImage, null);
            imageDB.getWritableDatabase().delete(MAP_TABLE, "idImage = " + idImage, null);
            imageDB.close();
        }
    }

    public static class SetCardsWrapped{
        private long idSetCards = 0;
        private long hashSetCards = 0;
        private String nameSetCards = null;
        private int sizeSetCards = 0;
        private boolean isEmptySetCards = true;

        SetCardsWrapped(){}

        SetCardsWrapped(long newHash) {
            hashSetCards = newHash;
        }

        private SetCardsWrapped(Cursor c) {
            hashSetCards = c.getLong(c.getColumnIndex("hash"));
            idSetCards = c.getLong(c.getColumnIndex("id"));
            nameSetCards = c.getString(c.getColumnIndex("name"));
            sizeSetCards = c.getInt(c.getColumnIndex("size"));
            isEmptySetCards = false;
        }

        public  void setHashSetCards(long newHash) {
            hashSetCards = newHash;
        }

        public void setIdSetCards(long id) {
            idSetCards = id;
        }

        public void setNameSetCards(String name) {
            nameSetCards = name;
        }

        public void setSizeSetCards(int size) {
            sizeSetCards = size;
        }

        public void setNotEmpty() { isEmptySetCards = false; }

        public String getNameSetCards() {
            return nameSetCards;
        }

        public int getSizeSetCards() {
            return sizeSetCards;
        }

        public boolean checkExistenceSet() {
            return !isEmptySetCards && (checkExistenceByHash(hashSetCards, SET_CARDS_TABLE) || checkExistenceByStringField(nameSetCards, "name", SET_CARDS_TABLE));
        }

        private void setSetCardsInfoByHash() {
            if (!isEmptySetCards) {
                return;
            }
            Cursor c = getImageInfoByHash(hashSetCards);
            if (c.moveToFirst()) {
                idSetCards = c.getLong(c.getColumnIndex("id"));
                nameSetCards = c.getString(c.getColumnIndex("name"));
                sizeSetCards = c.getInt(c.getColumnIndex("size"));
                c.close();
                isEmptySetCards = false;
            }
            else {
                Log.d(LOG_TAG, "Wrong set cards hash " + hashSetCards);
            }
            imageDB.close();
        }

        boolean checkExistenceAllCardsOfSet() {
            Log.d(LOG_TAG, " -- Calc set cards hash -- ");
            if (!checkExistenceSet()){
                Log.d(LOG_TAG, " -- Set is not found -- ");
                return false;
            }
            if (isEmptySetCards) {
                setSetCardsInfoByHash();
            }
            Cursor c = getIdCardsByIdSet(idSetCards);
            long sumImageHash = 0;
            int cntFound = 0;
            if (c.moveToFirst()) {
                do {
                    long idImage = c.getLong(c.getColumnIndex("idImage"));
                    sumImageHash += getHashImageById(idImage);
                    ++cntFound;
                } while (c.moveToNext());
                c.close();
            }
            imageDB.close();
            return sumImageHash == hashSetCards && cntFound == sizeSetCards;
        }

        public void updateCardsSet() {
            Cursor c = getIdCardsByIdSet(idSetCards);
            long sumImageHash = 0;
            int cntFound = 0;
            if (c.moveToFirst()) {
                do {
                    long idImage = c.getLong(c.getColumnIndex("idImage"));
                    sumImageHash += getHashImageById(idImage);
                    ++cntFound;
                } while (c.moveToNext());
                c.close();
            }
            hashSetCards = sumImageHash;
            sizeSetCards = cntFound;
            ContentValues cv = new ContentValues();
            cv.put("hash", hashSetCards);
            cv.put("size", sizeSetCards);
            imageDB.getWritableDatabase().update(SET_CARDS_TABLE, cv, "id = " + idSetCards, null);
            imageDB.close();
        }

        public void addSetCards() {
            if (checkExistenceSet()) {
                return;
            }
            setNotEmpty();
            ContentValues cv = new ContentValues();
            cv.put("hash", hashSetCards);
            cv.put("name", nameSetCards);
            cv.put("size", sizeSetCards);
            setIdSetCards(imageDB.getWritableDatabase().insert(SET_CARDS_TABLE, null, cv));
            imageDB.close();
        }

        public void addCardToSet(ImageWrapped newImage) {
            if (!newImage.checkExistenceImage()) {
                newImage.addImage();
            }
            if (!checkExistenceByStringField("idSet = " + idSetCards + " and idImage = " + newImage.idImage, MAP_TABLE)) {
                ContentValues cv = new ContentValues();
                cv.put("idSet", idSetCards);
                cv.put("idImage", newImage.idImage);
                hashSetCards += newImage.hashImage;
                sizeSetCards++;
                imageDB.getWritableDatabase().insert(MAP_TABLE, null, cv);
                cv.clear();
                cv.put("hash", hashSetCards);
                cv.put("size", sizeSetCards);
                imageDB.getWritableDatabase().update(SET_CARDS_TABLE, cv, "id = " + idSetCards, null);
                imageDB.close();
            }
        }

        public void addCardsToSet(Collection<ImageWrapped> allNewCards) {
            for (ImageWrapped curCard : allNewCards) {
                addCardToSet(curCard);
            }
        }

        public void deleteCardFromSet(ImageWrapped deletedImage) {
            if (checkExistenceByStringField("idSet = " + idSetCards + " and idImage = " + deletedImage.idImage, MAP_TABLE)) {
                sizeSetCards--;
                hashSetCards -= deletedImage.hashImage;
                ContentValues cv = new ContentValues();
                cv.put("hash", hashSetCards);
                cv.put("size", sizeSetCards);
                imageDB.getWritableDatabase().update(SET_CARDS_TABLE, cv, "id = " + idSetCards, null);
                imageDB.getWritableDatabase().delete(MAP_TABLE, "idSet = " + idSetCards + " and idImage = " + deletedImage.idImage, null);
                imageDB.close();
            }
        }

        public void deleteCardFromSet(Collection<ImageWrapped> allDeletedCards) {
            for (ImageWrapped curCard : allDeletedCards) {
                deleteCardFromSet(curCard);
            }
        }

        public void deleteSetCards() {
            if (!checkExistenceSet()) {
                return;
            }
            if (isEmptySetCards) {
                setSetCardsInfoByHash();
            }
            imageDB.getWritableDatabase().delete(SET_CARDS_TABLE, "hash = " + hashSetCards, null);
            imageDB.getWritableDatabase().delete(MAP_TABLE, "idSet = " + idSetCards, null);
            imageDB.close();
        }

        public ArrayList<ImageWrapped> getListOfCards() {
            ArrayList<ImageWrapped> ansList = new ArrayList<ImageWrapped>();
            Cursor c = getIdCardsByIdSet(idSetCards);
            if (c.moveToFirst()) {
                do {
                    Cursor curC = imageDB.getReadableDatabase().query(IMAGE_TABLE, null, "id = " + c.getLong(c.getColumnIndex("idImage")), null, null, null, null);
                    if (curC.moveToFirst()) {
                        ansList.add(new ImageWrapped(curC));
                        curC.close();
                    }
                } while (c.moveToNext());
                c.close();
            }
            imageDB.close();
            return ansList;
        }
    }
    public static ArrayList<SetCardsWrapped> getAllSetsCards() {
        ArrayList<SetCardsWrapped> ansList = new ArrayList<SetCardsWrapped>();
        Log.d(LOG_TAG, "a");
        Cursor c = imageDB.getReadableDatabase().query(SET_CARDS_TABLE, null, null, null, null, null, null);
        Log.d(LOG_TAG, "b");
        if (c.moveToFirst()) {
            Log.d(LOG_TAG, "c");
            do {
                Log.d(LOG_TAG, "d");
                SetCardsWrapped curSet = new SetCardsWrapped(c);
                curSet.updateCardsSet();
                ansList.add(curSet);
                break;
            } while (c.moveToNext());
            Log.d(LOG_TAG, "e");
            c.close();
            Log.d(LOG_TAG, "f");
        }
        Log.d(LOG_TAG, "g");
        imageDB.close();
        Log.d(LOG_TAG, "h");
        return ansList;
    }
}
