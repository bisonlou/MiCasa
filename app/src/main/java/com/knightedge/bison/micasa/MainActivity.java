package com.knightedge.bison.micasa;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.knightedge.bison.micasa.Adapters.SectionsPagerAdapter;
import com.knightedge.bison.micasa.Dialogs.AddItemDialog;
import com.knightedge.bison.micasa.Dialogs.AddOrderDialog;
import com.knightedge.bison.micasa.Dialogs.DeleteInventoryDialog;
import com.knightedge.bison.micasa.Dialogs.EditInventoryDialog;
import com.knightedge.bison.micasa.Dialogs.JoinCasaDialog;
import com.knightedge.bison.micasa.Dialogs.UpdatePriceDialog;
import com.knightedge.bison.micasa.Fragments.InventoryFragment;
import com.knightedge.bison.micasa.Fragments.OrdersFragment;
import com.knightedge.bison.micasa.data.MiCasaContract.MemberEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.OrderEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.model.CasaMember;


public class MainActivity extends AppCompatActivity implements ActionBar.TabListener,
        InventoryFragment.CallBack, OrdersFragment.CallBack, AddItemDialog.CallBack,
        AddOrderDialog.CallBack, DeleteInventoryDialog.CallBack, EditInventoryDialog.CallBack,
        UpdatePriceDialog.CallBack, JoinCasaDialog.CallBack {

    public static String SELECTED_TAB = "selected_tab";
    public static final String SHARED_PREF_FILE = "com.knightedge.bison.micasa";
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    static final String TAG = "MainActivity";
    ProgressDialog mProgressDialog;
    ActionBar actionBar;
    String mNewCasaAccount;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        String casaAccount = Utility.getCasaAccount(this);
        actionBar.setSubtitle(casaAccount);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager)

                findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_join_casa) {
            DialogFragment join = new JoinCasaDialog();
            join.show(getSupportFragmentManager(), "join");
            return true;
        } else if (id == R.id.action_members) {
            Intent intent = new Intent(this, MembersActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onInventoryDataChange(String itemName, String itemUnits, double price) {
        Cursor cursor = getItemRecord(itemName);

        if (!cursor.moveToFirst()) {
            ContentValues inventoryValues = new ContentValues();
            inventoryValues.put(InventoryEntry.COLUMN_ITEM_NAME, itemName);
            inventoryValues.put(InventoryEntry.COLUMN_ITEM_UNITS, itemUnits);
            inventoryValues.put(InventoryEntry.COLUMN_ITEM_UNIT_PRICE, price);

            getContentResolver().insert(InventoryEntry.CONTENT_URI, inventoryValues);
        }
        cursor.close();
    }

    @Override
    public void onFirebaseOrderRemoved(String itemName) {
        int itemId;
        Cursor cursor = getItemRecord(itemName);

        if (cursor.moveToFirst()) {
            itemId = cursor.getInt(InventoryFragment.COL_ITEM_ID);

            String deleteOrderWhere = OrderEntry.COLUMN_ITEM_ID + " = ? ";
            String[] deleteWhereArgs = {Long.toString(itemId)};
            getContentResolver().delete(OrderEntry.CONTENT_URI, deleteOrderWhere, deleteWhereArgs);
        }
        cursor.close();
    }

    @Override
    public void onFirebaseItemRemoved(String itemName) {
        int itemId;
        Cursor cursor = getItemRecord(itemName);

        if (cursor.moveToFirst()) {
            itemId = cursor.getInt(InventoryFragment.COL_ITEM_ID);

            String deleteOrderWhere = OrderEntry.COLUMN_ITEM_ID + " = ? ";
            String deleteInventoryWhere = InventoryEntry._ID + " = ? ";
            String[] deleteWhereArgs = {Long.toString(itemId)};


            getContentResolver().delete(OrderEntry.CONTENT_URI, deleteOrderWhere, deleteWhereArgs);
            getContentResolver().delete(InventoryEntry.CONTENT_URI, deleteInventoryWhere, deleteWhereArgs);
        }
        cursor.close();
    }

    @Override
    public void onFirebaseItemChanged(String itemName, String itemUnits, double price) {
        int itemId;
        Cursor cursor = getItemRecord(itemName);

        if (cursor.moveToFirst()) {
            itemId = cursor.getInt(InventoryFragment.COL_ITEM_ID);
            updateLocalDb(itemId, itemName, itemUnits, price);
        }
        cursor.close();
    }

    @Override
    public void onOrderDataChange(String itemName, String orderDate, double quantity, int priority) {
        int itemId;
        Cursor cursor = getItemRecord(itemName);

        if (cursor.moveToFirst()) {
            itemId = cursor.getInt(InventoryFragment.COL_ITEM_ID);
            int itemCount;
            itemCount = getItemCount(itemId);

            if (itemCount == 0) {
                insertOrderToLocalDb(itemId, orderDate, quantity, 0);
                //showNotification(itemName, quantity);
            }
        }
        cursor.close();
    }

    @Override
    public void onOrderDone(Uri uri, String where, String[] whereArgs) {
        getContentResolver().delete(uri, where, whereArgs);
    }

    @Override
    public void onAddItem(ContentValues inventoryValues) {
        getContentResolver().insert(InventoryEntry.CONTENT_URI, inventoryValues);
    }

    @Override
    public void onAddOrder(ContentValues orderValues) {
        getContentResolver().insert(OrderEntry.CONTENT_URI, orderValues);
    }

    @Override
    public void onUpdatePrice(Uri inventoryUri, Uri orderUri, ContentValues
            inventoryValues, String orderWhere, String inventoryWhere, String[] whereArgs) {
        getContentResolver().delete(orderUri, orderWhere, whereArgs);
        getContentResolver().update(inventoryUri, inventoryValues, inventoryWhere, whereArgs);

    }

    @Override
    public void onDeleteInventory(String orderWhere, String inventoryWhere, String[] whereArgs) {
        getContentResolver().delete(OrderEntry.CONTENT_URI,
                orderWhere,
                whereArgs);

        getContentResolver().delete(InventoryEntry.CONTENT_URI,
                inventoryWhere,
                whereArgs);

    }

    @Override
    public void onEditInventory(Uri inventoryUri, ContentValues inventoryValues, String
            where, String[] whereArgs) {
        getContentResolver().update(inventoryUri, inventoryValues, where, whereArgs);
    }


    private int getItemCount(int itemId) {
        Uri itemOrderUri = OrderEntry.buildItemOrders(Long.toString(itemId));
        String[] projection = {OrderEntry.COLUMN_ITEM_ID};
        String where = OrderEntry.COLUMN_ITEM_ID + " = ? ";
        String[] whereArgs = new String[]{Long.toString(itemId)};
        int itemCount;

        Cursor cursor = getContentResolver().query(
                itemOrderUri,
                projection,
                where,
                whereArgs,
                null
        );

        itemCount = cursor.getCount();
        return itemCount;
    }

    @Override
    public void onRequestJoinCasa(String casaAccount) {
        showProgressDialog("Requesting to join " + casaAccount + " ...");
        mNewCasaAccount = casaAccount;
        database = FirebaseDatabase.getInstance();
        DatabaseReference membersRef = database.getReference(mNewCasaAccount + "/Members");
        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    CasaMember casaMember;
                    String email;
                    Boolean invited = false;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        casaMember = child.getValue(CasaMember.class);
                        email = casaMember.getEmail();
                        String myEmail = Utility.getAccountEmail(MainActivity.this);

                        if (email.equals(myEmail)) {
                            SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            editor.putString(getString(R.string.casa_account), mNewCasaAccount).apply();

                            MainActivity.this.getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
                            MainActivity.this.getContentResolver().delete(OrderEntry.CONTENT_URI, null, null);
                            MainActivity.this.getContentResolver().delete(MemberEntry.CONTENT_URI, null, null);
                            invited = true;
                            mProgressDialog.dismiss();
                            MainActivity.this.recreate();
                            break;
                        }
                    }
                    if (!invited) {
                        Toast.makeText(MainActivity.this, "Sorry, but you have not been invited to this Casa", Toast.LENGTH_SHORT).show();
                    }
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private Cursor getItemRecord(String itemName) {
        String[] projection = {InventoryEntry._ID};
        String where = InventoryEntry.COLUMN_ITEM_NAME + " = ? ";
        String[] whereArgs = {itemName};
        Cursor cursor;

        cursor = getContentResolver().query(
                InventoryEntry.CONTENT_URI,
                projection,
                where,
                whereArgs,
                null);
        return cursor;
    }

    private void insertOrderToLocalDb(int itemId, String orderDate, double quantity, int priority) {
        ContentValues orderValues = new ContentValues();
        orderValues.put(OrderEntry.COLUMN_ITEM_ID, itemId);
        orderValues.put(OrderEntry.COLUMN_ORDER_DATE, orderDate);
        orderValues.put(OrderEntry.COLUMN_ORDER_QUANTITY, quantity);
        orderValues.put(OrderEntry.COLUMN_ORDER_PRIORITY, priority);
        getContentResolver().insert(OrderEntry.CONTENT_URI, orderValues);
    }

    private void updateLocalDb(int itemId, String itemName, String units, double price){
        ContentValues inventoryValues = new ContentValues();
        inventoryValues.put(InventoryEntry.COLUMN_ITEM_NAME, itemName);
        inventoryValues.put(InventoryEntry.COLUMN_ITEM_UNITS, units);
        inventoryValues.put(InventoryEntry.COLUMN_ITEM_UNIT_PRICE, price);

        String where = InventoryEntry._ID +  " = ?";
        String[] whereArgs = {Long.toString(itemId)};

        getContentResolver().update(InventoryEntry.CONTENT_URI,inventoryValues, where,whereArgs);
    }

    private void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

   /* private void showNotification(String itemName, double quantity) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New Order")
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setContentText("Dont forget to buy " + itemName);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }*/
}