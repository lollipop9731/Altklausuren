package com.example.loren.altklausurenneu;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ExamListAdapter extends ArrayAdapter<Exam> {

    private static final String TAG = "ExamListAdapter";


    public ExamListAdapter(@NonNull Context context, @NonNull ArrayList<Exam> objects) {
        super(context, R.layout.adpaterview_layout, objects);

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get information
        String category =getItem(position).getCategory();
        String semester = getItem(position).getSemester();

        //Create Exam objet with the information
        //todo namen ändern nicht fix mathematische Grundlagen
        Exam exam = new Exam("Mathematische Grundlagen",semester,category);

        //inflate Layout, set View for custom Listview
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View newconvertView = layoutInflater.inflate(R.layout.adpaterview_layout,parent,false);

        String singleline = getItem(position).getName();





        //get Textviews from custom menu
        final TextView textView1 = (TextView) newconvertView.findViewById(R.id.list_header);
        TextView textView2 = (TextView)newconvertView.findViewById(R.id.list_sub);

        //set Text to Textviews
        textView1.setText(category);
        textView2.setText(semester);



        return newconvertView;
    }
}
