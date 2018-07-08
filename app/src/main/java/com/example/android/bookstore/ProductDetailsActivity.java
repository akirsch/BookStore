package com.example.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.Data.BookStoreContract.StockEntry;

import java.text.DecimalFormat;


public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mProductHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    /**
     * EditText field to enter the product's name
     */
    private EditText mProductNameEditText;

    /**
     * EditText field to enter the product's price
     */
    private EditText mProductPriceEditText;

    /**
     * EditText field to enter the products quantity
     */
    private EditText mProductQuantityEditText;

    /**
     * EditText field to enter the supplier's name
     */
    private EditText mSupplierNameEditText;

    /**
     * ImageButton to increment product quantity
     */
    private ImageButton mIncrementButton;

    /**
     * ImageButton to decrement product quantity
     */
    private ImageButton mDecrementButton;

    /**
     * EditText field to enter the supplier's phone number
     */
    private EditText mSupplierNumberEditText;

    private FloatingActionButton orderFab;

    private String currentSupplierNumber;

    private Integer currentProductQuantity;

    // create decimal format pattern to show price to 2 decimal places
    private static DecimalFormat df2 = new DecimalFormat("#.00");

    /**
     * Boolean value to track whether any changes have been entered to product data by the user
     */
    private boolean mProductHasChanged = false;

    // declare variable to store uri provided by intent which opens the Activity
    Uri currentProductUri;

    private final int EXISTING_PRODUCT_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptoduct_details);

        mProductNameEditText = findViewById(R.id.product_name);
        mProductPriceEditText = findViewById(R.id.product_price);
        mProductQuantityEditText = findViewById(R.id.product_quantity);
        mSupplierNameEditText = findViewById(R.id.supplier_name);
        mSupplierNumberEditText = findViewById(R.id.supplier_number);
        mIncrementButton = findViewById(R.id.increment_button);
        mDecrementButton = findViewById(R.id.decrement_button);


        // set touchListeners on all the views to listen for changes made by the user
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierNumberEditText.setOnTouchListener(mTouchListener);


        // set up fab
        orderFab = findViewById(R.id.fab);
        orderFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhoneNumber(currentSupplierNumber);
            }
        });

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get intent object used to open this activity
        Intent intent = getIntent();

        // get data object inside the intent
        currentProductUri = intent.getData();

        if (currentProductUri == null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            getSupportActionBar().setTitle(getString(R.string.details_activity_title_new_product));
            currentProductQuantity = 0;
            orderFab.setVisibility(View.GONE);
        } else {
            getSupportActionBar().setTitle(getString(R.string.product_details_activity_title_edit_pet));
            // create instance of Loader Manager object
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER_ID, null, this);
        }

        // handle clicks on increment button
        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int updatedQuantity = ++currentProductQuantity;
                mProductQuantityEditText.setText(String.valueOf(updatedQuantity), TextView.BufferType.EDITABLE);
            }
        });

        // handle clicks on decrement button
        mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentProductQuantity >= 1) {
                    int updatedQuantity = --currentProductQuantity;
                    mProductQuantityEditText.setText(String.valueOf(updatedQuantity), TextView.BufferType.EDITABLE);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_product_details.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_product_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        /*
         * Takes action based on the ID of the Loader that's being created
         */
        switch (loaderID) {
            case EXISTING_PRODUCT_LOADER_ID:
                // Since the editor shows all pet attributes, define a projection that contains
                // all columns from the pet table
                String[] projection = {
                        StockEntry._ID,
                        StockEntry.COLUMN_PRODUCT_NAME,
                        StockEntry.COLUMN_PRODUCT_PRICE,
                        StockEntry.COLUMN_PRODUCT_QUANTITY,
                        StockEntry.COLUMN_SUPPLIER_NAME,
                        StockEntry.COLUMN_SUPPLIER_PHONE};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(getApplicationContext(),
                        currentProductUri,
                        projection,
                        null,
                        null,
                        null);
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // include this check so that app doesn't crash when pet is deleted, otherwise the Loader
        // will still try and reference a Cursor to a row that no longer exists
        // triggering an outOfBoundsExceptionError.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        cursor.moveToFirst();

        // Find the columns of pet attributes that we're interested in
        int productNameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_NAME);
        int supplierNumberColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_PHONE);

        // get the values for the current pet in each of the columns
        String currentProductName = cursor.getString(productNameColumnIndex);
        double currentProductPrice = cursor.getDouble(productPriceColumnIndex);
        currentProductQuantity = cursor.getInt(productQuantityColumnIndex);
        String currentSupplierName = cursor.getString(supplierNameColumnIndex);
        currentSupplierNumber = cursor.getString(supplierNumberColumnIndex);

        // set these values in the data entry fields in the Activity
        // set the product name
        mProductNameEditText.setText(currentProductName);

        // set the pet price
        double displayPrice = currentProductPrice / 100;
        mProductPriceEditText.setText(df2.format(displayPrice));

        // set the product quantity
        mProductQuantityEditText.setText(currentProductQuantity.toString());

        // set the supplier name
        mSupplierNameEditText.setText(currentSupplierName);


        // set the supplier number
        mSupplierNumberEditText.setText(currentSupplierNumber);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierNumberEditText.setText("");
    }

    public void saveProduct() {
        // grab data entered into the editText views and selected from the spinner
        String productNameString = mProductNameEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        String supplerNameString = mSupplierNameEditText.getText().toString().trim();
        String supplerNumberString = mSupplierNumberEditText.getText().toString().trim();

        // if all fields are left blank when save button is
        // pressed, just end the activity without saving a new product.
        if (TextUtils.isEmpty(productNameString)
                || TextUtils.isEmpty(productPriceString)
                || TextUtils.isEmpty(productQuantityString)
                || TextUtils.isEmpty(supplerNameString)
                || TextUtils.isEmpty(supplerNumberString)) {
            return;
        }

        // convert text entered for product price into long value to store in database as price in cents
        double productPrice = (Double.parseDouble(productPriceString)) * 100;

        // convert entered text in Quantity field to integer value to store in database
        int productQuantity = Integer.parseInt(productQuantityString);


        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(StockEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(StockEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(StockEntry.COLUMN_SUPPLIER_NAME, supplerNameString);
        values.put(StockEntry.COLUMN_SUPPLIER_PHONE, supplerNumberString);

        // if in 'Add Pet' mode, use insert method in the ContentResolver to insert new pet in the database
        if (currentProductUri == null) {
            // Insert the new row, returning the primary key value of the new row
            Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);

            String successfulEntryString = getString(R.string.successful_insertion);
            String entryErrorString = getString(R.string.entry_error_string);

            // Display toast message displaying Id of newly added row, or error message if insert is unsuccessful
            if (newUri != null) {
                Toast toast = Toast.makeText(getApplicationContext(), successfulEntryString,
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), entryErrorString, Toast.LENGTH_SHORT);
                toast.show();
            }

            // if in 'Edit Pet' mode, use update method in the ContentResolver to update pet in the database
        } else {
            // Otherwise this is an EXISTING product, so update the product content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentProductUri will already identify the correct row in the database that
            // we want to modify.
            int numOfRowsUpdated = getContentResolver().update(currentProductUri, values,
                    null, null);

            String successfulEditString = getString(R.string.successful_edit);
            String editErrorString = getString(R.string.edit_error_string);

            // Display toast message displaying number of rows updated,
            // or error message if insert is unsuccessful
            if (numOfRowsUpdated != 0) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        Integer.toString(numOfRowsUpdated) + " " + successfulEditString,
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), editErrorString, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // if the app in in product details mode, delete the current product from the database
        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri,
                    null,
                    null);

            String successfulProductDeletion = getString(R.string.editor_delete_product_successful);
            String productDeletionError = getString(R.string.editor_delete_product_failed);

            if (rowsDeleted != 0) {
                Toast toast = Toast.makeText(getApplicationContext(), successfulProductDeletion, Toast.LENGTH_SHORT);
                toast.show();
                // return to catalogActivity screen
                finish();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), productDeletionError, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
