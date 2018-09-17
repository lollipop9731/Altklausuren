package com.example.loren.altklausurenneu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.loren.altklausurenneu.Utils.GalleryRow;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import id.zelory.compressor.Compressor;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;
import io.reactivex.Scheduler;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class CameraViewer extends AppCompatActivity {


    private static final String TAG = "CameraViewer";
    private static final String INTENT_THUMBNAILS_PATH = "Thumbpath";
    private static final String INTENT_PHOTOS_PATH = "Filepath";

    @BindView(R.id.imagePreview)
    ImageView mFullscreenImage;

    @BindView(R.id.textnumberofPhotos)
    Button mNumberofPhotos;

    @BindView(R.id.blinkwhite)
    ImageView fotoAnimation;
    private ArrayList<String> thumbnailpath;
    private File originalimage;
    private String filepath;

    @OnClick(R.id.textnumberofPhotos)
    public void onClick() {

        passFilePath();
        Log.d(TAG, "Number of Filepath: " +filepaths.size());
        Log.d(TAG, "Number of Thumpaths: " +thumbnailpath.size());
    }

    @BindView(R.id.camera_view)
    CameraView cameraView;

    @BindView(R.id.takefoto)
    Button TakeFoto;
    int counter = 0;
    GalleryRow galleryRow;


    RelativeLayout relativeLayout;


    Fotoapparat mfotoapparat;
    ArrayList<String> filepaths;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mfotoapparat.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mfotoapparat.stop();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_photo_viewer);

        filepaths = new ArrayList<>();
        thumbnailpath = new ArrayList<>();

        relativeLayout = (RelativeLayout) findViewById(R.id.layout_photoviewer);


        ButterKnife.bind(this);
        galleryRow = new GalleryRow(relativeLayout, getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            //bundle with information from Gallery View -> so the right number of thumbs is displayed
            thumbnailpath = bundle.getStringArrayList(INTENT_THUMBNAILS_PATH);
            filepaths = bundle.getStringArrayList(INTENT_PHOTOS_PATH);
            galleryRow.updateImages(thumbnailpath,0);
            mNumberofPhotos.bringToFront();
            counter = thumbnailpath.size();
            mNumberofPhotos.setVisibility(View.VISIBLE);
            mNumberofPhotos.setText("    " + counter);
            Log.d(TAG,"Bundle send");
        }

        mFullscreenImage.setVisibility(View.INVISIBLE);
        //show button only if first photo is taken
        if(counter==0){
            mNumberofPhotos.setVisibility(View.INVISIBLE);
            Log.d(TAG,"Circle Invisble cause coúnter =0");
        }

        fotoAnimation.setVisibility(View.INVISIBLE);

        newFotoapparat();

        TakeFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoResult photoResult = mfotoapparat.takePicture();
                //counter for amount of photos
                counter++;

                //save fotos to file, if they should be uploaded later
                filepath = getApplicationContext().getExternalFilesDir("images/temp").toString();

                filepath += "/"+ Long.toString(System.currentTimeMillis()) + ".jpeg";
                originalimage = new File(filepath);
                photoResult.saveToFile(originalimage);








                Log.d(TAG,"Saved to path:" +filepath);

                //collect all filepath in an array
                filepaths.add(filepath);

                AnimateFoto();


                photoResult.toBitmap()
                        .whenAvailable(new Function1<BitmapPhoto, Unit>() {
                            @Override
                            public Unit invoke(BitmapPhoto bitmapPhoto) {
                                //only call at first shot
                                Log.d(TAG, "Byte count:" + bitmapPhoto.bitmap.getByteCount());


                                BackgroundImageResize imageResize = new BackgroundImageResize();
                                imageResize.setRotation(-bitmapPhoto.rotationDegrees);

                                imageResize.execute(bitmapPhoto.bitmap);


                                return null;
                            }
                        });


            }
        });


    }

    @Override
    public void onBackPressed() {
        for (int i = 0; i < filepaths.size(); i++) {
            File file = new File(filepaths.get(i));
            Boolean deleted = file.delete();
            Log.d(TAG, "File deleted: " +deleted + file.getAbsoluteFile());

        }
        filepaths.clear();
        thumbnailpath.clear();
        mfotoapparat.stop();
        Intent intent = new Intent(CameraViewer.this,MainActivity.class);

        startActivity(intent);
    }

    private void passFilePath() {
        Intent intent = new Intent(CameraViewer.this, GalleryView.class);
        intent.putExtra("Filepath", filepaths);
        intent.putExtra("Thumbpath",thumbnailpath);
        startActivity(intent);
    }

    private void AnimateFoto() {

        fotoAnimation.bringToFront();
        Animation animation1 = new AlphaAnimation(0, 1);
        animation1.setDuration(250);
        Animation animation2 = new AlphaAnimation(1, 0);
        animation2.setDuration(250);
        animation2.setFillAfter(false);
        AnimationSet animationset = new AnimationSet(true);
        animationset.addAnimation(animation1);
        animationset.addAnimation(animation2);

        fotoAnimation.startAnimation(animation2);

    }


    private Fotoapparat newFotoapparat() {
        mfotoapparat = Fotoapparat
                .with(getApplicationContext())
                .into(cameraView)

                .build();

        return mfotoapparat;
    }

    public class BackgroundImageResize extends AsyncTask<Bitmap, Void, Bitmap> {
        int imagerotation;

        private static final String TAG = "BackgroundImageResize";

        private void setRotation(int rotation) {
            this.imagerotation = rotation;
        }

        public int getImagerotation() {
            return imagerotation;
        }

        @Override
        protected void onPreExecute() {
            //runs on main thread
            super.onPreExecute();
            Log.d(TAG, "Thumbnail creating started");
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Log.d(TAG, "doInBackgorund started");

            //get thumbnail to display on Camera Preview
            Bitmap thumb = ThumbnailUtils.extractThumbnail(bitmaps[0], 100, 100);
            cacheBitmaps(thumb);



            return thumb;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);




            if (galleryRow != null) {
                Log.d(TAG, "Foto geklickt, Counter: " + counter);
                galleryRow.addImage(bitmap, getImagerotation());
            }

            mNumberofPhotos.bringToFront();
            mNumberofPhotos.setVisibility(View.VISIBLE);
            mNumberofPhotos.setText("    " + counter);

        }

        /**
         * Writes Bitmap to file png and stores path in array
         * @param bitmap
         */
        private void cacheBitmaps(Bitmap bitmap) {

            File f3 = new File(getApplicationContext().getExternalFilesDir("images/temp/bitmap").toString());
            if(!f3.exists()){
                f3.mkdirs();
            }
            OutputStream outputStream = null;
            File file = new File(getApplicationContext().getExternalFilesDir("images/temp/bitmap")  + Long.toString(System.currentTimeMillis())+".png");

            try {
                outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);

                Log.d(TAG, "File written to storage: " + file.getAbsolutePath());
            } catch (Exception e) {
                Log.d(TAG, "File not found");
                e.printStackTrace();
            }finally {
                try{
                    if(outputStream!=null){
                        outputStream.close();

                    }
                }catch (IOException e){
                    e.printStackTrace();
                }


            }
            //add path to array for later delete
            thumbnailpath.add(file.getAbsolutePath());


        }


    }


}
