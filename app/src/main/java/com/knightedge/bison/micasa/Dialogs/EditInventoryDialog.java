package com.knightedge.bison.micasa.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.knightedge.bison.micasa.MainActivity;
import com.knightedge.bison.micasa.R;
import com.knightedge.bison.micasa.Utility;
import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.model.InventoryItem;

/**
 * Created by Bison on 11/09/2017.
 */
public class EditInventoryDialog extends DialogFragment {
    View mView;
    private String mSendingClass;
    private final String SEARCHABLEACTIVITY = "SearchableActivity";
    private int mPosition;
    Cursor cursor;
    String mCasaAccount;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public interface CallBack {
        void onEditInventory(Uri inventoryUri, ContentValues inventoryValues, String where, String[] whereArgs);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_add_item, null);

        final int itemId  = getArguments().getInt(InventoryEntry._ID);
        String itemName  = getArguments().getString(InventoryEntry.COLUMN_ITEM_NAME);
        String itemUnits  = getArguments().getString(InventoryEntry.COLUMN_ITEM_UNITS);
        String price  = getArguments().getString(InventoryEntry.COLUMN_ITEM_UNIT_PRICE);


            final EditText itemNameEditText = (EditText) mView.findViewById(R.id.dialog_item_name);
            itemNameEditText.setText(itemName);
            final EditText itemUnitsEditText = (EditText) mView.findViewById(R.id.dialog_item_units);
            itemUnitsEditText.setText(itemUnits);
            final EditText itemPriceEditText = (EditText) mView.findViewById(R.id.dialog_item_price);
            itemPriceEditText.setText(price);

        View titleView = inflater.inflate(R.layout.dialog_add_item_title,null);
        TextView text = (TextView) titleView.findViewById(R.id.add_item_textview);
        text.setText("Edit "+ itemName);

            builder.setView(mView)
                    .setCustomTitle(titleView)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            ContentValues inventoryValues = new ContentValues();
                            String itemName = itemNameEditText.getText().toString();
                            String itemUnits = itemUnitsEditText.getText().toString();
                            String itemPrice = itemPriceEditText.getText().toString();
                            double price =  Double.parseDouble(itemPrice);

                            inventoryValues.put(InventoryEntry.COLUMN_ITEM_NAME, itemName);
                            inventoryValues.put(InventoryEntry.COLUMN_ITEM_UNITS, itemUnits);
                            inventoryValues.put(InventoryEntry.COLUMN_ITEM_UNIT_PRICE, price);

                            Uri inventoryUri = InventoryEntry.CONTENT_URI;
                            String where = InventoryEntry._ID + " = ?";
                            String[] whereArgs = {Long.toString(itemId)};

                            ((CallBack) getActivity()).onEditInventory(inventoryUri, inventoryValues, where, whereArgs);

                            mCasaAccount= Utility.getCasaAccount(getActivity());

                            InventoryItem inventoryItem = new InventoryItem(itemUnits, price);
                            DatabaseReference ref = database.getReference( mCasaAccount +"/Inventory");

                            ref.child(itemName).setValue(inventoryItem);

                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        return builder.create();
    }
}
