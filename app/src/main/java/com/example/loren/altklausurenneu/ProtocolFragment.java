package com.example.loren.altklausurenneu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class ProtocolFragment extends android.app.Fragment {

    private Button testbutton;

    private ImageView anim_vector;
    private ArrayList<Integer> button_ids;
    private Resources ressources;
    private int counter = 0;
    private View.OnClickListener ButtonClickListener;
    private int modulcounter = 0;
    private int displaywidth;

    public static String TAG = "ProtocolFragment";


    public void animate(Drawable drawable) {


        if (drawable instanceof AnimatedVectorDrawableCompat) {
            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        } else if (drawable instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
            animatedVectorDrawable.start();
        }
    }

    public void reset(Drawable drawable) {

        if (drawable instanceof AnimatedVectorDrawableCompat) {
            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
            avd.stop();
        } else if (drawable instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
            animatedVectorDrawable.reset();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        button_ids = new ArrayList<Integer>();
        ressources = getActivity().getResources();
        //get display width
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displaywidth = size.x;

        return inflater.inflate(R.layout.fragment_protoclls, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        testbutton = (Button) getActivity().findViewById(R.id.testbutton);


        ButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < button_ids.size(); i++) {
                    if (button_ids.get(i).equals(v.getId())) {
                        //only select on every second click

                        if (counter % 2 == 0) {
                            selectButton(v);
                        } else {
                            deselectButton(v);
                        }
                        counter++;
                    }
                }
            }
        };




    }






    private void deselectButton(View v) {
        Drawable drawable = ((Button) v).getCompoundDrawables()[0];
        v.setBackground(ressources.getDrawable(R.drawable.modul_button_style));
        ((Button) v).setTextColor(ressources.getColor(R.color.colorPrimary));
        drawable.setTint(ressources.getColor(R.color.colorPrimary));
        reset(drawable);
    }

    /**
     * animates the button from unselected to selected
     *
     * @param v the clicked View
     */
    public void selectButton(View v) {
        Drawable drawable = ((Button) v).getCompoundDrawables()[0];
        ((Button) v).setTextColor(ressources.getColor(R.color.mywhite));
        drawable.setTint(ressources.getColor(R.color.mywhite));
        v.setBackground(ressources.getDrawable(R.drawable.modul_button_style_clicked));
        animate(drawable);
    }


}
