package com.example.loren.altklausurenneu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ExamListAdapter extends ArrayAdapter<Exam> {

    private static final String TAG = "ExamListAdapter";
    private Context mcontext;
    int mResource;

    public ExamListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Exam> objects) {
        super(context, resource, objects);
        mcontext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get information
        String name =getItem(position).getName();
        String semester = getItem(position).getSemester();

        //Create Exam objet with the information
        Exam exam = new Exam(name,semester);

        //inflate Layout, set View for custom Listview
        LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        //get Textviews from custom menu
        TextView textView1 = (TextView) convertView.findViewById(R.id.list_header);
        TextView textView2 = (TextView)convertView.findViewById(R.id.list_sub);

        //set Text to Textviews
        textView1.setText(name);
        textView2.setText(semester);

        return convertView;
    }
}
