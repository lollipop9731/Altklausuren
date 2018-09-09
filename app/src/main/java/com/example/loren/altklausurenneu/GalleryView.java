package com.example.loren.altklausurenneu;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.Toast;

import com.glide.slider.library.Animations.BaseAnimationInterface;
import com.glide.slider.library.Indicators.PagerIndicator;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.glide.slider.library.Tricks.ViewPagerEx;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.yavski.fabspeeddial.FabSpeedDial;

public class GalleryView extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    ArrayList<String> filepath;
    ArrayList<String> thumbpath;
    private String TAG ="GalleryView";
    private SliderLayout mSlider;
    FloatingActionButton floatingActionButton;
    int currentpage;
    Boolean clickeddelete = false;
    ViewPagerEx viewPagerEx;

    @BindView(R.id.deletefoto)
    ImageView deletePhoto;


    private DefaultSliderView defaultSliderView;
    private int delete;

    @OnClick(R.id.deletefoto)
    public void onPhotoDeleted(){
        //return to camera if only one photo is left
        if(filepath.size()==1){
            onBackPressed();
        }


        Log.d(TAG,"Size of Array First : " +filepath.size());
        delete = mSlider.getCurrentPosition();

        Log.d(TAG, "Current Path:" + filepath.get(delete));
        //remove file from storage
        Boolean aBoolean = new File(filepath.get(delete)).delete();
        Boolean aBoolean2 = new File(filepath.get(delete)).delete();
        new File(thumbpath.get(delete)).delete();
        Log.d(TAG, "Deleted file from storage: " + aBoolean + filepath.get(delete));
        Log.d(TAG, "Deleted Thumb from storage " + aBoolean + thumbpath.get(delete));
        //remove filepath from array of files
        filepath.remove(delete);
        thumbpath.remove(delete);

        Log.d(TAG, "Size of Array : " + filepath.size() + "Size of Thumbs: " + thumbpath.size());


        mSlider.removeSliderAt(delete);



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GalleryView.this,PhotoViewer.class);
        intent.putExtra("Thumbpath",thumbpath);

        startActivity(intent);

    }

    //todo delete page if delete pressed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery_view);


        mSlider = (SliderLayout)findViewById(R.id.slider);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);

        viewPagerEx = new ViewPagerEx(getApplicationContext());


        ButterKnife.bind(this);
        deletePhoto.bringToFront();


        Bundle bundle = getIntent().getExtras();

        mSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mSlider.setDuration(0);
        mSlider.stopAutoCycle();


        if(bundle!=null){
            filepath = bundle.getStringArrayList("Filepath");
            thumbpath = bundle.getStringArrayList("Thumbpath");


            Log.d(TAG, "Array with filepath created.");
        }

        showSlider(filepath);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Upload gestartet",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showSlider(ArrayList<String>filepath){
        for(int i =0;i<filepath.size();i++){
            defaultSliderView = new DefaultSliderView(this);
            defaultSliderView
                    .image(filepath.get(i))
                    .setOnSliderClickListener(this);

            defaultSliderView.bundle(new Bundle());
            defaultSliderView.getBundle().putString("path",filepath.get(i));


            mSlider.addSlider(defaultSliderView);

        }
    }



    @Override
    public void onPageScrolled(int i, float v, int i1) {
        Log.d(TAG,"I: "+i + "v: "+v +"il: "+i1 );
    }

    @Override
    public void onSliderClick(BaseSliderView baseSliderView) {
    Log.d(TAG,"Clicked: " + baseSliderView.getBundle().get("path"));

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    public void onPageSelected(int position) {
        this.currentpage = position;
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        Log.d(TAG,"PageScrollState: "+ i);
    }

}
