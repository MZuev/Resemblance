package ru.spbau.resemblance;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;


public class ImageStorage {
    final private static String LOG_TAG = "DBLog";
    final private static String IMAGE_TABLE = "imageTable";
    final private static String SET_CARDS_TABLE = "setCardsTable";
    final private static String MAP_TABLE = "mapSetAndImage";
    final private static String DATA_BASE_NAME = "imageDB1";
    final private static int CURRENT_VERSION = 1;
    final private static long BASE_IN_HASH = 257;


    private static byte[] imageBuffer = null;

    private ImageStorage() {
    }

    private static ImageDB imageDB = null;

    public static void createImageStorage(Context context) {
        if (imageDB == null) {
            imageDB = new ImageDB(context);
        }
        if (imageBuffer == null) {
            imageBuffer = new byte[context.getResources().getInteger(R.integer.max_image_size)];
        }

        addTestSet(context, "Set1", "a", 45);
        addTestSet(context, "Set2", "b", 50);

        printToLogAll(IMAGE_TABLE);
        printToLogAll(SET_CARDS_TABLE);
        printToLogAll(MAP_TABLE);
    }

    private static void addTestSet(Context context, String nameSet, String prefCardName, int sizeSet) {
        SetCardsWrapped setCards = new SetCardsWrapped();
        setCards.setName(nameSet);
        setCards.addSetCards();
        for (int i = 1; i <= sizeSet; i++) {
            int curId = context.getResources().getIdentifier(prefCardName + i, "drawable", context.getPackageName());
            Resources resources = context.getResources();
            ImageWrapped curImage = addImageByUri(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(curId) + '/' + resources.getResourceTypeName(curId) + '/' + resources.getResourceEntryName(curId), context);
            setCards.addCardToSet(curImage);
        }
    }

    private static void clearImageDB() {
        imageDB.getWritableDatabase().delete(IMAGE_TABLE, null, null);
        imageDB.getWritableDatabase().delete(SET_CARDS_TABLE, null, null);
        imageDB.getWritableDatabase().delete(MAP_TABLE, null, null);
        imageDB.close();
    }

    private static void printToLogCur(Cursor c) {
        StringBuilder newLog = new StringBuilder();
        for (String columnName : c.getColumnNames()) {
            newLog.append(columnName);
            newLog.append(" = ");
            newLog.append(c.getString(c.getColumnIndex(columnName)));
            newLog.append(" ; ");
        }
        Log.d(LOG_TAG, newLog.toString());
    }

    private static void printToLogAll(Cursor c) {
        if (c.moveToFirst()) {
            do {
                printToLogCur(c);
            } while (c.moveToNext());
            c.close();
        }
    }
    private static void printToLogAll(String nameTable) {
        imageDB.close();
        Log.d(LOG_TAG, " -- Print table " + nameTable + " -- ");
        Cursor c = imageDB.getWritableDatabase().query(nameTable, null, null, null, null, null, null);
        printToLogAll(c);
        Log.d(LOG_TAG, " -- End to print table " + nameTable + " -- ");
        imageDB.close();
    }

    private static long calcHashByUri(String uriImage, Context context) {
        long hashImage = 0;
        try {
            Uri uri = Uri.parse(uriImage);
            InputStream stream = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
            int len = stream.read(imageBuffer);
            stream.close();
            hashImage = uriImage.hashCode();
            for (int i = 0; i < len; i++) {
                hashImage *= BASE_IN_HASH;
                hashImage += imageBuffer[i];
            }
            //TODO
        }
        catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        return hashImage;
    }

     private static class ImageDB extends SQLiteOpenHelper{

        private ImageDB(Context context) {
            super(context, DATA_BASE_NAME, null, CURRENT_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "  ---  Create DB  ---  ");
            db.execSQL("create table " + IMAGE_TABLE + " (id integer primary key autoincrement, uri text, hash integer);");
            db.execSQL("create table " + SET_CARDS_TABLE + " (id integer primary key autoincrement, hash integer, name text, size integer);");
            db.execSQL("create table " + MAP_TABLE + " (idSet integer, idImage integer);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //TODO
        }
    }

