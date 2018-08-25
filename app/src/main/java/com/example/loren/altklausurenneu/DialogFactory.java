package com.example.loren.altklausurenneu;

import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;

//class to encapsulate Dialogs
public class DialogFactory {

    public DialogFactory() {
    }

    //returns newExamDialog
     static NewExamDialog  makePDFExamDialog(@StringRes int title,  @StringRes int buttontext, @ArrayRes int categories, NewExamDialog.ButtonDialogAction action){
        return NewExamDialog.newInstance(title,
                buttontext,
                R.drawable.ic_pdf_neu,
                R.color.grey_text,
                categories,
                action);


     }

     static NewExamDialog makeJPEGExamDialog(@StringRes int title,  @StringRes int buttontext, @ArrayRes int categories, NewExamDialog.ButtonDialogAction action){
         return NewExamDialog.newInstance(title,
                 buttontext,
                 R.drawable.ic_jpg,
                 R.color.grey_text,
                 categories,
                 action);


     }
}
