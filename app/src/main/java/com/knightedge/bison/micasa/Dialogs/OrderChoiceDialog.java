package com.knightedge.bison.micasa.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.knightedge.bison.micasa.MainActivity;
import com.knightedge.bison.micasa.R;

/**
 * Created by Bison on 13/10/2017.
 */

public class OrderChoiceDialog extends DialogFragment {

    private int mActiveTab;
    int array;



    public interface CallBack {
        void onClick(int position);

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mActiveTab = getArguments().getInt(MainActivity.SELECTED_TAB);

        if(mActiveTab == 0){
           array = R.array.order_orders_by;
        }else{
            array = R.array.order_inventory_by;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Order by")
        .setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((CallBack) getActivity()).onClick(i);
            }
        });
        return builder.create();
    }

}
