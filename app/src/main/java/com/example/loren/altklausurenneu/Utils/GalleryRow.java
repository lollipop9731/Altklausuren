package com.example.loren.altklausurenneu.Utils;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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

    private static final String TAG = "GalleryRow";
    private LinearLayout.LayoutParams layoutParams;
    private RelativeLayout relativeLayout;

    private Context context;

    private LinearLayout linearLayout;


    /**
     * Creates custom Gallery Row
     *
     * @param parentlayout  on which the row should be drawn
     * @param context
     * @param imagerotation
     */
    public GalleryRow(RelativeLayout parentlayout, Context context) {

        this.relativeLayout = parentlayout;
        this.context = context;



        // Add scrollview
        HorizontalScrollView scrollView = new HorizontalScrollView(context);

        ScrollView.LayoutParams scrollparams = new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollparams.gravity = Gravity.BOTTOM;
        scrollparams.topMargin = 500;
        scrollView.setLayoutParams(scrollparams);

        scrollView.bringToFront();
        parentlayout.addView(scrollView);


        //new Imageview to be shown
        ImageView imageView = new ImageView(context);

        //set Layout settings
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        linearLayout.setPadding((int) context.getResources().getDimension(R.dimen.spacing_small), 0, 0, (int) context.getResources().getDimension(R.dimen.margin_bottom_galleryrow));
        linearLayout.setBaselineAligned(false);
        linearLayout.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        linearLayout.bringToFront();


       //set width and height for the LinearLayout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        linearLayout.setLayoutParams(params);

        scrollView.addView(linearLayout);






    }

    public void addImage(Bitmap bitmap,int imagerotation) {


        layoutParams = new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen.height_galleryrow), (int) context.getResources().getDimension(R.dimen.width_galleryrow));


        layoutParams.rightMargin = (int) context.getResources().getDimension(R.dimen.spacing_small);

        ImageView current = new ImageView(context);

        current.setLayoutParams(layoutParams);
        current.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //bring in front of camera preview
        current.bringToFront();
        //otherwise the picture is turned upside down
        current.setRotation(imagerotation);
        Glide.with(context)
                .load(bitmap)
                .into(current);


        this.linearLayout.addView(current);

    }

    public void updateImages(ArrayList<String> stringArrayList, int rotation) {
        for (int i = 0; i < stringArrayList.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(stringArrayList.get(i));
            Bitmap thumb = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
            addImage(thumb,rotation);

        }

    }


}