    public static ImageWrapped addImageByUri(String uriImage, Context context) {
        long hashImage = calcHashByUri(uriImage, context);
        if (hashImage == 0) {
            Log.d(LOG_TAG, " -- Bad Uri: " + uriImage + " -- ");
            return null;
        }
        ImageWrapped newImage = new ImageWrapped(hashImage, uriImage);
        newImage.addImage();
        newImage.setNotEmpty();
        return newImage;
    }

    private static boolean checkExistenceByHash(long hashObject, String nameTable) {
        Cursor c = imageDB.getWritableDatabase().query(nameTable, null, "hash = " + hashObject, null, null, null,null);
        boolean ans = c.moveToFirst();
        c.close();
        return ans;
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
        Cursor c = imageDB.getWritableDatabase().query(nameTable, null, nameAndValueField, null, null, null,null);
        boolean ans = c.moveToFirst();
        c.close();
        return ans;
    }

    private static Cursor getImageInfo(ImageWrapped curImage) {
     //   Log.d(LOG_TAG, " -- Get image info -- ");
        Cursor c = imageDB.getWritableDatabase().query(IMAGE_TABLE, null, "hash = " + curImage.hashImage, null, null, null, null);
        if (c.moveToFirst()) {
            return c;
        }
        if (curImage.uriImage != null) {
            c = imageDB.getWritableDatabase().query(IMAGE_TABLE, null, "uri = '" + curImage.uriImage + "'", null, null, null, null);
            if (c.moveToFirst()) {
                return c;
            }
        }
        c = imageDB.getWritableDatabase().query(IMAGE_TABLE, null, "id = " + curImage.idImage, null, null, null, null);
        if (c.moveToFirst()) {
            return c;
        }
        return null;
    }

    private static Cursor getSetCardsInfo(SetCardsWrapped curSetCard) {
       // Log.d(LOG_TAG, " -- Get set cards info -- ");
        Cursor c = null;
        if (curSetCard.nameSetCards != null) {
            c = imageDB.getWritableDatabase().query(SET_CARDS_TABLE, null, "name = '" + curSetCard.nameSetCards + "'", null, null, null, null);
            if (c.moveToFirst()) {
                return c;
            }
        }
        c = imageDB.getWritableDatabase().query(SET_CARDS_TABLE, null, "id = " + curSetCard.idSetCards, null, null, null, null);
        if (c.moveToFirst()) {
            return c;
        }
        c = imageDB.getWritableDatabase().query(SET_CARDS_TABLE, null, "hash = " + curSetCard.hashSetCards, null, null, null, null);
        if (c.moveToFirst()) {
            return c;
        }
        return null;
    }

    private static Cursor getIdCardsByIdSet(long idSetCards) {
        return imageDB.getWritableDatabase().query(MAP_TABLE, null, "idSet = " + idSetCards, null, null, null, null);
    }

    private static Cursor getIdCardsByIdImage(long idImage) {
        return imageDB.getWritableDatabase().query(MAP_TABLE, null, "idImage = " + idImage, null, null, null, null);
    }

    private static long getHashImageById(long idImage) {
        Cursor c = imageDB.getWritableDatabase().query(IMAGE_TABLE, null, "id = " + idImage, null, null, null, null);
        c.moveToFirst();
        long hashImage = c.getLong(c.getColumnIndex("hash"));
        c.close();
        return hashImage;
    }

    public static class ImageWrapped {
        private long hashImage = 0;
        private String uriImage = null;
        private long idImage = 0;
        private boolean isEmptyImage = true;

        ImageWrapped(){}

        public ImageWrapped(long newHash) {
            hashImage = newHash;
        }

        public ImageWrapped(long hash, String uri) {
            hashImage = hash;
            uriImage = uri;
        }

