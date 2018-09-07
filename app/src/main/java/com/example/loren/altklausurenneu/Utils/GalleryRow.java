package com.example.loren.altklausurenneu.Utils;

import android.content.Context;
import android.graphics.Bitmap;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.example.loren.altklausurenneu.R;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.util.ArrayList;



@GlideModule
public class GalleryRow extends AppGlideModule {

    public GalleryRow() {
    }

    private static final String TAG ="GalleryRow" ;
    private LinearLayout.LayoutParams layoutParams;
    private RelativeLayout relativeLayout;
    private int marginbottom;
    private Context context;
    private int imagerotation;
    private LinearLayout linearLayout;

    private Glide glide;



    /**
     * Creates custom Gallery Row
     * @param parentlayout on which the row should be drawn
     * @param marginbottom margin to bottom
     * @param bitmap of the first picture
     * @param context
     * @param imagerotation
     */
    public GalleryRow(RelativeLayout parentlayout, int marginbottom, Bitmap bitmap, Context context, int imagerotation,ArrayList<ImageView>imageViews) {
        this.marginbottom = marginbottom;
        this.relativeLayout = parentlayout;
        this.context = context;
        this.imagerotation = imagerotation;

        Log.d(TAG, "Bitmap Size:" + bitmap.getAllocationByteCount());

        // Add scrollview
        HorizontalScrollView scrollView = new HorizontalScrollView(context);

        ScrollView.LayoutParams scrollparams = new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollparams.gravity = Gravity.BOTTOM;
        scrollparams.topMargin= 500;
        scrollView.setLayoutParams(scrollparams);

        scrollView.bringToFront();
        parentlayout.addView(scrollView);

        //clear the Array with images
        imageViews.clear();
        ImageView imageView = new ImageView(context);

        //set Layout settings
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        linearLayout.setPadding((int)context.getResources().getDimension(R.dimen.spacing_normal),0,0,(int)context.getResources().getDimension(R.dimen.margin_bottom_galleryrow));
        linearLayout.setBaselineAligned(false);
        linearLayout.setGravity(Gravity.LEFT|Gravity.BOTTOM);
        linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        linearLayout.bringToFront();


////set width and height for the LinearLayout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        linearLayout.setLayoutParams(params);

        scrollView.addView(linearLayout);

        //Params for single Image View
        layoutParams = new LinearLayout.LayoutParams((int)context.getResources().getDimension(R.dimen.height_galleryrow),(int)context.getResources().getDimension(R.dimen.width_galleryrow));


        imageView.setLayoutParams(layoutParams);
        imageView.setRotation(imagerotation);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setId(View.generateViewId());


        Log.d(TAG, "ID of first View:" + imageView.getId());

        Glide.with(context)
                .load(bitmap)
                .thumbnail(0.5f)
                .into(imageView);


        //add first image to list of gallerypictures
        imageViews.add(imageView);


        //bring to front, otherwise behind the camera preview
        imageView.bringToFront();



        linearLayout.addView(imageView);


    }

    public void addImage(Bitmap bitmap,ArrayList<ImageView>imageViews){
        //get last image
        ImageView lastImage= imageViews.get(imageViews.size()-1);




        int id_lastimage = lastImage.getId();
        Log.d(TAG,"Last ID:" + id_lastimage);
        //set width and height for the new image view

        layoutParams = new LinearLayout.LayoutParams((int)context.getResources().getDimension(R.dimen.height_galleryrow),(int)context.getResources().getDimension(R.dimen.width_galleryrow));


        layoutParams.rightMargin = (int) context.getResources().getDimension(R.dimen.spacing_normal);

        ImageView current = new ImageView(context);

        current.setLayoutParams(layoutParams);
        current.setScaleType(ImageView.ScaleType.CENTER_CROP);

        current.setId(View.generateViewId());
        current.bringToFront();
        current.setRotation(imagerotation);
        Glide.with(context)
                .load(bitmap)
                .into(current);

        imageViews.add(current);
        this.linearLayout.addView(current);

    }


}
