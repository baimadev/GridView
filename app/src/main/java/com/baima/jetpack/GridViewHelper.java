package com.baima.jetpack;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GridViewHelper {
    LinearLayout parent;
    int row = 0;
    ArrayList<Integer> column = new ArrayList<>();

    public GridViewHelper(LinearLayout parent){
        this.parent = parent;
        inflateView();
        measure();
        addView();
    }

    private void addView() {
        if (row <= 0) return;
        int lastEndingItemIndex = 0;
        for (int i = 0; i < row; i++) {
            GridView gridView = new GridView(parent.getContext());
            ArrayList iconItemList = new ArrayList(Collections.singletonList(iconItem).subList(lastEndingItemIndex, column.get(i) + 1));
            lastEndingItemIndex = column.get(i)+1;
            gridView.setAdapter(new GridViewAdapter(iconItemList));
            gridView.setNumColumns(iconItemList.size());
            parent.addView(gridView);
        }
        parent.postInvalidate();
    }

    private void measure() {
        float width = parent.getMeasuredWidth();
        Log.e("xia","width "+width);
        int i = 0;
        while (i < iconItem.size()) {
            View view = iconItem.get(i);
            view.measure(0,0);
            float iconWidth = view.getMeasuredWidth();
            if(width > iconWidth){
                width = width - iconWidth;
                i++;
            }else{
                column.add(i-1);
                row++;
                width = parent.getWidth();
            }
        }
        column.add(iconItem.size()-1);
        Log.e("xia",row+" row");
        Log.e("xia",column.toString());
    }

    private final int[] iconArray = {R.drawable.albatross_icon, R.drawable.rak_eagle_icon, R.drawable.sc_icon_birdie_circle,
            R.color.rak_gray_level_3, R.drawable.icon_bogey, R.drawable.doublebogey_icon, R.drawable.triplebogey_icon};

    private final int[] iconText = {R.string.albatross, R.string.albatross1,R.string.albatross2, R.string.albatross,
            R.string.albatross, R.string.albatross, R.string.albatross,};

    private final ArrayList<View> iconItem = new ArrayList<>();

    private void inflateView(){
        int size = iconArray.length;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        for (int i = 0;i<size;i++) {
            View view = inflater.inflate(R.layout.rak_icon_item,parent,false);
            ImageView imageView =view.findViewById(R.id.rak_icon_image);
            TextView textView = view.findViewById(R.id.rak_icon_text);
            Drawable drawable = parent.getContext().getDrawable(R.drawable.albatross_icon);
            imageView.setImageDrawable(drawable);
            textView.setText(iconText[i]);
            iconItem.add(new LinearLayout(parent.getContext()));
        }
    }

    public class GridViewAdapter extends BaseAdapter {

        ArrayList<View> iconItem;
        public GridViewAdapter(ArrayList<View> iconItem){
        this.iconItem = iconItem;
        }

        @Override
        public int getCount() {
            return this.iconItem.size();
        }

        @Override
        public Object getItem(int position) {
            return this.iconItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return this.iconItem.get(position);
        }
    }

}
