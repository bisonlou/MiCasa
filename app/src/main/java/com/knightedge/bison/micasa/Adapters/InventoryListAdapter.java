package com.knightedge.bison.micasa.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knightedge.bison.micasa.Fragments.InventoryFragment;
import com.knightedge.bison.micasa.R;
import com.knightedge.bison.micasa.Utility;

/**
 * Created by Bison on 31/08/2017.
 */
public class InventoryListAdapter extends CursorAdapter {
    private Context mContext;

    public InventoryListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_inventory, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String itemName = cursor.getString(InventoryFragment.COL_ITEM_NAME);
        viewHolder.nameView.setText(itemName);

        String itemUnits = cursor.getString(InventoryFragment.COL_ITEM_UNITS);
        viewHolder.unitsView.setText(itemUnits);

        double unitPrice = cursor.getDouble(InventoryFragment.COL_ITEM_PRICE);
        String currency = Utility.getPreferedCurrency(mContext);
        viewHolder.priceView.setText(String.format("%s %1.2f", currency, unitPrice));

    }

   /* @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewGroup parent = viewGroup;
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_inventory, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.menuView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.showContextMenuForChild(v);
                }
            });

        return super.getView(position, view, parent);
    }*/


    public static class ViewHolder {
        public final TextView nameView;
        //public final TextView descriptionView;
        public final TextView priceView;
        public final TextView unitsView;
        //public final ImageButton menuView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_inventory_name_textview);
            //descriptionView = (TextView) view.findViewById(R.id.list_item_inventory_description_textview);
            priceView = (TextView) view.findViewById(R.id.list_item_inventory_price_textview);
            unitsView = (TextView) view.findViewById(R.id.list_item_inventory_units_textview);
           // menuView = (ImageButton) view.findViewById(R.id.list_item_inventory_menu_button);
        }
    }
}
