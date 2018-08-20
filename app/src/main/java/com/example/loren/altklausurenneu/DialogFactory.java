package com.example.loren.altklausurenneu;

import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;

//class to encapsulate Dialogs
public class DialogFactory {

    public DialogFactory() {
    }

    //returns newExamDialog
     static NewExamDialog  makeExamDialog(@StringRes int title,  @StringRes int buttontext, @ArrayRes int categories, NewExamDialog.ButtonDialogAction action){
        return NewExamDialog.newInstance(title,
                buttontext,
                R.drawable.ic_pdf,
                R.color.grey_text,
                categories,
                action);


     }
}
