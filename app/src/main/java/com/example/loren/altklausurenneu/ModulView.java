package com.example.loren.altklausurenneu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.AttributeSet;
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

    public ModulView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        LayoutInflater.from(context).inflate(R.layout.modul_item, this, true);

        String name;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ModulView, 0, 0);

        try {
            name = a.getString(R.styleable.ModulView_Modul_name);
        } finally {
            a.recycle();
        }

        // Exception if required attributes are not set
        if (name == null) {
            throw new RuntimeException("No name provided");
        }

        init(name);
        ImageView imageView = (ImageView) findViewById(R.id.modul_vector);
        this.drawable = imageView.getDrawable();
        this.context = context;
        this.constraintLayout = (ConstraintLayout) findViewById(R.id.modul_constraint_layout);
    }

    private void init(String name) {
        name_modul = (TextView) findViewById(R.id.modul_text);
        name_modul.setText(name);


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
                break;
            case DESELECTED:
                constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.mywhite));
                name_modul.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;

        }
    }


}