        private ImageWrapped(Cursor c) {
            hashImage = c.getLong(c.getColumnIndex("hash"));
            uriImage = c.getString(c.getColumnIndex("uri"));
            idImage = c.getLong(c.getColumnIndex("id"));
            isEmptyImage = false;
        }

        public void setIdImage(long id) {
            idImage = id;
        }

        public void setNotEmpty() { isEmptyImage = false; }

        public String getUriImage() { return uriImage; }
        public  int getIdImage() { return (int)idImage; }

        public boolean exists() {
            return checkExistenceByHash(hashImage, IMAGE_TABLE)
                            || checkExistenceByStringField(uriImage, "uri", IMAGE_TABLE)
                            || checkExistenceByStringField(String.valueOf(idImage), "id", IMAGE_TABLE);
        }

        public void setImageInfo() {
            if (!isEmptyImage) {
                return;
            }
            Cursor c = getImageInfo(this);
            if (c != null && c.moveToFirst()) {
                uriImage = c.getString(c.getColumnIndex("uri"));
                idImage = c.getLong(c.getColumnIndex("id"));
                c.close();
                isEmptyImage = false;
            }
            else {
                Log.d(LOG_TAG, "Wrong image");
            }
            imageDB.close();
        }

        public ImageView getViewImage(Context context) {
            if (!exists()) {
                return null;
            }
            if (isEmptyImage) {
                setImageInfo();
            }
            ImageView imageView = new ImageView(context);
            imageView.setImageURI(Uri.parse(uriImage));
            return imageView;
        }

        public boolean addImage() {
            if (exists()) {
                setImageInfo();
                return false;
            }
            ContentValues cv = new ContentValues();
            cv.put("uri", uriImage);
            cv.put("hash", hashImage);
            setIdImage(imageDB.getWritableDatabase().insert(IMAGE_TABLE, null, cv));
            imageDB.close();
            return true;
        }

        public void deleteImage() {
            if (!exists()) {
                return;
            }
            if (isEmptyImage) {
                setImageInfo();
            }
            imageDB.getWritableDatabase().delete(IMAGE_TABLE, "hash = " + hashImage, null);
            imageDB.getWritableDatabase().delete(MAP_TABLE, "idImage = " + idImage, null);
            imageDB.close();
        }
        public ImageView getImageView(Context context) {
            if (isEmptyImage) {
                setImageInfo();
            }
            ImageView imageView = new ImageView(context);
            imageView.setImageURI(Uri.parse(getUriImage()));
            return imageView;
        }

        public ImageView getPreview(Context context, int size) throws FileNotFoundException {
            final double EPS = 0.50001;
            if (isEmptyImage) {
                setImageInfo();
            }
            InputStream fileStream = context.getContentResolver().openInputStream(Uri.parse(uriImage));

            BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
            sizeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fileStream, null, sizeOptions);
            int realSize = Math.min(sizeOptions.outHeight, sizeOptions.outWidth);
            int scale = (int)((double)realSize / size + EPS);

            BitmapFactory.Options readOptions = new BitmapFactory.Options();
            readOptions.inSampleSize = scale;
            fileStream = context.getContentResolver().openInputStream(Uri.parse(uriImage));
            Bitmap imageBitmap = BitmapFactory.decodeStream(fileStream, null, readOptions);
            ImageView ret = new ImageView(context);
            ret.setImageBitmap(imageBitmap);

            return ret;
        }

        public  static ImageWrapped createById(int idNewImage) {
            ImageWrapped newImage = new ImageWrapped();
            newImage.setIdImage(idNewImage);
            newImage.setImageInfo();
            return newImage;
        }
    }

    public static class SetCardsWrapped{
        private long idSetCards = 0;
        private long hashSetCards = 0;
        private String nameSetCards = null;
        private int sizeSetCards = 0;
        private boolean isEmptySetCards = true;

        SetCardsWrapped(){}

