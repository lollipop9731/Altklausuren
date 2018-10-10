package com.example.loren.altklausurenneu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.loren.altklausurenneu.Utils.State;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {


    private ArrayList<String> myDataset;
    private LayoutInflater mInflater;
    private ItemClickListenerInterface mClickListener;
    private Context context;
private Boolean allselected;

    public Boolean getAllselected() {
        return allselected;
    }

    public void setAllselected(Boolean allselected) {
        this.allselected = allselected;
    }

    // data is passed into the constructor
       MyRecyclerViewAdapter(Context context, ArrayList<String> myDataset,Boolean allselected) {
        this.mInflater = LayoutInflater.from(context);
        this.myDataset = myDataset;
        this.context = context;
        this.allselected = allselected;

    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModulView modulView = new ModulView(parent.getContext());
       // View view = mInflater.inflate(R.layout.grid_recycler_item, parent, false);
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
        setClickListener(mClickListener);


    }

    // total number of cells
    @Override
    public int getItemCount() {
        return myDataset.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ModulView modulView;
        TextView myTextview;


        ViewHolder(View itemView) {
            super(itemView);
            myTextview = itemView.findViewById(R.id.modul_text);
            modulView = (ModulView) itemView;

            modulView.setOnClickListener(this);
        }

        public ModulView getModulView(){
            if(!getAllselected()){
                return modulView;
            }else{
                modulView.setState(State.SELECTED);
                return modulView;
            }

        }




        @Override
        public void onClick(View v) {
            if(mClickListener!=null){
                mClickListener.onItemClick(v,getAdapterPosition());

            }

        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return myDataset.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListenerInterface itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListenerInterface {
        void onItemClick(View view, int position);
    }

    public void removeAt(int position){
           myDataset.remove(position);
           notifyItemRemoved(position);
           notifyItemRangeChanged(position,getItemCount());

    }


}
