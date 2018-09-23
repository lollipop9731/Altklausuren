package com.example.loren.altklausurenneu;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerListAdapter extends ArrayAdapter<String> {

    public NavigationDrawerListAdapter(@NonNull Context context, @NonNull String[] objects) {
        super(context, R.layout.nav_listview, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        String header = getItem(position);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.nav_listview,parent,false);

        TextView textView = (TextView)view.findViewById(R.id.list_nav_text);
        ImageView imageView = (ImageView)view.findViewById(R.id.list_nav_icon);

        switch (position){
            case 0:
                imageView.setImageResource(R.drawable.ic_baseline_description_24px);
               // imageView.setImageTintList(getContext().getColorStateList(R.xml.icon_color_list));
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_baseline_calendar_today_24px);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_menu_manage);
        }


        textView.setText(header);

        return view;



    }


}


