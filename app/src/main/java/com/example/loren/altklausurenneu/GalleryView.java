package com.example.loren.altklausurenneu;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private static final String INTENT_THUMBNAILS_PATH = "Thumbpath";
    private static final String INTENT_PHOTOS_PATH = "Filepath";
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



        Log.d(TAG,"Size of Array First : " +filepath.size());
        delete = mSlider.getCurrentPosition();

        Log.d(TAG, "Current Path:" + filepath.get(delete));
        //remove file from storage
        Boolean aBoolean = new File(filepath.get(delete)).delete();
        Boolean aBoolean2 = new File(thumbpath.get(delete)).delete();
        new File(thumbpath.get(delete)).delete();
        Log.d(TAG, "Deleted file from storage: " + aBoolean + filepath.get(delete));
        Log.d(TAG, "Deleted Thumb from storage " + aBoolean2 + thumbpath.get(delete));
        //remove filepath from array of files
        filepath.remove(delete);
        thumbpath.remove(delete);

        Log.d(TAG, "Size of Array : " + filepath.size() + " Size of Thumbs: " + thumbpath.size());

        //true, so only a special intent on back pressed is called, when set to true


        mSlider.removeSliderAt(delete);

        //return to camera if only one photo is left
        clickeddelete = true;
        if(filepath.size()==0){
            onBackPressed();
        }



    }

    @Override
    public void onBackPressed() {
        //only put intent when user has changed a thing
        if(clickeddelete){
            //pass lists with file path of big photo and thumbnails
            Intent intent = new Intent(GalleryView.this,PhotoViewer.class);
            intent.putExtra(INTENT_THUMBNAILS_PATH,thumbpath);
            intent.putExtra(INTENT_PHOTOS_PATH,filepath);
            startActivity(intent);
            Log.d(TAG,"Photos were changed -> pass new filepaths");
        }else{
            super.onBackPressed();
            Log.d(TAG,"No changes detected.");
        }




    }


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

                CreatePDF(filepath);
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


    }

    private void CreatePDF(ArrayList<String> stringArrayList){
        //create PDF from Images
        Document document = new Document();
        FileOutputStream stream;
        String filepathstring = getApplicationContext().getExternalFilesDir("documents/pdf").toString();
        filepathstring += "/" + Long.toString(System.currentTimeMillis()) +".pdf";
        File file = new File(filepathstring);

        try{
            //create new Outputstream
            stream = new FileOutputStream(file);
            PdfWriter.getInstance(document,stream);
            //add image by filepath to the document -> first only one image
            Image image = Image.getInstance(stringArrayList.get(0));
            document.open();
            document.setPageSize(image);
            image.setAbsolutePosition(0,0);
//todo scale image correctly

            image.scaleToFit(1080,1920);
            image.setRotationDegrees(-90f);
            document.add(image);
        }catch (DocumentException|IOException e){
            e.printStackTrace();
        }finally {
            document.close();
            //upload to firebase Database
            FirebaseMethods firebaseMethods = new FirebaseMethods(getApplicationContext());
            Uri uri = Uri.fromFile(file);
            firebaseMethods.uploadFileToStorageNEW(uri,".pdf");
            firebaseMethods.setMethodsInter(new FirebaseMethods.FireBaseMethodsInter() {
                @Override
                public void onUploadSuccess(String filepath, String downloadurl) {
                    Log.d(TAG,"Erstelltes PDF erfolgreich hochgeladen.");
                }

                @Override
                public void onDownloadSuccess(Boolean downloaded) {

                }
            });

        }
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
