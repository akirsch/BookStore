package com.example.android.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.bookstore.Data.BookStoreContract.StockEntry;

import java.text.DecimalFormat;

public class ProductCursorAdapter extends CursorAdapter {

    // create decimal format pattern to show price to 2 decimal places
    private static DecimalFormat df2 = new DecimalFormat("#.00");

    private String quantityAsString;

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current proudct can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        TextView nameView = view.findViewById(R.id.product_name);
        final TextView priceView = view.findViewById(R.id.product_price);
        final TextView quantityView = view.findViewById(R.id.product_quantity);
        Button saleButton = view.findViewById(R.id.sale_button);

        // Extract properties from cursor
        final String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
        final Integer quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
        quantityAsString = quantity.toString();
        int productIdIndex = cursor.getColumnIndex(StockEntry._ID);
        final String productId = cursor.getString(productIdIndex);

        // Update the TextViews with the attributes for the current product
        nameView.setText(name);

        // format price to display correctly in the list view
        double displayPrice = price / 100;
        priceView.setText("$" + df2.format(displayPrice));

        quantityView.setText(quantityAsString);

        // handle sale button click to decrease stock quantity by one and update the database
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // decrement quantity by one when sale button is clicked if current value is >= 1
                Integer updatedQuantity;

                if (quantity >= 1) {
                    updatedQuantity = quantity - 1;
                    // update quantity view to show the updated quantity
                    quantityView.setText(updatedQuantity.toString());

                    // update database with new quantity
                    ContentValues values = new ContentValues();
                    values.put(StockEntry.COLUMN_PRODUCT_QUANTITY, updatedQuantity);

                    // create new Uri for row to be updated
                    Uri updateUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, Integer.valueOf(productId));

                    // update product in database with new quantity value
                    int rowsUpdated = context.getContentResolver().update(updateUri,
                            values, null, null);
                }
            }
        });

    }
}
