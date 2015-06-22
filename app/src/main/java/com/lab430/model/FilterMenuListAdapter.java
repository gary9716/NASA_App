package com.lab430.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.andexert.expandablelayout.library.ExpandableLayoutItem;

/**
 * Created by lab430 on 15/6/22.
 */
public class FilterMenuListAdapter {

    Context mContext;
    int mRowLayoutResID;
    String[] mFilterNames;

    public FilterMenuListAdapter(Context context, int rowLayoutResID, String[] filterNames) {

        mContext = context;
        mRowLayoutResID = rowLayoutResID;
        mFilterNames = filterNames;
        ExpandableLayoutItem expandableLayoutItem = new ExpandableLayoutItem(context);

    }

}
