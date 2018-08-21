package com.example.loren.altklausurenneu;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.loren.altklausurenneu.Utils.WheelView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewExamDialog extends DialogFragment {

    private ButtonDialogAction mbuttonDialogAction;


    public static final String TAG = "OneButtonDialogTag";


    //find view by id with butterknife
    @BindView(R.id.dlg_one_button_iv_icon)
    ImageView ivDialogIcon;

    @BindView(R.id.dlg_one_button_tv_title)
    TextView tvTitle;



   @BindView(R.id.dlg_one_button_btn_ok)
   Button btnNeutral;

   @BindView(R.id.dlg_spinner_category)
    Spinner spiCategory;

   @BindView(R.id.dlg_wheel)
   WheelView wheelView;

    protected static final String ARG_BUTTON_TEXT = "ARG_BUTTON_TEXT";
    protected static final String ARG_COLOR_RESOURCE_ID = "ARG_COLOR_RESOURCE_ID";
    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_IMAGE_RESOURCE_ID = "ARG_IMAGE_RESOURCE_ID";
    protected static final String ARG_SPINNER = "SPINNER_CATEGORY";
    private static final double DIALOG_WINDOW_WIDTH = 0.85;



    private int getContentView() {

        return R.layout.dialog_newexam;
    }


    @Override
    public void onStart() {
        super.onStart();
        setDialogWindowWidth(DIALOG_WINDOW_WIDTH);


    }
    private void setDialogWindowWidth(double width) {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display;
        if (window != null) {
            display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);
            int maxWidth = size.x;
            window.setLayout((int) (maxWidth* width), WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get from bundle

        int titleres = getArguments().getInt(ARG_TITLE);
        int message = getArguments().getInt(ARG_MESSAGE);
        int buttontext = getArguments().getInt(ARG_BUTTON_TEXT);
        int image = getArguments().getInt(ARG_IMAGE_RESOURCE_ID);
        int color = getArguments().getInt(ARG_COLOR_RESOURCE_ID);
        int spinner = getArguments().getInt(ARG_SPINNER);

        //Spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tvTitle.setText(titleres);
        tvTitle.setTextColor(getResources().getColor(color));

        btnNeutral.setText(buttontext);
        ivDialogIcon.setImageResource(image);
        spiCategory.setAdapter(adapter);


    }

    @OnClick(R.id.dlg_one_button_btn_ok)
    public void onButtonClicked() {
        closeDialog();
        //open interface -> pass data when button clicked
        if(mbuttonDialogAction != null) {

            mbuttonDialogAction.onDialogClicked(spiCategory.getSelectedItem().toString(),wheelView.getSeletedItem());
        }



    }

    public void closeDialog() {
        if (getDialog().isShowing()) {
            closeKeyboard();
            getDialog().dismiss();
        }
    }
    protected void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
    }

    public static NewExamDialog newInstance(@StringRes int title,  @StringRes int buttontext,
                                            @DrawableRes int imageResId, @ColorRes int color,
                                            @ArrayRes int categories, ButtonDialogAction buttonDialogAction){

        NewExamDialog newExamDialog = new NewExamDialog();
        //interface on new instance
        newExamDialog.mbuttonDialogAction = buttonDialogAction;


        //Supply the construction arguments for this fragment with a bundle
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, title);
        args.putInt(ARG_BUTTON_TEXT, buttontext);
        args.putInt(ARG_IMAGE_RESOURCE_ID, imageResId);
        args.putInt(ARG_COLOR_RESOURCE_ID, color);
        args.putInt(ARG_SPINNER,categories);
        newExamDialog.setArguments(args);

        return newExamDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        //delete the title of the dialog
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }

        //bind new view
        View view = inflater.inflate(getContentView(), container, false);
        ButterKnife.bind(this, view);

        getDialog().setCanceledOnTouchOutside(false);

        wheelView.setOffset(1);
        String[] semester = getResources().getStringArray(R.array.semester_array);
        wheelView.setItems( Arrays.asList(semester));
        wheelView.setSeletion(1);



        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        Log.d(TAG,"Dialog dismissed");
    }

    public interface ButtonDialogAction{

        void onDialogClicked(String category, String semester);
    }








}

