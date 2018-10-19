package com.example.loren.altklausurenneu;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.loren.altklausurenneu.Utils.State;

public class ModulView extends LinearLayout {

    Drawable drawable;
    ImageView imageView;
    Context context;
    ConstraintLayout constraintLayout;
    private TextView name_modul;
    State state;


    public ModulView(Context context, @Nullable AttributeSet attrs) {
        super(context, null);


    }

    public ModulView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    public ModulView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);


    }



    public ModulView(Context context,State state) {
        super(context);

        setOrientation(HORIZONTAL);

        Log.d("ModulView", "Constructor geöffnet Hier");
        LayoutInflater.from(context).inflate(R.layout.modul_item, this, true);


        this.imageView = (ImageView) findViewById(R.id.modul_vector);
        this.drawable = imageView.getDrawable();
        name_modul = (TextView) findViewById(R.id.modul_text);
        this.context = context;
        this.constraintLayout = (ConstraintLayout) findViewById(R.id.modul_constraint_layout);

        //default State
        if (state == State.SELECTED) {
            setChosenState();
        }



    }

    /**
     * Set the Modul view to chosen without animation
     */
    public void setChosenState(){
        this.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_check_white_24dp));

        name_modul.setTextColor(context.getColor(R.color.mywhite));
        constraintLayout.setBackgroundColor(context.getColor(R.color.colorPrimary));
    }



    public void setText(String text) {

        name_modul.setText(text);
    }




}
