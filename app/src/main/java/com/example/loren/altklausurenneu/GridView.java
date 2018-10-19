package com.example.loren.altklausurenneu;

import android.app.Fragment;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.example.loren.altklausurenneu.Utils.State;

import java.util.ArrayList;



public class GridView extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    ModulView modulView;

    MyRecyclerViewAdapter adapter;
    MyRecyclerViewAdapter adapter_chosen;
    String TAG = getTag();


    public GridView() {
        // Required empty public constructor
    }

    public static GridView newInstance() {
        GridView fragment = new GridView();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(
                R.id.recycler_modul);

        final RecyclerView recyclerViewMyModuls = (RecyclerView) getActivity().findViewById(R.id.recycler_chosen_moduls);


        // try of Chips layout manager for modul pool
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(getContext())
                //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
                .setChildGravity(Gravity.LEFT)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_VIEW)
                .build();


        ChipsLayoutManager chipsLayoutManagerModulPool = ChipsLayoutManager.newBuilder(getContext())
                //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
                .setChildGravity(Gravity.LEFT)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_VIEW)
                .build();


        //Array List for moduls
        final ArrayList<String> data = new ArrayList<>();
        data.add("Betriebssysteme");
        data.add("Mobile Systeme");
        data.add("Entrepreneurship");
        data.add("Statistik");
        data.add("IT-Security & Business Continuity");

        //Array list for chosen moduls
        final ArrayList<String> chosenmoduls = new ArrayList<>();

        recyclerView.setLayoutManager(chipsLayoutManager);
        recyclerViewMyModuls.setLayoutManager(chipsLayoutManagerModulPool);

        //adapter for chosen moduls
        adapter_chosen = new MyRecyclerViewAdapter(getActivity(), chosenmoduls, true, new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModulView modulView, int position) {
            //todo fill click listener for choosen moduls
            }
        });


        //adapter for modulpool
        adapter = new MyRecyclerViewAdapter(getActivity(), data, false, new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModulView modulView, int position) {


                chosenmoduls.add(data.get(position));
                adapter_chosen.notifyDataSetChanged();
                adapter.removeAt(position);


            }
        });


        recyclerView.setAdapter(adapter);
        recyclerViewMyModuls.setAdapter(adapter_chosen);


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid_view, container, false);
    }


}
