/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.inventoryapp2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app.
 */
public final class ProductContract {



    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {}

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of product.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        /** Name of database table for products */
        public final static String TABLE_NAME = "products";
        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;
        /**
         * Name of the pet.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="name";
        /**
         * supplier of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier";
        /**
         * type of the product.
         *
         * The only possible values are {@link #TYPE_UNKNOWN}, {@link #GROCERY},
         * or {@link #GOODS}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_TYPE = "type";
        /**
         * quantity of the product.
         *
         * Type: String
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        /**
         * PRICE of the PRODUCT.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";
        /**
         * supplier phone
         *
         * Type: String
         */
        public final static String COLUMN_SUPPLIER_PHONE = "phone";
        /**
         * Possible values for the type of the item.
         */
        public static final int TYPE_UNKNOWN = 0;
        public static final int GROCERY = 1;
        public static final int GOODS = 2;

        /**
         * Returns whether or not the given gender is {@link #TYPE_UNKNOWN}, {@link #GROCERY},
         * or {@link #GOODS}.
         */
        public static boolean isValidType(int type) {
            if (type == TYPE_UNKNOWN || type == GROCERY || type == GOODS) {
                return true;
            }
            return false;
        }
    }

}

