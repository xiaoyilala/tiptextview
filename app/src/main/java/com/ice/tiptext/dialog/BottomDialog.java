package com.ice.tiptext.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.ice.tiptext.R;

public class BottomDialog extends Dialog {
    public BottomDialog(@NonNull Context context) {
        super(context, R.style.BottomDialogFragment);
        setContentView(R.layout.dialog_bottom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) getContext().getResources().getDimension(R.dimen.sw_px600);
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
    }
}
