package com.knightedge.bison.micasa.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import com.knightedge.bison.micasa.data.MiCasaContract.OrderEntry;
import com.knightedge.bison.micasa.model.ItemOrder;

import java.util.Date;


/**
 * Created by Bison on 04/09/2017.
 */
public class AddOrderDialog extends DialogFragment {

    View mView;
    int itemId;
    String itemUnits, itemName;
    ContentValues orderValues;
    String dateString;
    double quantity;
    EditText quantityEditText;
    String mCasaAccount;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public interface CallBack {
        void onAddOrder(ContentValues orderValues);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        mView = inflater.inflate(R.layout.order_inventory_dialog, null);
        TextView itemUnitsTextView = (TextView) mView.findViewById(R.id.item_units_textview);
        quantityEditText = (EditText) mView.findViewById(R.id.order_quantity_textedit);

        itemId = getArguments().getInt(InventoryEntry._ID);
        itemName = getArguments().getString(InventoryEntry.COLUMN_ITEM_NAME);
        itemUnits = getArguments().getString(InventoryEntry.COLUMN_ITEM_UNITS);

        itemUnitsTextView.setText(itemUnits);
        dateString = String.format("%s", new Date());

        View titleView = inflater.inflate(R.layout.dialog_add_item_title,null);
        TextView text = (TextView) titleView.findViewById(R.id.add_item_textview);
        text.setText(itemName +" quantity?");

        builder.setView(mView)
                .setCustomTitle(titleView)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String quantityString = quantityEditText.getText().toString();
                        quantity = Double.parseDouble(quantityString);

                        orderValues = new ContentValues();
                        orderValues.put(OrderEntry.COLUMN_ITEM_ID, itemId);
                        orderValues.put(OrderEntry.COLUMN_ORDER_DATE, dateString);
                        orderValues.put(OrderEntry.COLUMN_ORDER_QUANTITY, quantity);
                        orderValues.put(OrderEntry.COLUMN_ORDER_PRIORITY, 0);

                        ((CallBack) getActivity()).onAddOrder(orderValues);

                        //Add to Firebase database
                        mCasaAccount = Utility.getCasaAccount(getActivity());

                        ItemOrder itemOrder = new ItemOrder(dateString, quantity, 0);
                        DatabaseReference ref =database.getReference(mCasaAccount + "/Order");

                        ref.child(itemName).setValue(itemOrder);
                    }
                })
                .

                        setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        return builder.create();
    }

}
