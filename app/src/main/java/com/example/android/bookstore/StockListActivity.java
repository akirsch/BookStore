package com.example.android.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bookstore.Data.BookStoreContract.StockEntry;
import com.example.android.bookstore.Data.BookStoreDbHelper;

import java.util.ArrayList;

public class StockListActivity extends AppCompatActivity {

    Button allProductsButton;
    Button productsOverFifteenDollarsButton;
    Button feldheimBooksButton;
    private BookStoreDbHelper mDbhelper;
    private TextView productView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);

        mDbhelper = new BookStoreDbHelper(this);

        productView = findViewById(R.id.text_view_products);

        allProductsButton = findViewById(R.id.button_all_products);
        // when button pressed show all products in the database
        allProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // empty the text view if displaying any data
                productView.setText("");
                showAllData();
            }
        });

        productsOverFifteenDollarsButton = findViewById(R.id.button_products_more_than_15);
        productsOverFifteenDollarsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productView.setText("");
                showProductsOverFifteen();
            }
        });

        feldheimBooksButton = findViewById(R.id.button_feldheim_books);
        feldheimBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productView.setText("");
                showFeldheimBooks();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_stocklist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_stock:
                insertDummyData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * method to display all current data in database
     */
    private void showAllData() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbhelper.getReadableDatabase();

        // create string array to select the desired columns from the database
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_PRODUCT_NAME,
                StockEntry.COLUMN_PRODUCT_PRICE,
                StockEntry.COLUMN_PRODUCT_QUANTITY,
                StockEntry.COLUMN_SUPPLIER_NAME,
                StockEntry.COLUMN_SUPPLIER_PHONE};

        Cursor cursor = db.query(
                StockEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        productView = findViewById(R.id.text_view_products);

        try {


            // create header in text view to show column headers
            productView.append(
                    StockEntry._ID + " - " +
                            StockEntry.COLUMN_SUPPLIER_NAME + " - " +
                            StockEntry.COLUMN_PRODUCT_PRICE + " - " +
                            StockEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                            StockEntry.COLUMN_SUPPLIER_NAME + " - " +
                            StockEntry.COLUMN_SUPPLIER_PHONE + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(StockEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_PHONE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                double currentPrice = cursor.getDouble(priceColumnIndex);

                // price is stored as number of cents in database, so
                // convert price into dollars and cents format to display to user
                double displayPrice = currentPrice / 100;

                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                productView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        "$" + displayPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhoneNumber));
            }


        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private void showProductsOverFifteen() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbhelper.getReadableDatabase();

        // create string array to select the desired columns from the database
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_PRODUCT_NAME,
                StockEntry.COLUMN_PRODUCT_PRICE};

        String selection = StockEntry.COLUMN_PRODUCT_PRICE + ">?";
        String[] selectionArgs = new String[]{"1500"};

        Cursor cursor = db.query(
                StockEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        productView = findViewById(R.id.text_view_products);

        try {
            // create header in text view to show column headers
            productView.append(
                    StockEntry._ID + " - " +
                            StockEntry.COLUMN_PRODUCT_NAME + " - " +
                            StockEntry.COLUMN_PRODUCT_PRICE + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(StockEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_PRICE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                double currentPrice = cursor.getDouble(priceColumnIndex);

                // price is stored as number of cents in database, so
                // convert price into dollars and cents format to display to user
                double displayPrice = currentPrice / 100;

                // Display the values from each column of the current row in the cursor in the TextView
                productView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        "$" + displayPrice));
            }
        } finally {
            cursor.close();
        }
    }

    private void showFeldheimBooks() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbhelper.getReadableDatabase();

        // create string array to select the desired columns from the database
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_PRODUCT_NAME,
                StockEntry.COLUMN_PRODUCT_PRICE,
                StockEntry.COLUMN_SUPPLIER_NAME};

        String selection = StockEntry.COLUMN_SUPPLIER_NAME + "=?";
        String[] selectionArgs = new String[]{"Feldheim"};

        Cursor cursor = db.query(
                StockEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        productView = findViewById(R.id.text_view_products);

        try {
            // create header in text view to show column headers
            productView.append(
                    StockEntry._ID + " - " +
                            StockEntry.COLUMN_PRODUCT_NAME + " - " +
                            StockEntry.COLUMN_PRODUCT_PRICE + " - " +
                            StockEntry.COLUMN_SUPPLIER_NAME + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(StockEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_PRICE);
            int supplierNameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_NAME);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                double currentPrice = cursor.getDouble(priceColumnIndex);

                // price is stored as number of cents in database, so
                // convert price into dollars and cents format to display to user
                double displayPrice = currentPrice / 100;

                String currentSupplierName = cursor.getString(supplierNameColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                productView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        "$" + displayPrice + " - " +
                        currentSupplierName));
            }


        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private void insertDummyData() {

        // create Array of dummy data to insert into database
        ArrayList<Product> dummyStockList = new ArrayList<>();
        dummyStockList.add(new Product("Lord of the Rings", 1599, 500, "Penguin Books",
                "02073245567"));
        dummyStockList.add(new Product("Harry Potter", 899, 1500, "Red Hat Publishers",
                "01616678976"));
        dummyStockList.add(new Product("Kosher Cuisine", 1699, 300, "Feldheim",
                "02086632178"));
        dummyStockList.add(new Product("Home of Miracles", 2099, 210, "Mosaica Press",
                "0097226547896"));
        dummyStockList.add(new Product("Tales for the Soul", 1799, 500, "Artscroll",
                "01171367893"));
        dummyStockList.add(new Product("Short Vort", 699, 50, "Adir Press",
                "0548786543"));
        dummyStockList.add(new Product("Planting and Building", 1299, 430, "Feldheim",
                "026435665"));

        mDbhelper = new BookStoreDbHelper(this);

        SQLiteDatabase db = mDbhelper.getWritableDatabase();

        for (int i = 0; i < dummyStockList.size(); i++) {

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(StockEntry.COLUMN_PRODUCT_NAME, dummyStockList.get(i).getmName());
            values.put(StockEntry.COLUMN_PRODUCT_PRICE, dummyStockList.get(i).getmPrice());
            values.put(StockEntry.COLUMN_PRODUCT_QUANTITY, dummyStockList.get(i).getmQuantity());
            values.put(StockEntry.COLUMN_SUPPLIER_NAME, dummyStockList.get(i).getmSupplierName());
            values.put(StockEntry.COLUMN_SUPPLIER_PHONE, dummyStockList.get(i).getmSupplierPhoneNumber());


            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(StockEntry.TABLE_NAME, null, values);

            Log.v(StockListActivity.class.getSimpleName(), "Id of new row is " + String.valueOf(newRowId));

        }


    }
}
