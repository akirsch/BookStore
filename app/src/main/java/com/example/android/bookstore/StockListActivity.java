package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.bookstore.Data.BookStoreContract.StockEntry;


public class StockListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ProductCursorAdapter productCursorAdapter;

    private CursorLoader cursorLoader;

    FloatingActionButton fab;

    private final int PRODUCT_CURSOR_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);

        getSupportActionBar().setTitle(getString(R.string.stock_list_activity_title));

        // Setup FAB to open ProductDetailsActivity
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockListActivity.this, ProductDetailsActivity.class);
                intent.setData(null);
                startActivity(intent);
            }
        });

        // set up list view functionality
        ListView productListView = findViewById(R.id.productListView);

        // Set up an adapter to create a list item for each row of pet data in the Cursor
        // There is no product data yet (until the loader finishes) so pass in null for the Cursor
        productCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(productCursorAdapter);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);


        // set up a click listener to open the product details activity
        // displaying the data for each product when it is clicked on
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // create intent to launch ProductDetailsActivity when a product in the list is clicked on.
                Intent intent = new Intent(StockListActivity.this, ProductDetailsActivity.class);

                // create specific uri for product that is clicked on
                Uri currentPetUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);

                // set this uri as the data field for the intent
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        // create instance of Loader Manager object
        getLoaderManager().initLoader(PRODUCT_CURSOR_LOADER_ID, null, this );
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
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        /*
         * Takes action based on the ID of the Loader that's being created
         */
        switch (PRODUCT_CURSOR_LOADER_ID) {
            case PRODUCT_CURSOR_LOADER_ID:
                // create string array to select the desired columns from the database
                String[] projection = {
                        StockEntry._ID,
                        StockEntry.COLUMN_PRODUCT_NAME,
                        StockEntry.COLUMN_PRODUCT_PRICE,
                        StockEntry.COLUMN_PRODUCT_QUANTITY};

                cursorLoader = new CursorLoader(getApplicationContext(),StockEntry.CONTENT_URI, projection,
                        null, null, null );

                return cursorLoader;
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        productCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);
    }

    /**
     * Helper method to delete all products in the database.
     */
    public void deleteAllProducts(){
        int rowsDeleted = getContentResolver().delete(StockEntry.CONTENT_URI, null, null);
        Log.v("StockListActivity", rowsDeleted + " rows deleted from Stock database");
    }
}
