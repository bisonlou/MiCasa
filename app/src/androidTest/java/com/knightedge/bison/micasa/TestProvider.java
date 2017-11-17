package com.knightedge.bison.micasa;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.OrderEntry;

import java.util.Map;
import java.util.Set;

/**
 * Created by Bison on 31/08/2017.
 */
public class TestProvider extends AndroidTestCase {

    public void testDeleteAllRecords() {
        mContext.getContentResolver().delete(OrderEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);

        Cursor cursor = mContext.getContentResolver().query(OrderEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals(cursor.getCount(), 0);
        cursor.close();

        cursor = mContext.getContentResolver().query(InventoryEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals(cursor.getCount(), 0);
        cursor.close();

    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(OrderEntry.CONTENT_URI);
        assertEquals(type, OrderEntry.CONTENT_TYPE);
    }


    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    String TEST_ITEM_NAME = "Rice";


    ContentValues getInventoryContentValues() {
        String testItemDesc = "Super";
        String testItemUnits = "Kgs";
        double testUnitPrice = 2500.55;

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
        orderValues.put(OrderEntry.COLUMN_ORDER_QUANTITY, 5.01);
        orderValues.put(OrderEntry.COLUMN_ORDER_PRIORITY, 0);

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


    public void testInsertReadContentProvider() {

        long locationRowId;
        Uri locationUri = mContext.getContentResolver().insert(InventoryEntry.CONTENT_URI, getInventoryContentValues());
        locationRowId = ContentUris.parseId(locationUri);


        Cursor cursor = mContext.getContentResolver().query(
                InventoryEntry.buildInventoryUri(locationRowId),
                null,
                null,
                null,
                null
        );


        if (cursor.moveToFirst()) {
            //validateCursor(getInventoryContentValues(), cursor);

            long weatherRowId;
            Uri insertUri = mContext.getContentResolver().insert(OrderEntry.CONTENT_URI, getOrderContentValues(locationRowId));
            //weatherRowId = ContentUris.parseId(insertUri);


          /*  Cursor weatherCursor = mContext.getContentResolver().query(
                    OrderEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
*/

        /*    weatherCursor = mContext.getContentResolver().query(
                    OrderEntry.buildItemOrders(locationRowId),
                    null,
                    null,
                    null,
                    null
            );*/

            /*if (weatherCursor.moveToFirst()) {
                validateCursor(getOrderContentValues(locationRowId), weatherCursor);

            } else {
                fail("No weather data returned");
            }*/


            // weatherCursor.close();
            /*weatherCursor = mContext.getContentResolver().query(
                    WeatherContract.WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                    null,
                    null,
                    null,
                    null
            );

            if (weatherCursor.moveToFirst()) {
                //validateCursor(getWeatherContentValues(locationRowId), weatherCursor);
            } else {
                fail("No weather data returned");
            }*/


       /* } else {
            fail("No values returned :(");
        }*/

        }

    /*public void testUpdateLocation() {
       // testDeleteAllRecords();

        Long locationRowId;
        Uri locationUri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, getLocationContentValues());
        locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);

        ContentValues values = getLocationContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        int count = mContext.getContentResolver().update(
                WeatherContract.LocationEntry.CONTENT_URI,
                values,
                WeatherContract.LocationEntry._ID + " = ?",
                new String[] {Long.toString(locationRowId)}
        );
        assertEquals(count , 1);
    }*/


    }
}
