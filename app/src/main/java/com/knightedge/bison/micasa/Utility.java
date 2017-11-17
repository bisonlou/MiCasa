package com.knightedge.bison.micasa;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.knightedge.bison.micasa.data.MiCasaContract;

/**
 * Created by Bison on 31/08/2017.
 */
public class Utility extends Activity {

    public static String getPreferedCurrency(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(
                context.getString(R.string.pref_currency_key),
                context.getString(R.string.pref_currency_default));
    }

    public static String getAccountName(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                MainActivity.SHARED_PREF_FILE,
                Context.MODE_PRIVATE);
        return sharedPrefs.getString(context.getString(R.string.account_name)
                , context.getString(R.string.account_name_default));
    }

    public static String getAccountEmail(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                MainActivity.SHARED_PREF_FILE,
                Context.MODE_PRIVATE);
        return sharedPrefs.getString(context.getString(R.string.account_email)
                , context.getString(R.string.account_email_default));
    }

    public static String getCasaAccount(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                MainActivity.SHARED_PREF_FILE,
                Context.MODE_PRIVATE);
        return sharedPrefs.getString(context.getString(R.string.casa_account)
                , context.getString(R.string.casa_account_default));
    }
}
