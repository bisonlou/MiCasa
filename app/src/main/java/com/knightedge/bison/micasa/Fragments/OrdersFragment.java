package com.knightedge.bison.micasa.Fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.knightedge.bison.micasa.Adapters.OrdersListAdapter;
import com.knightedge.bison.micasa.Dialogs.UpdatePriceDialog;
import com.knightedge.bison.micasa.R;
import com.knightedge.bison.micasa.Utility;
import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.OrderEntry;
import com.knightedge.bison.micasa.model.ItemOrder;

/**
 * Created by Bison on 30/08/2017.
 */
public class OrdersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public OrdersFragment() {
    }

    private ChildEventListener childEventListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static final int ORDERS_LOADER = 0;
    private static final String[] ORDER_COLUMNS = {
            OrderEntry.TABLE_NAME + "." + OrderEntry._ID,
            OrderEntry.COLUMN_ITEM_ID,
            OrderEntry.COLUMN_ORDER_DATE,
            OrderEntry.COLUMN_ORDER_QUANTITY,
            OrderEntry.COLUMN_ORDER_PRIORITY,
            InventoryEntry.COLUMN_ITEM_NAME,
            InventoryEntry.COLUMN_ITEM_UNITS,
            InventoryEntry.COLUMN_ITEM_UNIT_PRICE

    };
    public static final int _ID = 0;
    public static final int COL_ITEM_ID = 1;
    public static final int COL_ORDER_DATE = 2;
    public static final int COL_ORDER_QTY = 3;
    public static final int COL_ORDER_PRIORITY = 4;
    public static final int COL_ITEM_NAME = 5;
    public static final int COL_ITEM_UNITS = 6;
    public static final int COL_ITEM_PRICE = 7;


    public static OrdersListAdapter mOrdersAdapter;
    CursorLoader cursorLoader;
    String mCasaAccount;
    DatabaseReference mFirebaseOrder;
    View mRootView;


    public interface CallBack {
        void onOrderDataChange(String itemName, String orderDate, double quantity, int priority);
        void onOrderDone(Uri uri, String where, String[] whereArgs);
        void onFirebaseOrderRemoved(String itemName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_orders, container, false);

        mCasaAccount = Utility.getCasaAccount(getActivity());
        mFirebaseOrder = database.getReference(mCasaAccount + "/Order");
        createOrderLister();

        ListView orderListView = (ListView) mRootView.findViewById(R.id.listView_orders);
        mOrdersAdapter = new OrdersListAdapter(getActivity(), null, 0);
        orderListView.setAdapter(mOrdersAdapter);

        View emptyOrderResults = mRootView.findViewById(R.id.empty_order_textview);
        orderListView.setEmptyView(emptyOrderResults);

        registerForContextMenu(orderListView);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (childEventListener == null) {
            createOrderLister();
            mFirebaseOrder.addChildEventListener(childEventListener);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ORDERS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri ordersUri = OrderEntry.CONTENT_URI;
        cursorLoader = new CursorLoader(
                getActivity(),
                ordersUri,
                ORDER_COLUMNS,
                null,
                null,
                null
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mOrdersAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mOrdersAdapter.swapCursor(null);
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Cursor cursor = (Cursor) mOrdersAdapter.getItem(info.position);
        String title = cursor.getString(COL_ITEM_NAME);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View titleView = layoutInflater.inflate(R.layout.dialog_add_item_title, null);

        TextView text = (TextView) titleView.findViewById(R.id.add_item_textview);
        text.setText(title);
        menu.setHeaderView(titleView);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.order_contextual_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_order_done:
                orderDone(info.position);
                return true;
            case R.id.action_done_price_update:
                orderDonePriceUpdate(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void orderDone(int position) {
        Cursor itemCursor = mOrdersAdapter.getCursor();
        if (itemCursor.moveToPosition(position)) {
            int orderId = itemCursor.getInt(_ID);
            Uri uri = OrderEntry.CONTENT_URI;
            String where = OrderEntry._ID + " = ? ";
            String[] whereArgs = {Long.toString(orderId)};

            ((OrdersFragment.CallBack) getActivity()).onOrderDone(uri, where, whereArgs);
            deleteOrderFromFirebase(itemCursor, position);
        }

        itemCursor.close();
    }

    private void orderDonePriceUpdate(int position) {
        Cursor cursor = mOrdersAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            final int itemId = cursor.getInt(COL_ITEM_ID);
            String itemName = getItemName(itemId);

            DialogFragment updatePrice = new UpdatePriceDialog();
            Bundle args = new Bundle();
            args.putInt(OrderEntry.COLUMN_ITEM_ID, itemId);
            args.putString(InventoryEntry.COLUMN_ITEM_NAME, itemName);
            updatePrice.setArguments(args);

            updatePrice.show(getFragmentManager(), "updatePrice");
        }
    }


    private void deleteOrderFromFirebase(Cursor cursor, int position) {
        if (cursor.moveToPosition(position)) {
            String itemName = cursor.getString(OrdersFragment.COL_ITEM_NAME);

            mCasaAccount = Utility.getAccountName(getActivity());
            mFirebaseOrder = database.getReference(mCasaAccount + "/Order");
            mFirebaseOrder.child(itemName).removeValue();
        }
    }

    private void createOrderLister() {
        childEventListener = mFirebaseOrder.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String itemName, orderDate;
                double quantity;
                ItemOrder itemOrder;
                itemOrder = dataSnapshot.getValue(ItemOrder.class);

                itemName = dataSnapshot.getKey();
                orderDate = itemOrder.getDate();
                quantity = itemOrder.getQuantity();
                int priority = 0;

                ((OrdersFragment.CallBack) getActivity()).onOrderDataChange(itemName, orderDate, quantity, priority);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String itemName;
                itemName = dataSnapshot.getKey();
                ((CallBack) getActivity()).onFirebaseOrderRemoved(itemName);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private String getItemName(int itemId) {
        String[] projection = {InventoryEntry.COLUMN_ITEM_NAME};
        String where = InventoryEntry._ID + " = ? ";
        String[] whereArgs = {Long.toString(itemId)};
        Cursor cursor;
        String itemName = "";

        cursor = getActivity().getContentResolver().query(
                InventoryEntry.CONTENT_URI,
                projection,
                where,
                whereArgs,
                null);
        if (cursor.moveToFirst()) {
            itemName = cursor.getString(0);
        }
        return itemName;
    }

    @Override
    public void onPause() {
        super.onPause();
        mFirebaseOrder.removeEventListener(childEventListener);
        childEventListener = null;
    }
}
