package com.knightedge.bison.micasa;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.knightedge.bison.micasa.Adapters.MembersListAdapter;
import com.knightedge.bison.micasa.Dialogs.InviteMemberDialog;
import com.knightedge.bison.micasa.data.MiCasaContract.MemberEntry;
import com.knightedge.bison.micasa.model.CasaMember;


/**
 * Created by Bison on 24/10/2017.
 */

public class MembersActivity extends AppCompatActivity implements ValueEventListener, LoaderManager.LoaderCallbacks<Cursor> {
    String mCasaAccount;
    DatabaseReference mFirebaseMembers;
    ValueEventListener valueEventListener;
    public static MembersListAdapter mMembersAdapter;
    CursorLoader cursorLoader;
    ProgressDialog mProgressDialog;
    FirebaseDatabase database ;

    private static final int MEMBERS_LOADER = 0;
    private static final String[] MEMBER_COLUMNS = {
            MemberEntry.TABLE_NAME + "." + MemberEntry._ID,
            MemberEntry.COLUMN_MEMBER_NAME,
            MemberEntry.COLUMN_MEMBER_EMAIL,
            MemberEntry.COLUMN_MEMBER_STATUS,
            MemberEntry.COLUMN_MEMBER_RIGHTS
    };

    public static final int _ID = 0;
    public static final int COL_MEMBER_NAME = 1;
    public static final int COL_MEMBER_EMAIL = 2;
    public static final int COL_MEMBER_STATUS = 3;
    public static final int COL_MEMBER_RIGHTS = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_members);
        ListView membersListView = (ListView) findViewById(R.id.listView_members);
        mMembersAdapter = new MembersListAdapter(this, null, 0);
        membersListView.setAdapter(mMembersAdapter);

        mCasaAccount = Utility.getCasaAccount(this);
        database = FirebaseDatabase.getInstance();
        mFirebaseMembers = database.getReference(mCasaAccount + "/Members");
        valueEventListener = mFirebaseMembers.addValueEventListener(this);
        showProgressDialog("Loading ...");
        getSupportLoaderManager().initLoader(MEMBERS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri membersUri = MemberEntry.CONTENT_URI;
        cursorLoader = new CursorLoader(
                this,
                membersUri,
                MEMBER_COLUMNS,
                null,
                null,
                null
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMembersAdapter.swapCursor(data);
        mProgressDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMembersAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_members, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_member) {
            DialogFragment invite = new InviteMemberDialog();
            invite.show(getSupportFragmentManager(), "invite");
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getChildrenCount() > 0) {

            String email, name, status, rights;
            CasaMember casaMember;

            for (DataSnapshot child : dataSnapshot.getChildren()) {
                casaMember = child.getValue(CasaMember.class);
                name = casaMember.getName();
                email = casaMember.getEmail();
                status = casaMember.getStatus();
                rights = casaMember.getRights();

                Cursor cursor = getMemberRecord(email);

                if (!cursor.moveToFirst()) {
                    ContentValues memberValues = new ContentValues();
                    memberValues.put(MemberEntry.COLUMN_MEMBER_NAME, name);
                    memberValues.put(MemberEntry.COLUMN_MEMBER_EMAIL, email);
                    memberValues.put(MemberEntry.COLUMN_MEMBER_STATUS, status);
                    memberValues.put(MemberEntry.COLUMN_MEMBER_RIGHTS, rights);

                    getContentResolver().insert(MemberEntry.CONTENT_URI, memberValues);
                }
                cursor.close();
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private Cursor getMemberRecord(String email) {
        String[] projection = {MemberEntry._ID};
        String where = MemberEntry.COLUMN_MEMBER_EMAIL + " = ? ";
        String[] whereArgs = {email};
        Cursor cursor;

        cursor = getContentResolver().query(
                MemberEntry.CONTENT_URI,
                projection,
                where,
                whereArgs,
                null);
        return cursor;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseMembers.removeEventListener(valueEventListener);
    }

    private void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }
}
