package com.example.loren.altklausurenneu;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.constraint.ConstraintLayout;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridView extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    MyRecyclerViewAdapter adapter;


    @Override
    public void onItemClick(View view, int position) {

    }

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

    public void animate(Drawable drawable) {


        if (drawable instanceof AnimatedVectorDrawableCompat) {
            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        } else if (drawable instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
            animatedVectorDrawable.start();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(
                R.id.recycler_modul);

        // try of Chips layout manager


        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(getContext())
                //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
                .setChildGravity(Gravity.LEFT)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_VIEW)
                .build();



        final ArrayList<String> data = new ArrayList<>();
        data.add("Betriebssysteme");
        data.add("Mobile Systeme");
        data.add("Entrepreneurship");
        data.add("Statistik");
        data.add("IT-Security & Business Continuity");

        recyclerView.setLayoutManager(chipsLayoutManager);

        adapter = new MyRecyclerViewAdapter(getActivity(), data);
        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {

                adapter.removeAt(position);

                //views of item view
                final ImageView vector = view.findViewById(R.id.animate_vector);
                final TextView textView = view.findViewById(R.id.infotext);
                final ConstraintLayout constraintLayout = view.findViewById(R.id.constraint_id);
                final Drawable drawable = vector.getDrawable();

                //if view was clicked -> active
                if (view.isActivated()) {
                    //unselect view
                    constraintLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.mywhite));
                    textView.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    view.setActivated(false);
                    //todo add version for api below 23
                    if (drawable instanceof AnimatedVectorDrawable) {
                        ((AnimatedVectorDrawable) drawable).reset();
                    }
                    drawable.setTint(getActivity().getResources().getColor(R.color.colorPrimary));
                } else {
                    // select view
                    constraintLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    animate(drawable);
                    view.setActivated(true);
                    textView.setTextColor(getActivity().getResources().getColor(R.color.mywhite));
                    drawable.setTint(getActivity().getResources().getColor(R.color.mywhite));


                }


            }
        });
        recyclerView.setAdapter(adapter);


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid_view, container, false);
    }


}
