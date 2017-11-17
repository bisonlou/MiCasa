package com.knightedge.bison.micasa.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.knightedge.bison.micasa.MainActivity;
import com.knightedge.bison.micasa.R;
import com.knightedge.bison.micasa.Utility;
import com.knightedge.bison.micasa.model.CasaMember;

import java.lang.reflect.Member;

/**
 * Created by Bison on 24/10/2017.
 */

public class InviteMemberDialog extends DialogFragment {
    View mView;
    String mCasaAccount;
    FirebaseDatabase database;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.member_invite_dialog, null);
        builder.setView(mView)
                .setTitle("Invite Member")
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText nameEditText = (EditText) mView.findViewById(R.id.invite_name_textedit);
                        String name = nameEditText.getText().toString();
                        EditText emailEditText = (EditText) mView.findViewById(R.id.invite_email_textedit);
                        String email = emailEditText.getText().toString();

                        mCasaAccount = Utility.getCasaAccount(getActivity());
                        CasaMember casaMember = new CasaMember(name,email,"Pending","Admin");
                        DatabaseReference ref = database.getReference( mCasaAccount + "/Members");
                        ref.push().setValue(casaMember);
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
