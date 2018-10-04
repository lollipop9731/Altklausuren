package com.example.loren.altklausurenneu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.loren.altklausurenneu.Utils.Utils;

import java.util.ArrayList;

import static com.example.loren.altklausurenneu.PdfViewer.TAG;
import static com.example.loren.altklausurenneu.Utils.Utils.dpFromPx;

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
        displaywidth= size.x;

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

        testbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ConstraintLayout constraintLayout = getActivity().findViewById(R.id.fragment_constraint);

                final Button modul = newModul("Mathe I");
                modul.setId(R.id.modul1);




                int[] childs = {R.id.modul2, R.id.modul1};


                ConstraintSet constraintSet = new ConstraintSet();



                if (modulcounter == 0) {

                    addAsFirst(modul, 300, constraintLayout);

                } else {
                    Button modul2 = newModul("Statistik");
                    modul2.setId(R.id.modul2);

                    constraintLayout.addView(modul2);

                    if(modul.getWidth()+modul2.getWidth()< (displaywidth *9/10)){
                        //enough place
                        addToChain(constraintSet, constraintLayout, 300,childs);



                    }else{
                        //not enough place
                        addToNewRow(modul2,R.id.modul1,constraintLayout);

                    }




                }
                if (modulcounter == 2) {
                    Button modul3 = newModul("Electronic Business & Entrepreneurship");
                    modul3.setId(R.id.modul3);
                    Log.d(TAG,"Modul 1  " +modul.getMeasuredWidth());


                    addToNewRow(modul3, R.id.modul2, constraintLayout);
                }
                if(modulcounter == 3){
                    Button modul4 = newModul("IT-Security und Business Continuity Management");
                    modul4.setId(View.generateViewId());


                    addToNewRow(modul4, R.id.modul3, constraintLayout);
                }

                modulcounter++;



            }
        });


    }

    /**
     * Adds new Modul with default style
     *
     * @param name of Modul
     * @return
     */
    private Button newModul(String name) {
       final  Button button = new Button(getActivity());
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        button.setLayoutParams(layoutParams);
        button.setHeight(50);
        button.setText(name);
        //button.setId(View.generateViewId());
        //save id of button
        //button_ids.add(button.getId());
        button.setTextSize(18);
        Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.roboto_light);
        button.setTypeface(typeface);
        button.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        button.setAllCaps(false);
        button.setPadding(30, -5, 16, 0);
        button.setCompoundDrawablePadding(25);
        button.setCompoundDrawablesWithIntrinsicBounds(getActivity().getResources().getDrawable(R.drawable.animated_vector), null, null, null);
        button.setOnClickListener(ButtonClickListener);
        button.setBackground(getActivity().getResources().getDrawable(R.drawable.modul_button_style));

        button.post(new Runnable() {

            @Override
            public void run() {
                int width = button.getWidth();
                int height = button.getHeight();
                Log.d(TAG,"Runnable: " +width + "  " +height);
            }

        });

        return button;
    }

    /**
     * Adds the first button
     *
     * @param button           to be added
     * @param marginbotton     in dp, margin to the bottom of parent
     * @param constraintLayout the layout where the the rules should apply
     */
    private void addAsFirst(Button button, int marginbotton, ConstraintLayout constraintLayout) {
        constraintLayout.addView(button);

        int margindp = (int) Utils.pxFromDp(getActivity(), marginbotton);
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

    private void addToChain(ConstraintSet constraintSet, ConstraintLayout constraintLayout, int marginbottom,int[]childs) {
        int margindp = (int) Utils.pxFromDp(getActivity(), marginbottom);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.modul2, ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margindp);

        constraintSet.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, childs, null, ConstraintSet.CHAIN_SPREAD);
        constraintSet.applyTo(constraintLayout);
    }


    private void addToNewRow(Button button, int topModulID, ConstraintLayout constraintLayout) {
        constraintLayout.addView(button);


        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.constrainHeight(button.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainWidth(button.getId(), ConstraintSet.WRAP_CONTENT);

        //center horizontally
        constraintSet.connect(button.getId(), ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        constraintSet.connect(button.getId(), ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);

        //get dp margin
        int margin = (int) Utils.pxFromDp(getActivity(), 24);

        //margin to top buttom
        constraintSet.connect(button.getId(), ConstraintSet.TOP, topModulID, ConstraintSet.BOTTOM, margin);

        constraintSet.applyTo(constraintLayout);
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
