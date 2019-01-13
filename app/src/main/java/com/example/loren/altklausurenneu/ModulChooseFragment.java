package com.example.loren.altklausurenneu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ModulChooseFragment extends Fragment {

    //interface for Fragment -> Activity Communication
    private FragmentInteractionListener mFragmentInteractionlistener;


    MyRecyclerViewAdapter adapter;
    MyRecyclerViewAdapter adapter_chosen;
    String TAG = getTag();
    private ArrayList<String> chosenmoduls;
    private FirebaseMethods firebaseMethods;


    public ModulChooseFragment() {
        // Required empty public constructor
    }

    public static ModulChooseFragment newInstance() {
        ModulChooseFragment fragment = new ModulChooseFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseMethods = new FirebaseMethods(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //communicate with activity for new title
        if (mFragmentInteractionlistener != null) {
            mFragmentInteractionlistener.onFragmentInteraction("Module hinzufügen");

        }

        return inflater.inflate(R.layout.fragment_choose_moduls, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //two recyclerview for chosen moduls and moduls to choose from
        RecyclerView recyclerView = getActivity().findViewById(R.id.recycler_modul);
        final RecyclerView recyclerViewMyModuls = getActivity().findViewById(R.id.recycler_chosen_moduls);


        //Chips layout manager for moduls
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


        //Array List for moduls mock data //todo replace with real data from db
        final ArrayList<String> poolmoduls = new ArrayList<>();
        poolmoduls.add("Betriebssysteme");
        poolmoduls.add("Mobile Systeme");
        poolmoduls.add("Entrepreneurship");
        poolmoduls.add("Statistik");
        poolmoduls.add("IT-Security & Business Continuity");


        //set the chip layoutmanager to both of the recyclerviews
        recyclerView.setLayoutManager(chipsLayoutManager);
        recyclerViewMyModuls.setLayoutManager(chipsLayoutManagerModulPool);

        //get chosen moduls from Firebase for the current user
        firebaseMethods = new FirebaseMethods(getActivity());
        chosenmoduls = new ArrayList<>();
        Query query = firebaseMethods.selectAllModulsOfCurrentUser();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    chosenmoduls.add(child.getKey());

                }

                recyclerViewMyModuls.setAdapter(adapter_chosen);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //adapter for chosen moduls --> blue
        adapter_chosen = new MyRecyclerViewAdapter(getActivity(), chosenmoduls, true, new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModulView modulView, int position) {

                //add to pool of moduls and remove from chosen moduls
                String clicked_modul = chosenmoduls.get(position);
                //nur zum Pool hinzufügen, wenn nicht schon drin!!
                if(!poolmoduls.contains(clicked_modul)){
                    poolmoduls.add(clicked_modul);
                }

                adapter.notifyDataSetChanged();
                adapter_chosen.removeAt(position);
                //remove from DB
                firebaseMethods.deleteModuleFromCurrentUser(clicked_modul);
            }
        });

        //get all Module from Hochschule of current user

        //adapter for modulpool
        adapter = new MyRecyclerViewAdapter(getActivity(), poolmoduls, false, new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModulView modulView, int position) {

                //add to chosen moduls
                String clickedmodul = "";
                try{
                    clickedmodul = poolmoduls.get(position);
                }catch (IndexOutOfBoundsException e){
                    //wenn man zu schnell clicked, kann es zu Index out of Bounds kommen
                }

                //checks if modul already selected
                if (!chosenmoduls.contains(clickedmodul)) {
                    try{
                        chosenmoduls.add(poolmoduls.get(position));
                    }catch (IndexOutOfBoundsException e){
                        //wenn man zu schnell clicked, kann es zu Index out of Bounds kommen
                    }

                    adapter_chosen.notifyDataSetChanged();
                    adapter.removeAt(position);
                    //add to User DB
                    firebaseMethods.addModuleToCurrentUser(chosenmoduls);
                }


            }
        });


        recyclerView.setAdapter(adapter);


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentInteractionlistener = (FragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentInteractionlistener = null;
    }

    public interface FragmentInteractionListener {
        void onFragmentInteraction(String title);
    }


}
