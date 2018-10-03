package com.example.loren.altklausurenneu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itextpdf.text.pdf.ColumnText;

import java.util.HashMap;
import java.util.List;

import static com.example.loren.altklausurenneu.NewExamDialog.TAG;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private int position = -99;

    public int getPosition() {
        return position;
    }

    /**
     * This position will be highlighted
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    private List<String> listDataHeader; //header titles
    //child data in format of: header title and childs
    private HashMap<String,List<String>>  listDataChild;
    private ImageView imageView;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,HashMap<String, List<String>> listDataChild){
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }



    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expand_list_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);

        final ImageView  arrowheader = (ImageView)convertView.findViewById(R.id.list_header_arrow);

        arrowheader.animate().rotationBy(180).setDuration(100).start();


        lblListHeader.setText(headerTitle);

        return convertView;
    }





    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);




            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expand_list_child, null);


        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        imageView = (ImageView)convertView.findViewById(R.id.list_header_arrow);

        txtListChild.setText(childText);

        if(isLastChild){
            //add + in left at last child
            txtListChild.setPadding(180,0,0,0);
            txtListChild.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_add_black_24dp),null,null,null);
            txtListChild.setCompoundDrawablePadding(24);
        }


        if(childPosition==getPosition()){

            txtListChild.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            txtListChild.setBackgroundColor(context.getResources().getColor(R.color.grey));

        }

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }




}
