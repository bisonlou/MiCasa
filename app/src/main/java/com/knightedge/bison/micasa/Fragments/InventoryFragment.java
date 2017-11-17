package com.knightedge.bison.micasa.Fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.knightedge.bison.micasa.Adapters.InventoryListAdapter;
import com.knightedge.bison.micasa.Dialogs.AddItemDialog;
import com.knightedge.bison.micasa.Dialogs.AddOrderDialog;
import com.knightedge.bison.micasa.Dialogs.DeleteInventoryDialog;
import com.knightedge.bison.micasa.Dialogs.EditInventoryDialog;
import com.knightedge.bison.micasa.MainActivity;
import com.knightedge.bison.micasa.R;
import com.knightedge.bison.micasa.Utility;
import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.model.InventoryItem;

/**
 * Created by Bison on 30/08/2017.
 */

public class InventoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public InventoryFragment() {
    }

    private ChildEventListener childEventListener;
    private static final int INVENTORY_LOADER = 1;
    public static final String[] INVENTORY_COLUMNS = {
            InventoryEntry.TABLE_NAME + "." + InventoryEntry._ID,
            InventoryEntry.COLUMN_ITEM_NAME,
            InventoryEntry.COLUMN_ITEM_UNIT_PRICE,
            InventoryEntry.COLUMN_ITEM_UNITS

    };
    public static final int COL_ITEM_ID = 0;
    public static final int COL_ITEM_NAME = 1;
    public static final int COL_ITEM_PRICE = 2;
    public static final int COL_ITEM_UNITS = 3;


    public static InventoryListAdapter mInventoryAdapter;
    String mOrderBy;
    View mRootView;
    CursorLoader cursorLoader;
    String mCasaAccount;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mFirebaseInventory;

    public interface CallBack {
        void onInventoryDataChange(String itemName, String itemUnits, double price);
        void onFirebaseItemChanged(String itemName, String itemUnits, double price);
        void onFirebaseItemRemoved(String itemName);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (childEventListener == null) {
            createInventoryListener();
            mFirebaseInventory.addChildEventListener(childEventListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        cursorLoader.cancelLoadInBackground();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        setHasOptionsMenu(true);
        Uri inventoryUri = InventoryEntry.CONTENT_URI;
        mOrderBy = InventoryEntry.COLUMN_ITEM_NAME + " ASC";

        cursorLoader = new CursorLoader(
                getActivity(),
                inventoryUri,
                INVENTORY_COLUMNS,
                null,
                null,
                mOrderBy
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mInventoryAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mInventoryAdapter.swapCursor(null);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_inventory, container, false);

        mCasaAccount = Utility.getCasaAccount(getActivity());
        mFirebaseInventory = database.getReference(mCasaAccount + "/Inventory");
        createInventoryListener();

        FloatingActionButton fab = (FloatingActionButton) mRootView.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertItem();
            }
        });

        ListView inventoryListView = (ListView) mRootView.findViewById(R.id.listView_inventory);
        mInventoryAdapter = new InventoryListAdapter(getActivity(), null, 0);
        inventoryListView.setAdapter(mInventoryAdapter);

        registerForContextMenu(inventoryListView);
        return mRootView;
    }

    private void insertItem() {
        DialogFragment addItem = new AddItemDialog();
        addItem.show(getFragmentManager(), "addItem");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Cursor cursor = (Cursor) mInventoryAdapter.getItem(info.position);
        String title = (cursor.getString(COL_ITEM_NAME));

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View titleView = layoutInflater.inflate(R.layout.dialog_add_item_title, null);
        TextView text = (TextView) titleView.findViewById(R.id.add_item_textview);
        text.setText(title);
        menu.setHeaderView(titleView);

        MenuInflater inflater = getActivity().getMenuInflater();
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
            int itemId = cursor.getInt(COL_ITEM_ID);
            String itemName = cursor.getString(COL_ITEM_NAME);
            String itemUnits = cursor.getString(COL_ITEM_UNITS);

            DialogFragment addQuantity = new AddOrderDialog();

            Bundle args = new Bundle();
            args.putInt(InventoryEntry._ID, itemId);
            args.putString(InventoryEntry.COLUMN_ITEM_NAME, itemName);
            args.putString(InventoryEntry.COLUMN_ITEM_UNITS, itemUnits);
            addQuantity.setArguments(args);
            addQuantity.show(getFragmentManager(), "addQuantity");
        }
    }


    private void editItem(int position) {
        Cursor cursor = mInventoryAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            final int itemId = cursor.getInt(COL_ITEM_ID);
            String itemName = cursor.getString(COL_ITEM_NAME);
            String itemUnits = cursor.getString(COL_ITEM_UNITS);
            String price = String.format("%s", cursor.getDouble(COL_ITEM_PRICE));
            DialogFragment editInventory = new EditInventoryDialog();

            Bundle args = new Bundle();
            args.putInt(InventoryEntry._ID, itemId);
            args.putString(InventoryEntry.COLUMN_ITEM_NAME, itemName);
            args.putString(InventoryEntry.COLUMN_ITEM_UNITS, itemUnits);
            args.putString(InventoryEntry.COLUMN_ITEM_UNIT_PRICE, price);

            editInventory.setArguments(args);
            editInventory.show(getFragmentManager(), "editInventory");
        }
    }

    private void deleteItem(int position) {
        Cursor cursor = mInventoryAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            final int itemId = cursor.getInt(COL_ITEM_ID);
            String itemName = cursor.getString(COL_ITEM_NAME);

            DialogFragment deleteInventory = new DeleteInventoryDialog();

            Bundle args = new Bundle();
            args.putInt(InventoryEntry._ID, itemId);
            args.putString(InventoryEntry.COLUMN_ITEM_NAME, itemName);
            deleteInventory.setArguments(args);
            deleteInventory.show(getFragmentManager(), "deleteInventory");
        }
    }

    private void createInventoryListener() {
        childEventListener = mFirebaseInventory.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String itemName, itemUnits;
                double price;
                InventoryItem inventoryItem;

                itemName = dataSnapshot.getKey();
                inventoryItem = dataSnapshot.getValue(InventoryItem.class);
                itemUnits = inventoryItem.getUnits();
                price = inventoryItem.getUnitPrice();

                ((CallBack) getActivity()).onInventoryDataChange(itemName, itemUnits, price);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                InventoryItem inventoryItem;
                inventoryItem = dataSnapshot.getValue(InventoryItem.class);
                String itemName = dataSnapshot.getKey();
                String itemUnits = inventoryItem.getUnits();
                double itemPrice = inventoryItem.getUnitPrice();

                ((CallBack) getActivity()).onFirebaseItemChanged(itemName, itemUnits, itemPrice);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String itemName;
                itemName = dataSnapshot.getKey();
                ((CallBack) getActivity()).onFirebaseItemRemoved(itemName);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mFirebaseInventory.removeEventListener(childEventListener);
        childEventListener = null;
    }
}
