package com.ice.tiptext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ice.statusbarlib.StatusBarUtil;
import com.ice.tiptext.util.GetMetaDataUtil;
import com.ice.tiptext.util.LogUtils;
import com.ice.tiptext.widget.CommonSwitch;
import com.ice.tiptext.widget.CommonToast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.purple_500), 0);
        CommonSwitch cs = findViewById(R.id.cs);
        cs.setSlideListener(new CommonSwitch.SlideListener() {
            @Override
            public void open() {
                CommonToast.showToast(MainActivity.this, "ice", CommonToast.LENGTH_SHORT);
            }

            @Override
            public void close() {
                CommonToast.showSuccessToast(MainActivity.this, "ice", CommonToast.LENGTH_SHORT);
            }
        });


        LogUtils.d("ice", GetMetaDataUtil.getApplicationMetaData(this));
    }
}