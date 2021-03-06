package com.example.loren.altklausurenneu;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.glide.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.auth.FirebaseAuth;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;

public class GalleryView extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    ArrayList<String> filepathcompressedArray;
    ArrayList<String> thumbpath;
    ArrayList<String> filepath;
    private String TAG = "GalleryView";
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
    private Document document;
    private Exam exam;
    private FirebaseAuth mAuth;
    private FirebaseMethods firebaseMethods;
    private Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery_view);


        mSlider = (SliderLayout) findViewById(R.id.slider);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        viewPagerEx = new ViewPagerEx(getApplicationContext());


        ButterKnife.bind(this);

        deletePhoto.bringToFront();

        exam = new Exam();
        firebaseMethods = new FirebaseMethods(getApplicationContext());


        Bundle bundle = getIntent().getExtras();

        mSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mSlider.setDuration(0);
        mSlider.stopAutoCycle();


        if (bundle != null) {
            filepath = bundle.getStringArrayList("Filepath");
            thumbpath = bundle.getStringArrayList("Thumbpath");


            Log.d(TAG, "Array with filepath created.");
        }
        filepathcompressedArray = new ArrayList<>();

        showSlider(filepath);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();

            }
        });

    }

    public void showSlider(ArrayList<String> filepath) {
        for (int i = 0; i < filepath.size(); i++) {
            defaultSliderView = new DefaultSliderView(this);
            defaultSliderView
                    .image(filepath.get(i))
                    .setOnSliderClickListener(this);

            defaultSliderView.bundle(new Bundle());
            defaultSliderView.getBundle().putString("path", filepath.get(i));


            mSlider.addSlider(defaultSliderView);

        }
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {
        Log.d(TAG, "I: " + i + "v: " + v + "il: " + i1);
    }

    @Override
    public void onSliderClick(BaseSliderView baseSliderView) {


    }

    /**
     * @param images with filepaths from images
     * @return new created PDF as a file
     */
    private File createPdf(ArrayList<String> images) {
        //create PDF from Images
        FileOutputStream stream;
        String filepathstring = getApplicationContext().getExternalFilesDir("documents/pdf").toString();
        filepathstring += "/" + Long.toString(System.currentTimeMillis()) + ".pdf";
        File file = new File(filepathstring);

        try {
            Image image = Image.getInstance(images.get(0));
            //height is flipped cause image must be turned get size for the document
            Rectangle rectangle = new Rectangle(image.getScaledWidth(), image.getScaledHeight());
            document = new Document(rectangle);

            //create new Outputstream -> write to file
            stream = new FileOutputStream(file);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, stream);
            //add image by filepath to the document -> first only one image

            document.open();

            for (int i = 0; i < images.size(); i++) {
                //add all images to pdf
                image = Image.getInstance(images.get(i));
                PdfContentByte canvas = pdfWriter.getDirectContentUnder();
                image.setAbsolutePosition(0, 0);
                canvas.addImage(image);
                document.newPage();
            }


        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();


        }
        return file;
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
        Log.d(TAG, "PageScrollState: " + i);
    }

    /**
     * shows Snackbar with progress turning circle
     *
     * @param text Message to be displayed in Snackbar
     */
    public void showProgressSnackbar(String text) {

        snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_INDEFINITE);
        //todo why so long to show?
        ViewGroup contentLay = (ViewGroup) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(getApplicationContext());
        contentLay.addView(item, 0);
        snackbar.show();
    }

    public void showDialog() {

        NewExamDialog examdialog = DialogFactory.makePDFExamDialog("Change Title!!", R.string.dialog_button, R.array.category, new NewExamDialog.ButtonDialogAction() {
            @Override
            public void onDialogClicked(String category, String semester) {

                showProgressSnackbar("Upload läuft");


                File f3 = new File(getApplicationContext().getExternalFilesDir("images/temp/compressed").toString());
                if (!f3.exists()) {
                    f3.mkdirs();
                }


                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < filepath.size(); i++) {
                    //compress every picture
                    Date date = new Date();
                    File compressedFile = new File(getApplicationContext().getExternalFilesDir("images/temp/compressed") + dateFormat.toString() + ".jpeg");

                    try {
                        compressedFile = new Compressor(getApplicationContext()).compressToFile(new File(filepath.get(i)));
                        Log.d(TAG, "Size of compressed file: " + compressedFile.length() / 1000);
                        Log.d(TAG, "File saved to: " + compressedFile.getAbsolutePath());
                        //if successful add to filepathcompressed
                        filepathcompressedArray.add(compressedFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Image could not be compressed.");
                    }
                }


                //set new Values from dialog
                exam.setCategory(category);
                exam.setSemester(semester);
                //set current user
                mAuth = FirebaseAuth.getInstance();
                if (mAuth != null) {
                    String user = mAuth.getCurrentUser().getUid();
                    exam.setUserid(user);
                }
                //create PDF from array list with compressed images
                File pdf = createPdf(filepathcompressedArray);
                //upload PDF to storage
                Uri uri = Uri.fromFile(pdf);
                firebaseMethods.uploadFileToStorageNEW(uri, ".pdf");
                //interface called when upload was successfully
                firebaseMethods.setMethodsInter(new FirebaseMethods.FireBaseMethodsInter() {
                    @Override
                    public void onUploadSuccess(String filepath, String downloadurl) {
                        //set filepath and download url to new exam
                        exam.setDownloadurl(downloadurl);
                        exam.setFilepath(filepath);
                        //write exam to Database
                        firebaseMethods.uploadNewExam(exam);
                        snackbar.dismiss();
                        //Go To Main
                        Intent intent = new Intent(GalleryView.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onDownloadSuccess(Boolean downloaded) {

                    }
                });


            }
        });
        examdialog.show(getFragmentManager(), NewExamDialog.TAG);
    }

    @OnClick(R.id.deletefoto)
    public void onPhotoDeleted() {


        Log.d(TAG, "Size of Array First : " + filepath.size());
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
        if (filepath.size() == 0) {
            onBackPressed();
        }


    }

    @Override
    public void onBackPressed() {
        //only put intent when user has changed a thing
        if (clickeddelete) {
            //pass lists with file path of big photo and thumbnails
            Intent intent = new Intent(GalleryView.this, CameraViewer.class);
            intent.putExtra(INTENT_THUMBNAILS_PATH, thumbpath);
            intent.putExtra(INTENT_PHOTOS_PATH, filepath);
            startActivity(intent);
            Log.d(TAG, "Photos were changed -> pass new filepaths");
        } else {
            super.onBackPressed();
            Log.d(TAG, "No changes detected.");
        }


    }

}
