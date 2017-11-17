package com.knightedge.bison.micasa.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.knightedge.bison.micasa.R;
import com.knightedge.bison.micasa.Utility;
import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.OrderEntry;


/**
 * Created by Bison on 04/09/2017.
 */
public class UpdatePriceDialog extends DialogFragment {

    View mView;
    String mNewPriceString;
    int mItemId;
    String mItemName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mFirebaseOrder;
    String mCasaAccount;
    EditText newPriceEditText;

    public interface CallBack {
        void onUpdatePrice(Uri inventoryUri, Uri orderUri, ContentValues inventoryValues, String orderWhere, String inventoryWhere, String[] whereArgs);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.update_price_dialog, null);
        mItemId = getArguments().getInt(OrderEntry.COLUMN_ITEM_ID);
        mItemName = getArguments().getString(InventoryEntry.COLUMN_ITEM_NAME);

       newPriceEditText = (EditText) mView.findViewById(R.id.new_price_textedit);

        builder.setView(mView)
                .setTitle("Update Price")
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mNewPriceString = newPriceEditText.getText().toString();
                        if (!mNewPriceString.equals("")) {
                            double newPrice = Double.parseDouble(mNewPriceString);

                            ContentValues inventoryValues = new ContentValues();
                            inventoryValues.put(InventoryEntry.COLUMN_ITEM_UNIT_PRICE, newPrice);

                            String inventoryWhere = InventoryEntry._ID + " = ? ";
                            String orderWhere = OrderEntry.COLUMN_ITEM_ID + " = ? ";
                            String[] whereArgs = {Long.toString(mItemId)};
                            Uri inventoryUri = InventoryEntry.CONTENT_URI;
                            Uri orderUri = OrderEntry.CONTENT_URI;

                            ((CallBack) getActivity()).onUpdatePrice(inventoryUri, orderUri, inventoryValues, orderWhere, inventoryWhere, whereArgs);

                            mCasaAccount = Utility.getCasaAccount(getActivity());
                            mFirebaseOrder = database.getReference(mCasaAccount + "/Order");
                            mFirebaseOrder.child(mItemName).removeValue();
                        }
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
