package com.knightedge.bison.micasa.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.knightedge.bison.micasa.data.MiCasaContract.InventoryEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.OrderEntry;
import com.knightedge.bison.micasa.data.MiCasaContract.MemberEntry;

/**
 * Created by Bison on 24/07/2017.
 */
public class MiCasaDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 9;

    public static final String DATABASE_NAME = "micasa.db";

    public MiCasaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
                InventoryEntry._ID + " INTEGER PRIMARY KEY," +
                InventoryEntry.COLUMN_ITEM_NAME + " TEXT UNIQUE NOT NULL, " +
                InventoryEntry.COLUMN_ITEM_UNITS + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_ITEM_UNIT_PRICE + " REAL NOT NULL, " +
                "UNIQUE (" + InventoryEntry.COLUMN_ITEM_NAME + ") ON " +
                "CONFLICT REPLACE);";

        final String SQL_CREATE_MEMBER_TABLE = "CREATE TABLE " + MemberEntry.TABLE_NAME + " (" +
                MemberEntry._ID + " INTEGER PRIMARY KEY," +
                MemberEntry.COLUMN_MEMBER_NAME + " TEXT NOT NULL, " +
                MemberEntry.COLUMN_MEMBER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                MemberEntry.COLUMN_MEMBER_STATUS + " TEXT NOT NULL, " +
                MemberEntry.COLUMN_MEMBER_RIGHTS + " TEXT NOT NULL, " +
                "UNIQUE (" + MemberEntry.COLUMN_MEMBER_EMAIL + ") ON " +
                "CONFLICT REPLACE);";

        final String SQL_CREATE_ORDER_TABLE = "CREATE TABLE " + OrderEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                OrderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                OrderEntry.COLUMN_ITEM_ID + " INTEGER NOT NULL, " +
                OrderEntry.COLUMN_ORDER_DATE + " TEXT NOT NULL, " +
                OrderEntry.COLUMN_ORDER_QUANTITY + " REAL NOT NULL, " +
                OrderEntry.COLUMN_ORDER_PRIORITY + " INTEGER NOT NULL," +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + OrderEntry.COLUMN_ITEM_ID + ") REFERENCES " +
                InventoryEntry.TABLE_NAME + " (" + InventoryEntry._ID + "), " +

                // To assure the application have just one item order per day
                //created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + OrderEntry.COLUMN_ORDER_DATE + ", " +
                OrderEntry.COLUMN_ITEM_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_INVENTORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ORDER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MEMBER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OrderEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MemberEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
