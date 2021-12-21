package com.ice.tiptext.ui;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public void start(Context context, Class clazz){
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }
}
