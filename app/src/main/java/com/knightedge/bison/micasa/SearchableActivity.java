package com.knightedge.bison.micasa;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.knightedge.bison.micasa.Adapters.InventoryListAdapter;
import com.knightedge.bison.micasa.Dialogs.AddOrderDialog;
import com.knightedge.bison.micasa.Dialogs.DeleteInventoryDialog;
import com.knightedge.bison.micasa.Dialogs.EditInventoryDialog;
import com.knightedge.bison.micasa.Fragments.InventoryFragment;
import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.OrderEntry;

/**
 * Created by Bison on 06/10/2017.
 */

public class SearchableActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AddOrderDialog.CallBack, DeleteInventoryDialog.CallBack, EditInventoryDialog.CallBack {

    private String mQuery;
    public static InventoryListAdapter mInventoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView searchListView = (ListView) findViewById(R.id.seachable_listview);
        mInventoryAdapter = new InventoryListAdapter(this, null, 0);
        searchListView.setAdapter(mInventoryAdapter);
        View emptySearchResults = findViewById(R.id.empty_search_textview);
        searchListView.setEmptyView(emptySearchResults);

        registerForContextMenu(searchListView);


        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            getSupportLoaderManager().initLoader(3, null, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.textview_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            getSupportLoaderManager().restartLoader(3, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri inventoryUri = InventoryEntry.CONTENT_URI;
        final String where = InventoryEntry.COLUMN_ITEM_NAME + " LIKE ?";
        String[] whereArgs = {"%" + mQuery + "%"};

        CursorLoader cursorLoader = new CursorLoader(
                this,
                inventoryUri,
                InventoryFragment.INVENTORY_COLUMNS,
                where,
                whereArgs,
                InventoryEntry.COLUMN_ITEM_NAME
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d("Cursor count: ", Long.toString(cursor.getCount()));
        mInventoryAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mInventoryAdapter.swapCursor(null);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Cursor cursor = (Cursor) mInventoryAdapter.getItem(info.position);
        String title =cursor.getString(InventoryFragment.COL_ITEM_NAME);

        LayoutInflater layoutInflater = getLayoutInflater();
        View titleView = layoutInflater.inflate(R.layout.dialog_add_item_title, null);
        TextView text = (TextView) titleView.findViewById(R.id.add_item_textview);
        text.setText(title);
        menu.setHeaderView(titleView);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_contextual_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_order_item:
                addItemToOrders(info.position);
                return true;
            case R.id.action_edit_item:
                editItem(info.position);
                return true;
            case R.id.action_delete_item:
                deleteItem(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void addItemToOrders(int position) {
        Cursor cursor = mInventoryAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            int itemId = cursor.getInt(InventoryFragment.COL_ITEM_ID);
            String itemName = cursor.getString(InventoryFragment.COL_ITEM_NAME);
            String itemUnits = cursor.getString(InventoryFragment.COL_ITEM_UNITS);

            DialogFragment addQuantity = new AddOrderDialog();

            Bundle args = new Bundle();
            args.putInt(InventoryEntry._ID, itemId);
            args.putString(InventoryEntry.COLUMN_ITEM_NAME, itemName);
            args.putString(InventoryEntry.COLUMN_ITEM_UNITS, itemUnits);
            addQuantity.setArguments(args);
            addQuantity.show(getSupportFragmentManager(), "addQuantity");
        }
    }


    private void editItem(int position) {
        Cursor cursor = mInventoryAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            final int itemId = cursor.getInt(InventoryFragment.COL_ITEM_ID);
            String itemName = cursor.getString(InventoryFragment.COL_ITEM_NAME);
            String itemUnits = cursor.getString(InventoryFragment.COL_ITEM_UNITS);
            String price = String.format("%s", cursor.getDouble(InventoryFragment.COL_ITEM_PRICE));
            DialogFragment editInventory = new EditInventoryDialog();

            Bundle args = new Bundle();
            args.putInt(InventoryEntry._ID, itemId);
            args.putString(InventoryEntry.COLUMN_ITEM_NAME, itemName);
            args.putString(InventoryEntry.COLUMN_ITEM_UNITS, itemUnits);
            args.putString(InventoryEntry.COLUMN_ITEM_UNIT_PRICE, price);

            editInventory.setArguments(args);
            editInventory.show(getSupportFragmentManager(), "editInventory");
        }
    }

    private void deleteItem(int position) {
        Cursor cursor = mInventoryAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            final int itemId = cursor.getInt(InventoryFragment.COL_ITEM_ID);
            String itemName = cursor.getString(InventoryFragment.COL_ITEM_NAME);

            DialogFragment deleteInventory = new DeleteInventoryDialog();

            Bundle args = new Bundle();
            args.putInt(InventoryEntry._ID, itemId);
            args.putString(InventoryEntry.COLUMN_ITEM_NAME, itemName);
            deleteInventory.setArguments(args);
            deleteInventory.show(getSupportFragmentManager(), "deleteInventory");
        }
    }


    @Override
    public void onAddOrder(ContentValues orderValues) {
        getContentResolver().insert(OrderEntry.CONTENT_URI, orderValues);
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
    public void onEditInventory(Uri inventoryUri, ContentValues inventoryValues, String where, String[] whereArgs) {
        getContentResolver().update(inventoryUri, inventoryValues, where, whereArgs);
    }

}
