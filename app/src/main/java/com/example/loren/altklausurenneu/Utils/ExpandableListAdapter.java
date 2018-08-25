package com.example.loren.altklausurenneu.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loren.altklausurenneu.R;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final String TAG = "ExpandableListAdapter";

    private Context context;
    //header titles
    private List<ExpandListModel> mListDataHeader;

    // child data in format of header title, child title
    private HashMap<ExpandListModel, List<String>> mListDataChild;
    ExpandableListView expandList;

    public ExpandableListAdapter(Context context, List<ExpandListModel> expandListModelList, HashMap<ExpandListModel, List<String>> mListDataChild, ExpandableListView expandList) {
        this.context = context;
        this.mListDataHeader = expandListModelList;
        this.mListDataChild = mListDataChild;
        this.expandList = expandList;
    }

    @Override
    public int getGroupCount() {
        int i = mListDataHeader.size();
        Log.d(TAG, "Group Count: " + i);

        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childcount = 0;
        //todo warum hier group positiion != 2
        if(groupPosition !=2){
            childcount = this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
        }
        return childcount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandListModel headertitle = (ExpandListModel) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listheader, null);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.submenu);
        ImageView headerIcon = (ImageView) convertView.findViewById(R.id.iconimage);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headertitle.getIconName());
        headerIcon.setImageResource(headertitle.getIconImg());
        return convertView;
    }




    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_submenu, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.submenu);

        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }















}
