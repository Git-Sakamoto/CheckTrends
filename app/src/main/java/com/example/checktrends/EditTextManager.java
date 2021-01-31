package com.example.checktrends;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTextManager {
    Context context;

    public EditTextManager(Context context){
        this.context = context;
    }

    public boolean isEmpty(EditText...editText){
        boolean empty = false;

        for(EditText text : editText){
            if(TextUtils.isEmpty(text.getText().toString())) {
                text.setError(context.getString(R.string.error_message_is_empty));
                empty = true;
            }
        }

        return empty;
    }

}
