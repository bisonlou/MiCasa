package com.knightedge.bison.micasa.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knightedge.bison.micasa.MembersActivity;
import com.knightedge.bison.micasa.R;

/**
 * Created by Bison on 31/10/2017.
 */

public class MembersListAdapter extends CursorAdapter {
    private Context mContext;

    public MembersListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_member, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String memberName = cursor.getString(MembersActivity.COL_MEMBER_NAME);
        viewHolder.nameView.setText(memberName);

        String memberEmail = cursor.getString(MembersActivity.COL_MEMBER_EMAIL);
        viewHolder.emailView.setText(memberEmail);

        String memberStatus = cursor.getString(MembersActivity.COL_MEMBER_STATUS);
        viewHolder.statusView.setText(memberStatus);

    }

    public static class ViewHolder {
        public final TextView nameView;
        public final TextView emailView;
        public final TextView statusView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_member_name);
            emailView = (TextView) view.findViewById(R.id.list_item_member_email);
            statusView = (TextView) view.findViewById(R.id.list_item_member_status);
        }
    }
}
