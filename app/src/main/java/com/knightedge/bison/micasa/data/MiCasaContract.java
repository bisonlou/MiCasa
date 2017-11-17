package com.knightedge.bison.micasa.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Bison on 24/07/2017.
 */
public class MiCasaContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.knightedge.bison.micasa";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_ORDER = "orders";
    public static final String PATH_INVENTORY = "inventory";
    public static final String PATH_MEMBER = "members";

    public static final class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVENTORY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" +
                        PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" +
                        PATH_INVENTORY;

        public static final String TABLE_NAME = "inventory";

        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_ITEM_UNITS = "units";
        public static final String COLUMN_ITEM_UNIT_PRICE = "unit_price";

        public static Uri buildInventoryUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

    }


    public static final class MemberEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEMBER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" +
                        PATH_MEMBER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" +
                        PATH_MEMBER;

        public static final String TABLE_NAME = "members";

        public static final String COLUMN_MEMBER_NAME = "name";
        public static final String COLUMN_MEMBER_EMAIL = "email";
        public static final String COLUMN_MEMBER_STATUS = "status";
        public static final String COLUMN_MEMBER_RIGHTS = "rights";

        public static Uri buildMemberUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

    }


    public static final class OrderEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" +
                        PATH_ORDER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" +
                        PATH_ORDER;

        public static final String TABLE_NAME = "orders";

        public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_ORDER_DATE = "order_date";
        public static final String COLUMN_ORDER_QUANTITY = "order_qty";
        public static final String COLUMN_ORDER_PRIORITY = "order_priority";

        public static Uri buildOrderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildItemOrders(String itemId) {
            return CONTENT_URI.buildUpon().appendPath(itemId).build();
        }

        public static Uri buildItemOrdersWithStartDate(String itemId, String startDate) {
            return CONTENT_URI.buildUpon().appendPath(itemId)
                    .appendQueryParameter(COLUMN_ORDER_DATE, startDate).build();
        }


        public static String getItemIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_ORDER_DATE);
            if (null != dateString && dateString.length() > 0)
                return dateString;
            else
                return null;
        }

    }

    public static final String DATE_FORMAT = "yyyyMMdd";

    public static String getDbDateString(Date date) {
        //Because the API returns a unix timestamp (measured in seconds)
        //it must be converted to milliseconds in order to convert to a valid date
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     *
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
