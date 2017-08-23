package com.tripco.www.tripco.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.tripco.www.tripco.R;
import com.tripco.www.tripco.ui.MakeTripActivity;
import com.tripco.www.tripco.util.U;

/**
 * Created by kkmnb on 2017-08-22.
 */

public class HashtagAdapter extends BaseAdapter {
    CheckBox checkBox;
    MakeTripActivity self;

    public HashtagAdapter(MakeTripActivity self) {
        this.self = self;
    }

    @Override
    public int getCount() {
        return U.getInstance().tags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = self.getLayoutInflater().inflate(R.layout.cell_hashtag_layout, viewGroup, false);
        }
        checkBox = view.findViewById(R.id.check_cb);
        checkBox.setText("#" + U.getInstance().tags[i]);
        return view;
    }
}
