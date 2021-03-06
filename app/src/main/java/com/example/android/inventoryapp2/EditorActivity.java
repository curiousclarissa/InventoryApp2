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
package com.example.android.inventoryapp2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryapp2.data.ProductContract;
import com.example.android.inventoryapp2.data.ProductContract.ProductEntry;

/**
 * Created by clarissajarem on 11/9/18.
 */
/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    /** Identifier for the pet data loader */
    private static final int EXISTING_PRODUCT_LOADER = 0;
    /** Content URI for the existing pet (null if it's a new pet) */
    private Uri localCurrentProductUri;

    /** EditText field to enter the product name */
    private EditText mNameEditText;

    /** EditText field to enter the product supplier */
    private EditText mSupplierEditText;

    /** EditText field to enter the product price */
    private EditText mPriceEditText;

    /** EditText field to enter the product quantity */
    private EditText mQuantityEditText;

    /** EditText field to enter the supplier phone */
    private EditText mSupplierPhoneEditText;
    /** EditText field to enter the supplier phone */
    /** sale button */

    private Button mSaleButton;
    /** add button */

    private Button mAddButton;
    /** call button */
    private Button mCallButton;

    /** delete button */
    private Button mDeleteButton;

    /** EditText field to enter the pet's gender */
    private Spinner mTypeSpinner;


    /**
     * Gender of the pet. The possible valid values are in the ProductContract.java file:
     * {@link ProductContract.ProductEntry#TYPE_UNKNOWN}, {@link ProductContract.ProductEntry#GROCERY}, or
     * {@link ProductEntry#GOODS}.
     */
    private int mType = ProductEntry.TYPE_UNKNOWN;
    /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
    private boolean localProductHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener localTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            localProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //receive the intent from the {@link CatalogActivity}
        Intent intent = getIntent();
        localCurrentProductUri = intent.getData();

        //handle the Uri having no data
        if (localCurrentProductUri == null){
            //this is a new pet, set title to Add a Product
            setTitle(getString(R.string.editor_activity_title_new_product));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }
        else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mSupplierEditText = (EditText) findViewById(R.id.edit_product_supplier);
        mPriceEditText = (EditText) findViewById(R.id.edit_pet_price);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mSaleButton = (Button) findViewById(R.id.sale_button);
        mAddButton = (Button) findViewById(R.id.add_button);
        mCallButton = (Button) findViewById(R.id.call_button);
        mDeleteButton = (Button) findViewById(R.id.delete_button);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_type);

        mNameEditText.setOnTouchListener(localTouchListener);
        mSupplierEditText.setOnTouchListener(localTouchListener);
        mPriceEditText.setOnTouchListener(localTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(localTouchListener);
        mQuantityEditText.setOnTouchListener(localTouchListener);
        mTypeSpinner.setOnTouchListener(localTouchListener);

        mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityUpdate = Integer.parseInt(mQuantityEditText.getText().toString());
                if (quantityUpdate > 0) {
                    quantityUpdate -= 1;
                    mQuantityEditText.setText(Integer.toString(quantityUpdate));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.below_zero_warning, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityUpdate = Integer.parseInt(mQuantityEditText.getText().toString());
                quantityUpdate += 1;
                mQuantityEditText.setText(Integer.toString(quantityUpdate));
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PackageManager pm = getPackageManager();

                if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)){
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse("tel:" + mSupplierPhoneEditText.getText()));
                    startActivity(phoneIntent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.action_warn_no_phone, Toast.LENGTH_SHORT).show();
                }

            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            showDeleteConfirmationDialog();
            }
        });

        setupSpinner();
    }
    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mTypeSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_grocery))) {
                        mType = ProductEntry.GROCERY;
                    } else if (selection.equals(getString(R.string.type_goods))) {
                        mType = ProductEntry.GOODS;
                    } else {
                        mType = ProductContract.ProductEntry.TYPE_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = ProductContract.ProductEntry.TYPE_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save new pet into database.
     */

    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank

        if (TextUtils.isEmpty(priceString) || mType == ProductContract.ProductEntry.TYPE_UNKNOWN || TextUtils.isEmpty(nameString) || TextUtils.isEmpty(supplierPhoneString) || TextUtils.isEmpty(quantityString)) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, R.string.unsaved_missing_data,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
        values.put(ProductEntry.COLUMN_PRODUCT_TYPE, mType);
        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, price);

        // Insert a new pet into the provider, returning the content URI for the new pet.
        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (localCurrentProductUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            }
            else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }
        else {
            // Otherwise this is an EXISTING product, so update the pet with content URI: mCurrentProductUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentProductUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(localCurrentProductUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!localProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!localProductHasChanged) {
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_TYPE,
                ProductEntry.COLUMN_SUPPLIER_PHONE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                localCurrentProductUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColmnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE);
            int genderColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TYPE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int type = cursor.getInt(genderColumnIndex);
            String phone = cursor.getString(supplierPhoneColmnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mSupplierEditText.setText(supplier);
            mSupplierPhoneEditText.setText(phone);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));
            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (type) {
                case ProductContract.ProductEntry.GROCERY:
                    mTypeSpinner.setSelection(1);
                    break;
                case ProductEntry.GOODS:
                    mTypeSpinner.setSelection(2);
                    break;
                default:

                    mTypeSpinner.setSelection(0);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mTypeSpinner.setSelection(0); // Select "Unknown" TYPE

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new PRODUCT, hide the "Delete" menu item.
        if (localCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
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

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (localCurrentProductUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(localCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();

    }
}