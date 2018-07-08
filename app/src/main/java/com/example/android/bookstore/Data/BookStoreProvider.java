package com.example.android.bookstore.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bookstore.Data.BookStoreContract.StockEntry;

public class BookStoreProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = BookStoreProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int STOCK = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int STOCK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_STOCK, STOCK);
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_STOCK + "/#", STOCK_ID);
    }

    private BookStoreDbHelper mBookStoreDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create and initialize a BookStoreDbHelper object to gain access to the pets database.
        mBookStoreDbHelper = new BookStoreDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mBookStoreDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                // For the STOCK code, query multiple rows from the stock table directly.
                cursor = database.query(
                        StockEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            case STOCK_ID:
                // For the STOCK_ID code, extract out the ID from the URI.
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the stock table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the cursor
        // so we know what content URI the cursor was created for
        // if the data at this URI changes , then we know we need to update the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return insertStockItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertStockItem(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(StockEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the price is not null
        double price = values.getAsDouble(StockEntry.COLUMN_PRODUCT_PRICE);
        String priceAsString = Double.toString(price);
        if (priceAsString == null) {
            throw new IllegalArgumentException("Product requires a price");
        }

        // Check that the quantity is not null
        Integer quantity = values.getAsInteger(StockEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Product requires a quantity");
        }

        // Check that the supplier name is not null
        String supplierName = values.getAsString(StockEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Product requires a supplier name");
        }

        // Check that the supplier phone number is not null
        String supplierPhoneNumber = values.getAsString(StockEntry.COLUMN_SUPPLIER_PHONE);
        if (supplierPhoneNumber == null) {
            throw new IllegalArgumentException("Product requires a supplier phone number");
        }

        // get writable database
        SQLiteDatabase database = mBookStoreDbHelper.getWritableDatabase();

        // insert new row into table using ContentValues object passed in to this method.
        long id = database.insert(StockEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
        }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case STOCK_ID:
                // For the STOCK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    public int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check that ContentValues object contains key for product name, and is it does,
        // check the name is not null
        if (values.containsKey(StockEntry.COLUMN_PRODUCT_NAME)) {
            String productName = values.getAsString(StockEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // Check that ContentValues object contains key for product price.
        if (values.containsKey(StockEntry.COLUMN_PRODUCT_PRICE)) {
            double price = values.getAsDouble(StockEntry.COLUMN_PRODUCT_PRICE);
            String priceAsString = Double.toString(price);
            if (priceAsString == null  || price < 0) {
                throw new IllegalArgumentException("Product requires a price");
            }
        }

        // Check that ContentValues object contains key for product quantity, and is it does,
        // check the quantity is valid
        if (values.containsKey(StockEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(StockEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires a valid quantity");
            }
        }

        // Check that ContentValues object contains key for supplier name, and is it does,
        // check the name is not null
        if (values.containsKey(StockEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(StockEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Product requires a supplier name");
            }
        }

        // Check that ContentValues object contains key for supplier phone number, and is it does,
        // check the name is not null
        if (values.containsKey(StockEntry.COLUMN_SUPPLIER_PHONE)) {
            String supplierNumber = values.getAsString(StockEntry.COLUMN_SUPPLIER_PHONE);
            if (supplierNumber == null) {
                throw new IllegalArgumentException("Product requires a supplier phone number");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // get writable database
        SQLiteDatabase database = mBookStoreDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(StockEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        // Get writable database
        SQLiteDatabase database = mBookStoreDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                // Delete all rows that match the selection and selection args
                rowsDeleted =  database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK_ID:
                // Delete a single row given by the ID in the URI
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted =  database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0 ){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return StockEntry.CONTENT_LIST_TYPE;
            case STOCK_ID:
                return StockEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


}
