package com.knightedge.bison.micasa.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.knightedge.bison.micasa.MainActivity;
import com.knightedge.bison.micasa.R;
import com.knightedge.bison.micasa.Utility;
import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.OrderEntry;

/**
 * Created by Bison on 11/09/2017.
 */
public class DeleteInventoryDialog extends DialogFragment {

    String mCasaAccount;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    public interface CallBack {
        void onDeleteInventory(String orderWhere, String inventoryWhere, String[] whereArgs);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String itemName = getArguments().getString(InventoryEntry.COLUMN_ITEM_NAME);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = inflater.inflate(R.layout.dialog_add_item_title, null);
        TextView text = (TextView) titleView.findViewById(R.id.add_item_textview);
        text.setText("Delete " + itemName + " ?");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(titleView)
                .setMessage("Are you sure you want to delete this item? All item orders will also be deleted")
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int itemId = getArguments().getInt(InventoryEntry._ID);
                        String itemName = getArguments().getString(InventoryEntry.COLUMN_ITEM_NAME);

                        String orderWhere = OrderEntry.COLUMN_ITEM_ID + " =? ";
                        String inventoryWhere = InventoryEntry._ID + " =? ";
                        String[] whereArgs = {Long.toString(itemId)};

                        ((CallBack) getActivity()).onDeleteInventory(orderWhere, inventoryWhere, whereArgs);

                        mCasaAccount = Utility.getCasaAccount(getActivity());
                        DatabaseReference firebaseInventory = database.getReference( mCasaAccount + "/Inventory");
                        firebaseInventory.child(itemName).removeValue();
                    }
                })

                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }

                );
        return builder.show();
    }
}