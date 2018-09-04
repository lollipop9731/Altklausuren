package com.example.loren.altklausurenneu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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



    @BindView(R.id.imagePreview)
    ImageView mFullscreenImage;

    @BindView(R.id.camera_view)
    CameraView cameraView;

    @BindView(R.id.takefoto)
    Button TakeFoto;



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


        ButterKnife.bind(this);
        newFotoapparat();

        TakeFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               PhotoResult photoResult = mfotoapparat.takePicture();


               photoResult.toBitmap()
                       .whenAvailable(new Function1<BitmapPhoto, Unit>() {
                           @Override
                           public Unit invoke(BitmapPhoto bitmapPhoto) {
                               mFullscreenImage.setImageBitmap(bitmapPhoto.bitmap);
                               mFullscreenImage.setRotation(-bitmapPhoto.rotationDegrees);
                               mFullscreenImage.bringToFront();

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

        /*Glide.with(this)
                .load(uri)
                .into(mFullscreenImage);



        //setUritoImage();*/

    }

    private void setUritoImage(){
        Intent intent = getIntent();
        Uri uri = intent.getData();

        if(uri!=null){
            Glide.with(this)
                    .load(uri)
                    .into(mFullscreenImage);
        }


    }

    private Fotoapparat newFotoapparat(){
        mfotoapparat = Fotoapparat
                .with(getApplicationContext())
                .into(cameraView)

                .build();

        return mfotoapparat;
    }
}
