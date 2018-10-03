package com.example.loren.altklausurenneu;

import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class ProtocolFragment extends android.app.Fragment {

    private Button testbutton;

    private ImageView anim_vector;
    private ArrayList<Integer> button_ids;



    public void animate(Drawable drawable){


        if(drawable instanceof AnimatedVectorDrawableCompat){
            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        }else if(drawable instanceof  AnimatedVectorDrawable){
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable)drawable;
            animatedVectorDrawable.start();
        }
    }

    public void reset(){
        Drawable drawable = anim_vector.getDrawable();
        if(drawable instanceof AnimatedVectorDrawableCompat){
            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
            avd.stop();
        }else if(drawable instanceof  AnimatedVectorDrawable){
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable)drawable;
            //animatedVectorDrawable.reset();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        button_ids = new ArrayList<Integer>();
        return inflater.inflate(R.layout.fragment_protoclls,container,false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        testbutton = (Button)getActivity().findViewById(R.id.testbutton);


        final View.OnClickListener ButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<button_ids.size();i++){
                    if(button_ids.get(i).equals(v.getId())){
                        Drawable drawable = ((Button) v).getCompoundDrawables()[0];

                        ((Button) v).setTextColor(getActivity().getResources().getColor(R.color.mywhite));
                        v.setBackground(getActivity().getResources().getDrawable(R.drawable.modul_button_style));
                        //((Button)v).setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                        animate(drawable);
                    }
                }
            }
        };

        testbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ConstraintLayout constraintLayout = getActivity().findViewById(R.id.fragment_constraint);

                Button button = new Button(getActivity());
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                button.setLayoutParams(layoutParams);
                button.setHeight(50);
                button.setText("Mathematische Grundlagen I");
                button.setId(View.generateViewId());
                button_ids.add(button.getId());
                button.setTextSize(18);
                Typeface typeface = ResourcesCompat.getFont(getActivity(),R.font.roboto_light);
                button.setTypeface(typeface);
                button.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                button.setAllCaps(false);
                button.setPadding(30,-5,16,0);
                button.setCompoundDrawablePadding(25);
                button.setCompoundDrawablesWithIntrinsicBounds(getActivity().getResources().getDrawable(R.drawable.animated_vector),null,null,null);
                button.setOnClickListener(ButtonClickListener);
                button.setBackground(getActivity().getResources().getDrawable(R.drawable.modul_button_style));

                constraintLayout.addView(button);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.constrainHeight(button.getId(),ConstraintSet.WRAP_CONTENT);
                constraintSet.constrainWidth(button.getId(),ConstraintSet.WRAP_CONTENT);
                constraintSet.connect(button.getId(), ConstraintSet.LEFT,
                        ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                constraintSet.connect(button.getId(), ConstraintSet.RIGHT,
                        ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                constraintSet.connect(button.getId(), ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                constraintSet.connect(button.getId(), ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

                constraintSet.applyTo(constraintLayout);
            }
        });






    }


}
