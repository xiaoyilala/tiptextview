package com.ice.tiptext.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ice.tiptext.R;


/**
 * 通用Toast提示
 * Created by flli3 on 2019-11-13.
 */
public class CommonToast {

    private static final String TAG = "CommonToast";

    public static final int LENGTH_SHORT = 2000;
    public static final int LENGTH_LONG = 3000;
    private static final int MSG_SHOW = 0x0001;

    private static Toast mToast;
    private static Toast mViewToast;
    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW:
                    Context context = (Context) msg.obj;
                    Bundle bundle = msg.getData();
                    String text = "";
                    int gravity = Gravity.BOTTOM;
                    if(bundle != null){
                        text = bundle.getString("text");
                        gravity = bundle.getInt("gravity");
                    }
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = new Toast(context);
                    if(!TextUtils.isEmpty(text)){
                        View view = View.inflate(context, R.layout.ui_common_toast, null);
                        TextView tv = view.findViewById(R.id.txt_toast);
                        tv.setText(TextUtils.isEmpty(text) ? "" : text);
                        if(gravity == Gravity.CENTER) {
                            mToast.setGravity(Gravity.CENTER, 0, 0);
                        }else {
                            mToast.setGravity(gravity, 0,
                                    context.getResources().getDimensionPixelOffset(R.dimen.sw_px210));
                        }
                        mToast.setView(view);
                        mToast.show();
                    }
                    break;
            }
        }
    };
    private static Runnable toastCel = new Runnable() {
        public void run() {
            if (mToast != null) {
                mToast.cancel();
            }
        }
    };


    /**
     * @param context
     * @param text
     * @param duration
     */
    public static void showCenterToast(Context context, String text, int duration) {
        showToast(context, text, duration, Gravity.CENTER);
    }

    /**
     * @param context
     * @param text
     * @param duration
     */
    public static void showToast(Context context, String text, int duration) {
        showToast(context, text, duration, Gravity.BOTTOM);
    }

    /**
     *
     * @param context
     * @param text
     * @param duration
     * @param gravity 屏幕中显示位置
     */
    private static void showToast(Context context, String text, int duration, int gravity) {
        if (context == null) {
            return;
        }
        if(TextUtils.isEmpty(text)){
            return;
        }
        if (duration <= 1) {
            duration = LENGTH_SHORT;
        }

        //每14个字添加换行符
        String regex = "(.{14})";
        String result = text.replaceAll(regex, "$1\n");
        //移除刚好14个字时末尾添加的换行
        if(text.length()%14 == 0){
            result = result.substring(0,result.length()-1);
        }

        mHandler.removeCallbacks(toastCel);

        Message msg = Message.obtain();
        msg.what = MSG_SHOW;
        msg.obj = context;
        Bundle bundle = new Bundle();
        bundle.putString("text",result);
        bundle.putInt("gravity", gravity);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mHandler.postDelayed(toastCel, duration);
    }

    public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getResources().getString(resId), duration);
    }

    public static void showViewToast(Context mContext, int layoutId,
                                     int duration) {
        mHandler.removeCallbacks(toastCel);
        //if (mViewToast == null) {
            mViewToast = new Toast(mContext);
        //}
        View view = View.inflate(mContext, layoutId, null);
        mViewToast.setView(view);
        mViewToast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间 100为向下移100dp
        mViewToast.setDuration(duration);
        mViewToast.show();
    }

    //add by yfren
    public static void showViewToast(Context mContext, View view,
                                     int duration) {
        mHandler.removeCallbacks(toastCel);
        //if (mViewToast == null) {
            mViewToast = new Toast(mContext);
        //}
        mViewToast.setView(view);
        mViewToast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间 100为向下移100dp
        mViewToast.setDuration(duration);
        mViewToast.show();
    }

    //add by yfren
    public static void showNativeToast(final Context context, final String txt,
                                final int duration){
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast.makeText(context, txt, duration).show();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, txt, duration).show();
                }
            });
        }
    }

    /**
     * 显示成功提示toast √
     * @param context
     * @param text
     * @param duration
     */
    public static void showSuccessToast(final Context context, final String text, final int duration){
        showViewToast(context,R.mipmap.ui_icon_toast_success,text,duration);
    }

    /**
     * 显示错误提示toast X
     * @param context
     * @param text
     * @param duration
     */
    public static void showFailToast(final Context context, final String text, final int duration){
        showViewToast(context,R.mipmap.ui_icon_toast_fail,text,duration);
    }

    /**
     * 显示操作异常提示toast ！
     * @param context
     * @param text
     * @param duration
     */
    public static void showWarningToast(final Context context, final String text, final int duration){
        showViewToast(context,R.mipmap.ui_icon_toast_warning,text,duration);
    }

    /**
     * 显示toast 带图片和文字
     * @param context
     * @param imgId
     * @param text
     * @param duration
     */
    private static void showViewToast(Context context, int imgId,String text,
                                     int duration) {
        mHandler.removeCallbacks(toastCel);
        if(mViewToast != null){
            mViewToast.cancel();
        }
        //if (mViewToast == null) {
        mViewToast = new Toast(context);
        //}
        View view = View.inflate(context, R.layout.ui_common_img_toast, null);
        TextView tv = view.findViewById(R.id.txt_toast);
        ImageView img = view.findViewById(R.id.img_toast);
        if(TextUtils.isEmpty(text)){
            tv.setVisibility(View.GONE);
        }else{
            tv.setText(text);
        }
        img.setBackgroundResource(imgId);

        mViewToast.setView(view);
        mViewToast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间 100为向下移100dp
        mViewToast.setDuration(duration);
        mViewToast.show();
    }
}
