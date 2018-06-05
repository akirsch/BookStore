package com.example.android.bookstore.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.android.bookstore.Data.BookStoreContract.StockEntry;

public class BookStoreDbHelper extends SQLiteOpenHelper {

    // string constants for database version number and name
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "bookstore.db";

    private final static String TEXT_TYPE = "TEXT ";
    private final static String INTEGER_TYPE = "INTEGER ";
    private final static String NOT_NULL = "NOT NULL ";
    private final static String COMMA_SEP = ",";
    private final static String PRIMARY_KEY = "PRIMARY KEY ";
    private final static String AUTOINCREMENT = "AUTOINCREMENT ";

    public BookStoreDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STOCK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    private static final String SQL_CREATE_STOCK_TABLE = "CREATE TABLE " + StockEntry.TABLE_NAME + " " +
            "(" + StockEntry.COLUMN_ID  + " " + INTEGER_TYPE  + PRIMARY_KEY  + AUTOINCREMENT + COMMA_SEP +
            StockEntry.COLUMN_PRODUCT_NAME  + " " + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            StockEntry.COLUMN_PRODUCT_PRICE + " " + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
            StockEntry.COLUMN_PRODUCT_QUANTITY + " " + INTEGER_TYPE + NOT_NULL + "DEFAULT " + 0 + COMMA_SEP +
            StockEntry.COLUMN_SUPPLIER_NAME + " " + TEXT_TYPE + COMMA_SEP +
            StockEntry.COLUMN_SUPPLIER_PHONE + " " + TEXT_TYPE + ");";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + StockEntry.TABLE_NAME;
}