        private SetCardsWrapped(Cursor c) {
            Log.d(LOG_TAG, "q");
            hashSetCards = c.getLong(c.getColumnIndex("hash"));
            idSetCards = c.getLong(c.getColumnIndex("id"));
            nameSetCards = c.getString(c.getColumnIndex("name"));
            sizeSetCards = c.getInt(c.getColumnIndex("size"));
            isEmptySetCards = false;
        }

        public long getHash() {
            return hashSetCards;
        }

        public void setIdSetCards(long id) {
            idSetCards = id;
        }

        public void setName(String name) {
            nameSetCards = name;
        }

        public void setNotEmpty() { isEmptySetCards = false; }

        public String getNameSetCards() {
            return nameSetCards;
        }

        public int getSizeSetCards() {
            return sizeSetCards;
        }

        public boolean checkExistenceSet() {
            return ImageStorage.checkExistenceByHash(hashSetCards, SET_CARDS_TABLE)
                            || checkExistenceByStringField(nameSetCards, "name", SET_CARDS_TABLE)
                            || checkExistenceByStringField(String.valueOf(idSetCards), "id", SET_CARDS_TABLE);
        }

        public static boolean checkExistenceByHash(long hash) {
            return ImageStorage.checkExistenceByHash(hash, SET_CARDS_TABLE);
        }

        public void setSetCardsInfo() {
            if (!isEmptySetCards) {
                return;
            }
            Cursor c = getSetCardsInfo(this);
            if (c != null && c.moveToFirst()) {
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
                setSetCardsInfo();
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
            setSetCardsInfo();
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

        public boolean addSetCards() {
            if (checkExistenceSet()) {
                setSetCardsInfo();
                return false;
            }
            setNotEmpty();
            ContentValues cv = new ContentValues();
            cv.put("hash", hashSetCards);
            cv.put("name", nameSetCards);
            cv.put("size", sizeSetCards);
            setIdSetCards(imageDB.getWritableDatabase().insert(SET_CARDS_TABLE, null, cv));
            imageDB.close();
            return true;
        }

        public boolean addCardToSet(ImageWrapped newImage) {
            if (!newImage.exists()) {
                newImage.addImage();
            }
            else {
                newImage.setImageInfo();
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
                return true;
            }
            else {
                return false;
            }
        }

        public int addCardsToSet(Collection<ImageWrapped> allNewCards) {
            int cntAddedCard = 0;
            for (ImageWrapped curCard : allNewCards) {
                if (addCardToSet(curCard)) {
                    cntAddedCard++;
                }
            }
            return cntAddedCard;
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
                setSetCardsInfo();
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
                    Cursor curC = imageDB.getWritableDatabase().query(IMAGE_TABLE, null, "id = " + c.getLong(c.getColumnIndex("idImage")), null, null, null, null);
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
        public static SetCardsWrapped createById(int idSetCards) {
            SetCardsWrapped newSetCards = new SetCardsWrapped();
            newSetCards.setIdSetCards(idSetCards);
            newSetCards.setSetCardsInfo();
            return newSetCards;
        }
    }
    public static ArrayList<SetCardsWrapped> getAllSetsCards() {
        ArrayList<SetCardsWrapped> ansList = new ArrayList<SetCardsWrapped>();
        Cursor c = imageDB.getWritableDatabase().query(SET_CARDS_TABLE, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                SetCardsWrapped curSet = new SetCardsWrapped(c);
                curSet.updateCardsSet();
                ansList.add(curSet);
            } while (c.moveToNext());
            c.close();
        }
        imageDB.close();
        return ansList;
    }
    public static ArrayList<ImageWrapped> getAllImages() {
        ArrayList<ImageWrapped> ansList = new ArrayList<>();
        Cursor c = imageDB.getWritableDatabase().query(IMAGE_TABLE, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                ImageWrapped curImage = new ImageWrapped(c);
                ansList.add(curImage);
            } while (c.moveToNext());
            c.close();
        }
        imageDB.close();
        return ansList;
    }
}
