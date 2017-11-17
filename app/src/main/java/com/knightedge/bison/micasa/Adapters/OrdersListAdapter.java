package com.knightedge.bison.micasa.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knightedge.bison.micasa.Fragments.OrdersFragment;
import com.knightedge.bison.micasa.R;
import com.knightedge.bison.micasa.Utility;

/**
 * Created by Bison on 31/08/2017.
 */
public class OrdersListAdapter extends CursorAdapter {
    private Context mContext;
    public OrdersListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_orders, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String itemName = cursor.getString(OrdersFragment.COL_ITEM_NAME);
        viewHolder.nameView.setText(itemName);

        String itemUnits = cursor.getString(OrdersFragment.COL_ITEM_UNITS);

        double quantity = cursor.getDouble(OrdersFragment.COL_ORDER_QTY);
        viewHolder.quantityView.setText(String.format("%s %1.2f",itemUnits,quantity));

        double unitPrice = cursor.getDouble(OrdersFragment.COL_ITEM_PRICE);
        String currency = Utility.getPreferedCurrency(mContext);
        viewHolder.priceView.setText(String.format("%s %1.2f",currency, (unitPrice * quantity)));
    }

    public static class ViewHolder {
        public final TextView nameView;
        public final TextView descriptionView;
        public final TextView quantityView;
        public final TextView priceView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_orders_name_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_orders_description_textview);
            quantityView = (TextView) view.findViewById(R.id.list_item_orders_quantity_textview);
            priceView = (TextView) view.findViewById(R.id.list_item_orders_price_textview);
        }
    }
}
