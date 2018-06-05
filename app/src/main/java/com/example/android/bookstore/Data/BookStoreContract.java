package com.example.android.bookstore.Data;

import android.provider.BaseColumns;

public final class BookStoreContract {

    private BookStoreContract() {}

    public static abstract class StockEntry implements BaseColumns {

        public static final String TABLE_NAME = "stock";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone_number";

    }

}
