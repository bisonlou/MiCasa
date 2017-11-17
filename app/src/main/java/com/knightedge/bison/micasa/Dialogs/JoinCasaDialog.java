package com.knightedge.bison.micasa.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.knightedge.bison.micasa.R;
import com.knightedge.bison.micasa.Utility;


/**
 * Created by Bison on 03/11/2017.
 */

public class JoinCasaDialog extends DialogFragment {
    View mView;
    String myEmail;
    String newCasaAccount;


    public interface CallBack {
        void onRequestJoinCasa(String newCasaAccount);

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    mView = inflater.inflate(R.layout.join_casa_dialog, null);
        builder.setView(mView)
            .setTitle("Join another Casa")
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            EditText nameEditText = (EditText) mView.findViewById(R.id.join_casa_edittext);
            newCasaAccount = nameEditText.getText().toString();

            myEmail = Utility.getAccountEmail(getActivity());
            ((CallBack) getActivity()).onRequestJoinCasa(newCasaAccount);

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
