package com.ice.tiptext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ice.statusbarlib.StatusBarUtil;
import com.ice.tiptext.dialog.BottomDialog;
import com.ice.tiptext.permisson.IOnPermissionCallback;
import com.ice.tiptext.permisson.Permission;
import com.ice.tiptext.permisson.PermissionUtils;
import com.ice.tiptext.ui.BaseActivity;
import com.ice.tiptext.ui.FragmentUseActivity;
import com.ice.tiptext.ui.SpanActivity;
import com.ice.tiptext.util.GetMetaDataUtil;
import com.ice.tiptext.util.LogUtils;
import com.ice.tiptext.widget.CommonSwitch;
import com.ice.tiptext.widget.CommonToast;
import com.ice.tiptext.widget.ExpandableTextView;

import java.util.List;

public class MainActivity extends BaseActivity {

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

        ExpandableTextView etv = findViewById(R.id.etv);
        etv.setText(getString(R.string.etv_str));

        findViewById(R.id.tv_bottom).setOnClickListener(v->{
            BottomDialog bottomDialog = new BottomDialog(MainActivity.this);
            bottomDialog.show();
        });

        findViewById(R.id.tv_span).setOnClickListener(v->{
            start(MainActivity.this, SpanActivity.class);
        });

        findViewById(R.id.tv_fragment).setOnClickListener(v->{
            start(MainActivity.this, FragmentUseActivity.class);
        });

        PermissionUtils.with(this).permission(Permission.Group.STORAGE).request(new IOnPermissionCallback() {
            @Override
            public void hasPermission(List<String> granted, boolean isAll) {

            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {

            }
        });

        LogUtils.d("ice", GetMetaDataUtil.getApplicationMetaData(this));
    }
}