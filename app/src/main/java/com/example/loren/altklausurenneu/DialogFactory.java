package com.example.loren.altklausurenneu;

import android.support.annotation.StringRes;

//class to encapsulate Dialogs
public class DialogFactory {

    public DialogFactory() {
    }

    //returns newExamDialog
     static NewExamDialog  makeExamDialog(@StringRes int title, @StringRes int message, @StringRes int buttontext,@StringRes int categories, NewExamDialog.ButtonDialogAction action){
        return NewExamDialog.newInstance(title,
                message,
                buttontext,
                R.drawable.ic_pdf,
                R.color.grey_text,
                categories,
                action);


     }
}
