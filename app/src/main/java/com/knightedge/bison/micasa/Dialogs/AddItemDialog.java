package com.knightedge.bison.micasa.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.knightedge.bison.micasa.MainActivity;
import com.knightedge.bison.micasa.R;
import com.knightedge.bison.micasa.SignInActivity;
import com.knightedge.bison.micasa.Utility;
import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.model.InventoryItem;


/**
 * Created by Bison on 04/09/2017.
 */
public class AddItemDialog extends DialogFragment {

    View mView;
    String mCasaAccount;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public interface CallBack {
        void onAddItem(ContentValues inventoryValues);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_add_item, null);
        View titleView = inflater.inflate(R.layout.dialog_add_item_title,null);
        TextView text = (TextView) titleView.findViewById(R.id.add_item_textview);
        text.setText("Add New Item");

        builder.setView(mView)
                .setCustomTitle(titleView)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        InputMethodManager imm = (InputMethodManager) mView.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                        EditText itemNameEditText = (EditText) mView.findViewById(R.id.dialog_item_name);
                        EditText itemUnitsEditText = (EditText) mView.findViewById(R.id.dialog_item_units);
                        EditText itemPriceEditText = (EditText) mView.findViewById(R.id.dialog_item_price);
                        String itemName = itemNameEditText.getText().toString();
                        String itemUnits = itemUnitsEditText.getText().toString();
                        String itemPrice = itemPriceEditText.getText().toString();

                        double price;
                        if (TextUtils.isEmpty(itemPrice)) {
                            price = 0.00;
                        } else {
                            price = Double.parseDouble(itemPrice);
                        }

                        if (TextUtils.isEmpty(itemUnits)) {
                            itemUnits = "Ea";
                        }

                        if (!TextUtils.isEmpty(itemName)) {

                            ContentValues inventoryValues = new ContentValues();
                            inventoryValues.put(InventoryEntry.COLUMN_ITEM_NAME, itemName);
                            inventoryValues.put(InventoryEntry.COLUMN_ITEM_UNITS, itemUnits);
                            inventoryValues.put(InventoryEntry.COLUMN_ITEM_UNIT_PRICE, price);

                            Log.d("onAddItem",getActivity().toString());
                            ((CallBack) getActivity()).onAddItem(inventoryValues);

                            //Add to Firebase database
                            mCasaAccount= Utility.getCasaAccount(getActivity());

                            String uid = SignInActivity.mAuth.getUid();

                            InventoryItem inventoryItem = new InventoryItem(itemUnits, price);
                            DatabaseReference ref = database.getReference( mCasaAccount +"/Inventory");
                            ref.child(itemName).setValue(inventoryItem);
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        InputMethodManager imm = (InputMethodManager) mView.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });
        return builder.create();
    }

}
