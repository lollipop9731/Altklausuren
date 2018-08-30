package com.example.loren.altklausurenneu;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ExamListAdapter extends ArrayAdapter<Exam> {

    private static final String TAG = "ExamListAdapter";


    public ExamListAdapter(@NonNull Context context, @NonNull ArrayList<Exam> objects) {
        super(context, R.layout.adapterview_constraint, objects);

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get information
        String filetype;
        String category =getItem(position).getCategory();
        String semester = getItem(position).getSemester();

        String filetypetemp = getItem(position).getFilepath();
        //todo add number of pages
        if(filetypetemp.contains(".pdf")){
                filetype = "pdf";
        }else{
            filetype ="jpg";
        }



        //inflate Layout, set View for custom Listview
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View newconvertView = layoutInflater.inflate(R.layout.adapterview_constraint,parent,false);







        //get Textviews from custom menu
        final TextView textView1 = (TextView) newconvertView.findViewById(R.id.listview_header);
        TextView textView2 = (TextView)newconvertView.findViewById(R.id.listview_sub);
        ImageView imageView = (ImageView)newconvertView.findViewById(R.id.listview_svg);

        //set correct icon depending on file type
        if(filetype.equals("jpg")){
            imageView.setBackgroundResource(R.drawable.ic_jpg);

        }else{
            imageView.setBackgroundResource(R.drawable.ic_pdf_neu);
        }

        //set Text to Textviews
        textView1.setText(category);
        textView2.setText(semester);



        return newconvertView;
    }
}
