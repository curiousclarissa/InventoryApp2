package com.example.android.inventoryapp2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp2.data.ProductContract;

import static java.lang.String.valueOf;

/**
 * Created by clarissajarem on 11/9/18.
 */

public class ProductCursorAdapter extends CursorAdapter {

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
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.supplier);
        TextView productPriceTextView = view.findViewById(R.id.list_price);
        final TextView productQtyTextView = (TextView) view.findViewById(R.id.list_quantity);
        final int idIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int supplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int productQtyColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);


        // Read the product attributes from the Cursor for the current pet
        String productName = cursor.getString(nameColumnIndex);
        String productSupplier = cursor.getString(supplierColumnIndex);
        String productPrice = cursor.getString(productPriceColumnIndex);
        final String productQuantity = cursor.getString(productQtyColumnIndex);
        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(productSupplier)) {
            productSupplier = context.getString(R.string.unknown_supplier);
        }

        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productName);
        summaryTextView.setText(productSupplier);
        productPriceTextView.setText(productPrice);
        productQtyTextView.setText(productQuantity);
        Button saleButton2 = view.findViewById(R.id.sale_button2);
        saleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityUpdate = Integer.parseInt(productQuantity);
                if (quantityUpdate > 0) {
                    // Create a ContentValues object where column names are the keys,
                    // and product attributes from the editor are the values.
                    ContentValues values = new ContentValues();
                    quantityUpdate -= 1;
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityUpdate);
                    Uri newUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, idIndex);
                    context.getContentResolver().update(newUri, values, null, null);
                    productQtyTextView.setText(Integer.toString(quantityUpdate));
                } else {
                    Toast.makeText(view.getContext(), R.string.below_zero_warning, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

