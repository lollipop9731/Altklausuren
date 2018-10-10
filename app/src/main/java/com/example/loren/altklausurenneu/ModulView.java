package com.example.loren.altklausurenneu;

import android.content.Context;
import android.content.res.TypedArray;
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



    public ModulView(Context context) {
        super(context);

        setOrientation(HORIZONTAL);

        Log.d("ModulView", "Constructor geöffnet Hier");
        LayoutInflater.from(context).inflate(R.layout.modul_item, this, true);
        ImageView imageView = (ImageView) findViewById(R.id.modul_vector);

        this.drawable = imageView.getDrawable();
        name_modul = (TextView) findViewById(R.id.modul_text);
        this.context = context;
        this.constraintLayout = (ConstraintLayout) findViewById(R.id.modul_constraint_layout);
        //default State
        this.state = State.DESELECTED;
        drawable.setTint(context.getColor(R.color.colorPrimary));


    }

    public void setText(String text) {

        name_modul.setText(text);
    }

    public void animateDrawable() {

        if (this.drawable instanceof AnimatedVectorDrawableCompat) {
            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
            drawable.setTint(context.getColor(R.color.mywhite));
            avd.start();
        } else if (this.drawable instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
            drawable.setTint(context.getColor(R.color.mywhite));
            animatedVectorDrawable.start();
        }

    }

    public void setState(State state) {
        switch (state) {
            case SELECTED:
                constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                name_modul.setTextColor(context.getResources().getColor(R.color.mywhite));
                animateDrawable();
                this.state = State.SELECTED;

                break;
            case DESELECTED:
                constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.mywhite));
                name_modul.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                if (drawable instanceof AnimatedVectorDrawable) {
                    ((AnimatedVectorDrawable) drawable).reset();
                }
                drawable.setTint(context.getColor(R.color.colorPrimary));
                this.state = State.DESELECTED;
                break;

        }
    }

    public State getState(){
        return this.state;
    }


}
