package com.example.loren.altklausurenneu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loren.altklausurenneu.Utils.GalleryRow;
import com.example.loren.altklausurenneu.Utils.GlideApp;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.Format;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PhotoViewer extends AppCompatActivity {


    private static final String TAG ="PhotoViewer" ;
    private Bitmap mThumbBitmap;
    @BindView(R.id.imagePreview)
    ImageView mFullscreenImage;

    @BindView(R.id.camera_view)
    CameraView cameraView;

    @BindView(R.id.takefoto)
    Button TakeFoto;
    int counter = 0;
    GalleryRow galleryRow;
    ArrayList<ImageView>imageViews;
    RelativeLayout relativeLayout;


    Fotoapparat mfotoapparat;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo_viewer);
        imageViews = new ArrayList<>();

        relativeLayout = (RelativeLayout)findViewById(R.id.layout_photoviewer);


        ButterKnife.bind(this);
        newFotoapparat();

        TakeFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               PhotoResult photoResult = mfotoapparat.takePicture();
               counter++;


               photoResult.toBitmap()
                       .whenAvailable(new Function1<BitmapPhoto, Unit>() {
                           @Override
                           public Unit invoke(BitmapPhoto bitmapPhoto) {
                               //only call at first shot
                               Log.d(TAG,"Byte count:" + bitmapPhoto.bitmap.getByteCount());


                               BackgroundImageResize imageResize = new BackgroundImageResize();
                               imageResize.setRotation(-bitmapPhoto.rotationDegrees)    ;

                               imageResize.execute(bitmapPhoto.bitmap);


                               return null;
                           }
                       });


            }
        });




        Intent intent = getIntent();
        Uri uri = intent.getData();

        if(uri==null){
            Log.d("PhotoView:","URI is null ");
        }



    }



    private Fotoapparat newFotoapparat(){
        mfotoapparat = Fotoapparat
                .with(getApplicationContext())
                .into(cameraView)

                .build();

        return mfotoapparat;
    }

    public class BackgroundImageResize extends AsyncTask<Bitmap,Void,Bitmap> {
        int imagerotation;

        private static final String TAG = "BackgroundImageResize";

        private void setRotation(int rotation){
            this.imagerotation = rotation;
        }

        public int getImagerotation() {
            return imagerotation;
        }

        @Override
        protected void onPreExecute() {
            //runs on main thread
            super.onPreExecute();
            Log.d(TAG,"Compressing started");
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Log.d(TAG, "doInBackgorund started");

            byte[] bytes = null;
            Log.d(TAG,"MB before Thumbnail: "+bitmaps[0].getByteCount()/1000000);
            Bitmap thumb = ThumbnailUtils.extractThumbnail(bitmaps[0],100,100);

            Log.d(TAG,"Size of Thumbnail:" + thumb.getByteCount()/1000000);

            return thumb;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(counter==1){
                galleryRow = new GalleryRow(relativeLayout,(int)getResources().getDimension(R.dimen.margin_bottom_galleryrow),bitmap,getApplicationContext(),getImagerotation(),imageViews);

            }
            if(counter>1){
                if(galleryRow!=null){
                    Log.d(TAG,"Foto geklickt, Counter: "+counter);
                    galleryRow.addImage(bitmap,imageViews);
                }
            }

        }


    }



}
