package com.example.loren.altklausurenneu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.loren.altklausurenneu.Utils.State;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {


    private ArrayList<String> myDataset;
    private LayoutInflater mInflater;
    private OnItemClickListener mClickListener;
    private Context context;
    private Boolean allselected;
    String TAG = getClass().getSimpleName();

    public Boolean getAllselected() {
        return allselected;
    }

    public void setAllselected(Boolean allselected) {
        this.allselected = allselected;
    }

    // data is passed into the constructor
       MyRecyclerViewAdapter(Context context, ArrayList<String> myDataset,Boolean allselected,OnItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.myDataset = myDataset;
        this.context = context;
        this.allselected = allselected;
        this.mClickListener = mClickListener;

    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModulView modulView;
        if(getAllselected()){
            //on second list for chosen moduls
            Log.d(TAG,"All selected");
            modulView = new ModulView(parent.getContext(),State.SELECTED);

        }else{
            modulView = new ModulView(parent.getContext(),State.DESELECTED);
        }


        modulView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));


        return new ViewHolder(modulView);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           //set data and image to grid view

        holder.getModulView().setText(myDataset.get(position));
        //binding for setting click listener interface
        holder.bind(holder.modulView,this.mClickListener,position);



    }

    // total number of cells
    @Override
    public int getItemCount() {
        return myDataset.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder      {
        private ModulView modulView;
        TextView myTextview;


        ViewHolder(View itemView) {
            super(itemView);
            myTextview = itemView.findViewById(R.id.modul_text);
            modulView = (ModulView) itemView;

                    }

        public ModulView getModulView(){
          return modulView;

        }

        public void bind(final ModulView modulView, final OnItemClickListener itemClickListener, final int position){
            modulView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemClickListener.onItemClick(modulView,position);
                }
            });
        }





    }



    // parent activity will implement this method to respond to click events
    public interface OnItemClickListener {
        void onItemClick(ModulView modulView,int position);
    }

    public void removeAt(int position){
        //todo hier war nen IndexoutofBounds
           myDataset.remove(position);
           notifyItemRemoved(position);
           notifyItemRangeChanged(position,getItemCount());

    }


}
