package com.example.nomad_len.bt_duckie;

import android.app.Activity;
import android.inputmethodservice.KeyboardView;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.support.v4.content.ContextCompat.getSystemService;

public class CustomKeyboard {

    private KeyboardView mKeyboardView;
    private Activity mHostActivity;

    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public void showCustomKeyboard( View v ) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        //if( v!=null ) ((InputMethodManager)Activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

}
