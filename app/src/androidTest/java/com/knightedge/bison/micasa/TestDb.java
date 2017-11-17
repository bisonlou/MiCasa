package com.knightedge.bison.micasa;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.OrderEntry;
import com.knightedge.bison.micasa.data.MiCasaDbHelper;

import java.util.Map;
import java.util.Set;


/**
 * Created by Bison on 25/07/2017.
 */
public class TestDb extends AndroidTestCase {
    /*public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(MiCasaDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MiCasaDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    String TEST_ITEM_NAME = "Rice";
    public static final String LOG_TAG = TestDb.class.getSimpleName();
    ContentValues getInventoryContentValues() {

        String testItemDesc = "Maize";
        String testItemUnits = "Kgs";
        double testUnitPrice = 2500.00;

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_ITEM_NAME, TEST_ITEM_NAME);
        values.put(InventoryEntry.COLUMN_ITEM_UNITS, testItemUnits);
        values.put(InventoryEntry.COLUMN_ITEM_UNIT_PRICE, testUnitPrice);

        return values;
    }


   ContentValues getOrderContentValues(long inventoryRowId) {
        ContentValues orderValues = new ContentValues();
        orderValues.put(OrderEntry.COLUMN_ITEM_ID, inventoryRowId);
        orderValues.put(OrderEntry.COLUMN_ORDER_DATE, "20171205");
        orderValues.put(OrderEntry.COLUMN_ORDER_QUANTITY, 5.0);
        orderValues.put(OrderEntry.COLUMN_ORDER_PRIORITY,0);

        return orderValues;
    }

    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(-1 == idx);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
    }


    public void testInsertReadDb() {

        MiCasaDbHelper dbHelper = new MiCasaDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long inventoryRowId;
        inventoryRowId = db.insert(InventoryEntry.TABLE_NAME, null, getInventoryContentValues());
        assertTrue(inventoryRowId != -1);
        Log.d(LOG_TAG, "New row id: " + inventoryRowId);


        Cursor cursor = db.query(
               InventoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );


        if (cursor.moveToFirst()) {
            //validateCursor(getInventoryContentValues(), cursor);


            long orderRowId;
            orderRowId = db.insert(OrderEntry.TABLE_NAME, null, getOrderContentValues(inventoryRowId));
            assertTrue(orderRowId != -1);
            Log.d(LOG_TAG, "Order row id: " + orderRowId);

            Cursor orderCursor = db.query(
                    OrderEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (orderCursor.moveToFirst()) {
                //validateCursor(getOrderContentValues(inventoryRowId),orderCursor);
            } else {
                fail("No order data returned");
            }

        } else {
            fail("No values returned :(");
        }
    }*/


}
