package com.ice.tiptext.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.DrawableMarginSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.IconMarginSpan;
import android.text.style.ImageSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.ice.tiptext.R;
import com.ice.tiptext.manager.ActivityManager;
import com.ice.tiptext.widget.CommonToast;

public class SpanActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_span);

        ActivityManager.getInstance().addFrontBackCallback((front)->{
            CommonToast.showToast(getApplicationContext(), front+"", Toast.LENGTH_SHORT);
            Log.d("SpanActivity", "front: "+front);
        });

        Activity topActivity = ActivityManager.getInstance().getTopActivity(false);
        if(topActivity!=null) {
            CommonToast.showToast(getApplicationContext(), topActivity.getLocalClassName(), Toast.LENGTH_SHORT);
            Log.d("SpanActivity", "topActivity: "+topActivity.getLocalClassName());
        }

        TextView tv_span_1 = findViewById(R.id.tv_span_1);
        TextView tv_span_2 = findViewById(R.id.tv_span_2);
        TextView tv_span_3 = findViewById(R.id.tv_span_3);
        TextView tv_span_4 = findViewById(R.id.tv_span_4);
        TextView tv_span_5 = findViewById(R.id.tv_span_5);
        TextView tv_span_6 = findViewById(R.id.tv_span_6);
        TextView tv_span_7 = findViewById(R.id.tv_span_7);
        TextView tv_span_8 = findViewById(R.id.tv_span_8);
        TextView tv_span_9 = findViewById(R.id.tv_span_9);
        TextView tv_span_10 = findViewById(R.id.tv_span_10);
        TextView tv_span_11 = findViewById(R.id.tv_span_11);
        TextView tv_span_12 = findViewById(R.id.tv_span_12);
        TextView tv_span_13 = findViewById(R.id.tv_span_13);
        TextView tv_span_14 = findViewById(R.id.tv_span_14);
        TextView tv_span_15 = findViewById(R.id.tv_span_15);
        TextView tv_span_16 = findViewById(R.id.tv_span_16);
        TextView tv_span_17 = findViewById(R.id.tv_span_17);
        TextView tv_span_18 = findViewById(R.id.tv_span_18);

        // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE --- 不包含两端start和end所在的端点              (a,b)
        // Spanned.SPAN_EXCLUSIVE_INCLUSIVE --- 不包含端start，但包含end所在的端点       (a,b]
        // Spanned.SPAN_INCLUSIVE_EXCLUSIVE --- 包含两端start，但不包含end所在的端点   [a,b)
        // Spanned.SPAN_INCLUSIVE_INCLUSIVE--- 包含两端start和end所在的端点                     [a,b]
        // 但实际测试这其中似乎并未有差别，而在start和end相同的情况下，则只对start所在字符的当前行起作用。


        SpannableStringBuilder builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new AbsoluteSizeSpan(60),3,9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_1.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new BackgroundColorSpan(Color.GREEN),3,9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_2.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                Toast.makeText(SpanActivity.this,"ClickableSpan",Toast.LENGTH_SHORT).show();
                Activity topActivity = ActivityManager.getInstance().getTopActivity(false);
                if(topActivity!=null) {
                    CommonToast.showToast(getApplicationContext(), topActivity.getLocalClassName(), Toast.LENGTH_SHORT);
                    Log.d("SpanActivity", "topActivity: "+topActivity.getLocalClassName());
                }
            }
        }, 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_3.setText(builder);
        tv_span_3.setMovementMethod(LinkMovementMethod.getInstance());
        tv_span_3.setHighlightColor(Color.TRANSPARENT);


        builder = new SpannableStringBuilder(tv_span_1.getText());
        Drawable drawable = AppCompatResources.getDrawable(SpanActivity.this,R.mipmap.ic_launcher);
        builder.setSpan(new DrawableMarginSpan(drawable,30), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_4.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new DynamicDrawableSpan() {
            @Override
            public Drawable getDrawable() {
                Drawable drawable =
                        AppCompatResources.getDrawable(SpanActivity.this,R.mipmap.ic_launcher);
                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                return drawable;
            }
        }, 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_5.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new ForegroundColorSpan(Color.GREEN), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_6.setText(builder);

//        builder = new SpannableStringBuilder(tv_span_1.getText());
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        builder.setSpan(new IconMarginSpan(bitmap,30), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        tv_span_7.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new ImageSpan(SpanActivity.this,R.mipmap.ic_launcher), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_8.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        MaskFilter maskFilter = new BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL);
        builder.setSpan(new MaskFilterSpan(maskFilter), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_9.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new QuoteSpan(Color.GREEN), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_10.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new RelativeSizeSpan(2.0f), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_11.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new ScaleXSpan(2.5f), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_12.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new StrikethroughSpan(), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_13.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new StyleSpan(Typeface.BOLD), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_14.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new SubscriptSpan(), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_15.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new SuperscriptSpan(), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_16.setText(builder);

        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new UnderlineSpan(), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_17.setText(builder);
        
        builder = new SpannableStringBuilder(tv_span_1.getText());
        builder.setSpan(new UnderlineSpan(), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.GREEN), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), 3, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_span_18.setText(builder);

    }
}
