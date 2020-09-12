package de.sit.waterboy.application;

import android.widget.EditText;

public class Validator {

    boolean hasErrors = false;

    public String notEmpty(EditText view){
        String content = view.getText().toString().trim();
        if("".equals(content)){
            view.setError("is empty");
            this.hasErrors = true;
        }
        return content;
    }

    public String integerAndNotEmpty(EditText view){
        String content = view.getText().toString().trim();
        if(!content.matches("[0-9][0-9]*[.]?[0-9]*")
        ||  content.matches("[0-9][0-9]*[.]")){
            view.setError("not a number");
            this.hasErrors = true;
        }
        return content;
    }
}
