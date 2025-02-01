package com.gmwapp.hi_dude.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatButton;

public class DButton extends AppCompatButton {
    private final Context context;

    public DButton(Context context) {
        super(context);
        this.context = context;
    }

    public DButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public DButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
}
