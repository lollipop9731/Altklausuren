package com.example.loren.altklausurenneu.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loren.altklausurenneu.R;

public class ModulButtons {

    private int width;
    private int name;
    private int id;
    private Context context;
    private Button button;
    private ConstraintLayout constraintLayout;

    public ModulButtons(int name, Context context,ConstraintLayout constraintLayout,int id) {
        this.name = name;
        this.context = context;
        this.button = new Button(context);
        this.constraintLayout = constraintLayout;
        this.id = id;
        init(button);

    }

    private void addAsFirst(int marginbottom){
        this.constraintLayout.addView(this.button);
        int margindp = (int) Utils.pxFromDp(getContext(), marginbottom);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.clear(button.getId());

        constraintSet.constrainHeight(button.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainWidth(button.getId(), ConstraintSet.WRAP_CONTENT);
        //center horizontally
        constraintSet.connect(button.getId(), ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        constraintSet.connect(button.getId(), ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);

        //margin to bottom
        constraintSet.connect(button.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margindp);

        constraintSet.applyTo(constraintLayout);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void init(final Button button){
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        button.setLayoutParams(layoutParams);
        button.setHeight(50);
        button.setText(name);
        int id = View.generateViewId();
        button.setId(id);
        setId(id);
        button.setTextSize(18);
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.roboto_light);
        button.setTypeface(typeface);
        button.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        button.setAllCaps(false);
        button.setPadding(30, -5, 16, 0);
        button.setCompoundDrawablePadding(25);
        button.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.animated_vector), null, null, null);
        button.setOnClickListener(onClickListener());
        button.setBackground(getContext().getResources().getDrawable(R.drawable.modul_button_style));


        button.post(new Runnable() {

            @Override
            public void run() {
                int width = button.getWidth();
                setWidth(width);


            }

        });

    }

    private View.OnClickListener onClickListener(){
      View.OnClickListener onClickListener = new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //todo put somethin
           }
       };
      return onClickListener;
    }
}
