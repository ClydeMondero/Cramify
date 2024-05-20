package com.example.cramify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dbflashcard.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users (user_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
        MyDB.execSQL("create Table deck (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, user_id INTEGER)");
        MyDB.execSQL("create Table card (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, answer TEXT, deck_id INTEGER, FOREIGN KEY(deck_id) REFERENCES deck(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("DROP TABLE IF EXISTS users");
        MyDB.execSQL("DROP TABLE IF EXISTS deck");
        MyDB.execSQL("DROP TABLE IF EXISTS card");
        onCreate(MyDB);
    }

    public Boolean insertData(String username, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = MyDB.insert("users", null, contentValues);
        MyDB.close();
        return result != -1;
    }

    public int getUserId(String username, String password) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT user_id FROM users WHERE username = ? AND password = ?", new String[] {username, password});
        int userId = -1; // Default value if no user found
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        MyDB.close();
        return userId;
    }

    public Boolean checkusername(String username){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM users WHERE username = ?", new String[] {username});
        int count = cursor.getCount();
        cursor.close();
        MyDB.close();
        return count > 0;
    }

    // Adding a new deck
    public Boolean addDeck(String name, int userId) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("user_id", userId);

        try {
            long result = MyDB.insertOrThrow("deck", null, contentValues);
            MyDB.close();
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            MyDB.close();
            return false;
        }
    }

    // When retrieving decks, include a condition to filter by user ID
    Cursor readDeck(int userId){
        String query = "SELECT * FROM deck WHERE user_id = ?";
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = null;
        if(MyDB != null){
            cursor = MyDB.rawQuery(query, new String[]{String.valueOf(userId)});
        }
        return cursor;
    }

    public boolean updateDeckName(String deckId, String updatedName) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", updatedName);

        try {
            int rowsAffected = MyDB.update("deck", contentValues, "id = ?", new String[]{deckId});
            MyDB.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            MyDB.close();
            return false;
        }
    }

    public boolean deleteDeck(String deckId) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        int result = MyDB.delete("deck", "id = ?", new String[]{deckId});
        MyDB.close();
        return result > 0;
    }


    public boolean addCard(String question, String answer, String deckId) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("question", question);
        contentValues.put("answer", answer);
        contentValues.put("deck_id", deckId);

        try {
            long result = MyDB.insertOrThrow("card", null, contentValues);
            MyDB.close();
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            MyDB.close();
            return false;
        }
    }

    public Cursor readCards(String deckId) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        String query = "SELECT * FROM card WHERE deck_id = ?";
        Cursor cursor = MyDB.rawQuery(query, new String[]{deckId});
        return cursor;
    }

    public boolean updateCardDetails(String cardId, String updatedQuestion, String updatedAnswer) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("question", updatedQuestion);
        contentValues.put("answer", updatedAnswer);

        try {
            int rowsAffected = MyDB.update("card", contentValues, "id = ?", new String[]{cardId});
            MyDB.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            MyDB.close();
            return false;
        }
    }


    public boolean deleteCard(String cardId) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        int result = MyDB.delete("card", "id = ?", new String[]{cardId});
        MyDB.close();
        return result > 0;
    }
}

