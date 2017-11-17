package com.knightedge.bison.micasa.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.OrderEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.MemberEntry;

/**
 * Created by Bison on 25/07/2017.
 */
public class MiCasaProvider extends ContentProvider {


    static final int ORDER = 100;
    static final int ORDER_WITH_INVENTORY = 101;
    static final int ORDER_WITH_INVENTORY_AND_DATE = 102;
    static final int INVENTORY = 300;
    static final int INVENTORY_ID = 301;
    static final int MEMBER = 400;
    static final int MEMBER_ID = 401;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MiCasaDbHelper mOpenHelper;

    private static final SQLiteQueryBuilder sOrderByItemQueryBuilder;

    static {
        sOrderByItemQueryBuilder = new SQLiteQueryBuilder();
        sOrderByItemQueryBuilder.setTables(
                OrderEntry.TABLE_NAME + " INNER JOIN " +
                        InventoryEntry.TABLE_NAME + " ON " +
                        OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_ITEM_ID
                        + " = " + InventoryEntry.TABLE_NAME + "." + InventoryEntry._ID);
    }

    private static final String sItemSelection = InventoryEntry.TABLE_NAME +
            "." + InventoryEntry._ID + " = ?";

    private static final String sItemWithStartDateSelection = InventoryEntry.TABLE_NAME +
            "." + InventoryEntry._ID + " = ? AND "
            + OrderEntry.COLUMN_ORDER_DATE + " >= ?";

    private static final String sItemWithDaySelection = InventoryEntry.TABLE_NAME +
            "." + InventoryEntry._ID + " = ? AND "
            + OrderEntry.COLUMN_ORDER_DATE + " = ?";


    private Cursor getOrderByItemIdWithDate(Uri uri, String[] projection, String sortOrder) {

        String itemId = OrderEntry.getItemIdFromUri(uri);
        String day = OrderEntry.getStartDateFromUri(uri);

        return sOrderByItemQueryBuilder.query(mOpenHelper.getWritableDatabase(),
                projection,
                sItemWithDaySelection,
                new String[]{itemId, day},
                null,
                null,
                sortOrder
        );
    }

    ;

    private Cursor getDetailedOrders(Uri uri, String[] projection, String sortOrder) {
        return sOrderByItemQueryBuilder.query(mOpenHelper.getWritableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                null);
    }

    ;

    private Cursor getOrderByItemId(Uri uri, String[] projection, String sortOrder) {

        String itemId = OrderEntry.getItemIdFromUri(uri);
        String startDate = OrderEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == null) {
            selection = sItemSelection;
            selectionArgs = new String[]{itemId};

        } else {
            selection = sItemWithStartDateSelection;
            selectionArgs = new String[]{itemId, startDate};
        }

        return sOrderByItemQueryBuilder.query(mOpenHelper.getWritableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    };


    private static UriMatcher buildUriMatcher() {
//
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MiCasaContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MiCasaContract.PATH_ORDER, ORDER);
        matcher.addURI(authority, MiCasaContract.PATH_ORDER + "/*", ORDER_WITH_INVENTORY);
        matcher.addURI(authority, MiCasaContract.PATH_ORDER + "/*/#", ORDER_WITH_INVENTORY_AND_DATE);

        matcher.addURI(authority, MiCasaContract.PATH_INVENTORY, INVENTORY);
        matcher.addURI(authority, MiCasaContract.PATH_INVENTORY + "/#", INVENTORY_ID);

        matcher.addURI(authority, MiCasaContract.PATH_MEMBER, MEMBER);
        matcher.addURI(authority, MiCasaContract.PATH_MEMBER + "/#", MEMBER_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MiCasaDbHelper(getContext());
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case ORDER_WITH_INVENTORY_AND_DATE:
                retCursor = getOrderByItemIdWithDate(uri, projection, sortOrder);
                break;
            case ORDER_WITH_INVENTORY:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        OrderEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ORDER:
                retCursor = getDetailedOrders(uri, projection, sortOrder);
                break;

            case INVENTORY:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case INVENTORY_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        InventoryEntry.TABLE_NAME,
                        projection,
                        InventoryEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MEMBER:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MemberEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MEMBER_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MemberEntry.TABLE_NAME,
                        projection,
                        MemberEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ORDER_WITH_INVENTORY_AND_DATE:
                return OrderEntry.CONTENT_ITEM_TYPE;
            case ORDER_WITH_INVENTORY:
                return OrderEntry.CONTENT_TYPE;
            case ORDER:
                return OrderEntry.CONTENT_TYPE;
            case INVENTORY:
                return InventoryEntry.CONTENT_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            case MEMBER:
                return MemberEntry.CONTENT_TYPE;
            case MEMBER_ID:
                return MemberEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        switch (match) {
            case ORDER: {
                long _id = mOpenHelper.getWritableDatabase().insert(OrderEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = OrderEntry.buildOrderUri(_id);

                else
                    throw new SQLException("failed to insert row int " + uri);

                break;
            }
            case INVENTORY: {
                long _id = mOpenHelper.getWritableDatabase().insert(InventoryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = InventoryEntry.buildInventoryUri(_id);
                else
                    throw new SQLException("failed to insert row int " + uri);
                break;
            }
            case MEMBER: {
                long _id = mOpenHelper.getWritableDatabase().insert(MemberEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MemberEntry.buildMemberUri(_id);
                else
                    throw new SQLException("failed to insert row int " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: + uri");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ORDER: {
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(OrderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case INVENTORY: {
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case MEMBER: {
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: + uri");
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case ORDER:
                rowsUpdated = mOpenHelper.getWritableDatabase().update(OrderEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INVENTORY:
                rowsUpdated = mOpenHelper.getWritableDatabase().update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MEMBER:
                rowsUpdated = mOpenHelper.getWritableDatabase().update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: + uri");
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri returnUri = null;
        switch (match) {
            case ORDER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        Long _Id = db.insert(OrderEntry.TABLE_NAME, null, value);
                        if (_Id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }

    }


}




