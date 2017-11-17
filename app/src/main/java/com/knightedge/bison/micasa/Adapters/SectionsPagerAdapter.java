package com.knightedge.bison.micasa.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.knightedge.bison.micasa.Fragments.InventoryFragment;
import com.knightedge.bison.micasa.Fragments.OrdersFragment;
import com.knightedge.bison.micasa.R;

/**
 * Created by Bison on 16/10/2017.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;

    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);
        switch (position) {
            case 0: InventoryFragment inventoryFragment = new InventoryFragment();
                inventoryFragment.setRetainInstance(true);
                return inventoryFragment;

            case 1:
                OrdersFragment ordersFragment = new OrdersFragment();
                ordersFragment.setRetainInstance(true);
                return ordersFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section2);
            case 1:
                return mContext.getString(R.string.title_section1);
        }
        return null;
    }
}