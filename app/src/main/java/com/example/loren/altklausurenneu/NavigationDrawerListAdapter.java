package com.example.loren.altklausurenneu;

import android.content.Context;
import android.graphics.Color;
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

import static android.view.View.GONE;

public class NavigationDrawerListAdapter extends ArrayAdapter<String> {

   int color;
   int position;
   Context context;

    public int getPosition() {
        return position;
    }

    /**
     * Set position which one should be colored and highlighted
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }



    public NavigationDrawerListAdapter(@NonNull Context context, @NonNull String[] objects) {
        super(context, R.layout.nav_listview, objects);
        this.context = context;
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
        if(position==getPosition()){
            textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            view.setBackgroundColor(context.getResources().getColor(R.color.grey));
        }


        return view;



    }


}


