package com.knightedge.bison.micasa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.knightedge.bison.micasa.model.CasaMember;

import static android.text.TextUtils.isEmpty;
import static com.knightedge.bison.micasa.MainActivity.SHARED_PREF_FILE;

/**
 * Created by Bison on 15/10/2017.
 */

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    EditText casaAccountNameEditText;
    String mCasaAccountName;
    FirebaseDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      Firebase.setAndroidContext(this);
        database = FirebaseDatabase.getInstance();
        setContentView(R.layout.welcome_activity);
        getSupportActionBar().hide();

        TextView welcomeTextView = (TextView) findViewById(R.id.welcome_username);
        casaAccountNameEditText = (EditText) findViewById(R.id.casa_name_edittext);
        findViewById(R.id.welcome_finish_button).setOnClickListener(this);

        String mAccountName = Utility.getAccountName(this);
        welcomeTextView.setText("Welcome " + mAccountName);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.welcome_finish_button) {
            if (!isEmpty(casaAccountNameEditText.getText())) {
                mCasaAccountName = casaAccountNameEditText.getText().toString();

                String userName = Utility.getAccountName(this);
                String userEmail = Utility.getAccountEmail(this);

                CasaMember casaMember;
                casaMember = new CasaMember(userName,userEmail, "Active", "Admin");
                DatabaseReference ref = database.getReference(mCasaAccountName + "/Members");
                ref.push().setValue(casaMember);

                SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(getString(R.string.casa_account), mCasaAccountName).apply();

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter a Casa account name", Toast.LENGTH_LONG).show();
            }
        }
    }
}
