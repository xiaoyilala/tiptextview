package com.ice.tiptext.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ice.tiptext.R;


/**
 * 通用按钮组件
 * Created by flli3 on 2019-07-03.
 */

public class CommonButton extends AppCompatButton {
    private static String TAG = "CommonButton";
    //按钮的背景色
    private int backColor = 0;
    //按钮被按下时的背景色
    private int backColorPress = 0;
    //按钮禁用时的背景色
    private int backColorDisable = 0;
    //按钮的背景图片
    private Drawable backGroundDrawable = null;
    //按钮被按下时显示的背景图片
    private Drawable backGroundDrawablePress = null;
    //按钮禁用时的背景图片
    private Drawable backGroundDrawableDisable = null;
    //按钮文字的颜色
    private ColorStateList textColor = null;
    //按钮被按下时文字的颜色
    private ColorStateList textColorPress = null;
    //按钮禁用时文字的颜色
    private ColorStateList textColorDisable = null;
    //是否设置圆角或者圆形等样式
    private boolean fillet = true;
    //标示onTouch方法的返回值，用来解决onClick和onTouch冲突问题
    private boolean isCost = true;

    private GradientDrawable gradientDrawable = null;
    private int mShapeMode = 0;//按钮基础样式 0填充 1边框 2灰色边框
    private int defaultBackColor = getResources().getColor(R.color.color_common_cyan1);
    private int defaultBackColorPress = getResources().getColor(R.color.color_common_cyan2);
    private int defaultBackColorDisable = Color.parseColor("#4d05c1ae");//0x4d05c1ae;   //30%透明度05c1ae
    private float defaultRadius = getResources().getDimension(R.dimen.sw_px8);
    private Drawable defaultBGResource = getResources().getDrawable(R.drawable.bg_button_green_line_8px);
    private Drawable defaultBGResourcePress = getResources().getDrawable(R.drawable.bg_button_green_line_8px_press);
    private Drawable defaultBGResourceDisable = getResources().getDrawable(R.drawable.bg_button_green_line_8px_disable);
    private Drawable defaultGrayBGResource = getResources().getDrawable(R.drawable.bg_button_gray_line_8px);
    private Drawable defaultGrayBGResourcePress = getResources().getDrawable(R.drawable.bg_button_gray_line_8px_press);
    private Drawable defaultGrayBGResourceDisable = getResources().getDrawable(R.drawable.bg_button_gray_line_8px_disable);
    private int defaultTvColor = getResources().getColor(R.color.white);
    private int defaultLineTvColor = getResources().getColor(R.color.color_common_cyan1);
    private int defaultLineTvColorDisable = Color.parseColor("#6605c1ae");//0x6605c1ae; //40%透明度05c1ae
    private int defaultGrayLineTvColor = getResources().getColor(R.color.color_common_black_font_first);
    private int defaultGrayLineTvColorPress = getResources().getColor(R.color.black);
    private int defaultGrayLineTvColorPressDisable = getResources().getColor(R.color.color_common_gray_font);

    public CommonButton(Context context) {
        super(context, null);
    }

    public CommonButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //TypedArray textTypedArray = context.obtainStyledAttributes(attrs,R.styleable.AppCompatTextView,defStyle,0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonButton, defStyle, 0);
        if (a != null) {
            //设置按钮模式，fillet为true时才生效
            mShapeMode = a.getInteger(R.styleable.CommonButton_shape, 0);
            if(mShapeMode == 0) {
                //设置背景色
                initBackColor(a);
                //设置按钮被按下时的背景色
                initBackColorPress(a);
                //设置按钮禁用时的背景色
                initBackColorDisable(a);
                //设置圆角或圆形等样式的背景色
                //fillet = a.getBoolean(R.styleable.CommonButton_fillet, false);
                if (fillet) {
                    getGradientDrawable();
                    if(isEnabled()) {
                        if (backColor != 0) {
                            gradientDrawable.setColor(backColor);
                            //gradientDrawable.setAlpha(Color.alpha(backColor));
                            setBackgroundDrawable(gradientDrawable);
                        }
                    }else{
                        if (backColorDisable != 0) {
                            gradientDrawable.setColor(backColorDisable);
                            //gradientDrawable.setAlpha(Color.alpha(backColorDisable));
                            setBackgroundDrawable(gradientDrawable);
                        }
                    }
                }
                //设置圆角矩形的角度，fillet为true时才生效
                initRadius(a);
            } else {
                //设置背景图片，若backColor与backGroundDrawable同时存在，则backGroundDrawable将覆盖backColor
                initBackDrawable(a);
                //设置按钮被按下时的背景图片
                initBackDrawablePress(a);
                //设置按钮禁用时的背景图片
                initBackDrawableDisable(a);
            }
            //设置文字的颜色
            initTextColor(a);
            //设置按钮被按下时文字的颜色
            initTextColorPress(a);
            //设置按钮被禁用时文字的颜色
            initTextColorDisable(a);
            a.recycle();
        }
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                //根据touch事件设置按下抬起的样式
                return setTouchStyle(event.getAction());
            }
        });
    }

    /**
     * 设置背景色
     * @param typedArray
     */
    private void initBackColor(TypedArray typedArray){
        ColorStateList colorList = typedArray.getColorStateList(R.styleable.CommonButton_backColor);
        if (colorList != null) {
            backColor = colorList.getColorForState(getDrawableState(), defaultBackColor);
        } else {
            backColor = defaultBackColor;
        }
        if (backColor != 0) {
            setBackgroundColor(backColor);
        }
    }

    /**
     * 设置按钮被按下时的背景色
     * @param typedArray
     */
    private void initBackColorPress(TypedArray typedArray){
        ColorStateList colorListPress = typedArray.getColorStateList(R.styleable.CommonButton_backColorPress);
        if (colorListPress != null) {
            backColorPress = colorListPress.getColorForState(getDrawableState(), defaultBackColorPress);
        } else {
            backColorPress = defaultBackColorPress;
        }
    }

    /**
     * 设置按钮禁用时的背景色
     * @param typedArray
     */
    private void initBackColorDisable(TypedArray typedArray){
        ColorStateList colorListPress = typedArray.getColorStateList(R.styleable.CommonButton_backColorDisable);
        if (colorListPress != null) {
            backColorDisable = colorListPress.getColorForState(getDrawableState(), defaultBackColorDisable);
        } else {
            backColorDisable = defaultBackColorDisable;
        }
        if(!isEnabled()){
            setBackgroundColor(backColorDisable);
        }
    }

    /**
     * 设置背景图片，若backColor与backGroundDrawable同时存在，则backGroundDrawable将覆盖backColor
     * @param typedArray
     */
    private void initBackDrawable(TypedArray typedArray){
        backGroundDrawable = typedArray.getDrawable(R.styleable.CommonButton_backGroundImage);
        if (backGroundDrawable == null) {
            switch (mShapeMode) {
                case 1:
                    backGroundDrawable = defaultBGResource;
                    break;
                case 2:
                    backGroundDrawable = defaultGrayBGResource;
            }
        }
        setBackgroundDrawable(backGroundDrawable);
    }

    /**
     * 设置按钮被按下时的背景图片
     * @param typedArray
     */
    private void initBackDrawablePress(TypedArray typedArray){
        backGroundDrawablePress = typedArray.getDrawable(R.styleable.CommonButton_backGroundImagePress);
        if (backGroundDrawablePress == null) {
            switch (mShapeMode) {
                case 1:
                    backGroundDrawablePress = defaultBGResourcePress;
                    break;
                case 2:
                    backGroundDrawablePress = defaultGrayBGResourcePress;
            }
        }
    }

    /**
     * 设置按钮禁用时的背景图片
     * @param typedArray
     */
    private void initBackDrawableDisable(TypedArray typedArray){
        backGroundDrawableDisable = typedArray.getDrawable(R.styleable.CommonButton_backGroundImageDisable);
        if (backGroundDrawableDisable == null) {
            switch (mShapeMode) {
                case 1:
                    backGroundDrawableDisable = defaultBGResourceDisable;
                    break;
                case 2:
                    backGroundDrawableDisable = defaultGrayBGResourceDisable;
            }
        }
        if(!isEnabled()){
            setBackgroundDrawable(backGroundDrawableDisable);
        }
    }

    /**
     * 设置圆角矩形的角度，fillet为true时才生效
     * @param typedArray
     */
    private void initRadius(TypedArray typedArray){
        float radius = typedArray.getDimensionPixelSize(R.styleable.CommonButton_radius, 0);
        if (fillet) {
            if (radius != 0) {
                setRadius(radius);
            }else{
                setRadius(defaultRadius);
            }
        }
    }

    /**
     * 设置文字的颜色
     * @param typedArray
     */
    private void initTextColor(TypedArray typedArray){
        textColor = typedArray.getColorStateList(R.styleable.CommonButton_textColor);
        if (textColor == null) {
            switch (mShapeMode){
                case 0:
                    textColor = ColorStateList.valueOf(defaultTvColor);
                    break;
                case 1:
                    textColor = ColorStateList.valueOf(defaultLineTvColor);
                    break;
                case 2:
                    textColor = ColorStateList.valueOf(defaultGrayLineTvColor);
                    break;
            }
        }
        setTextColor(textColor);
    }

    /**
     * 设置按钮被按下时文字的颜色
     * @param typedArray
     */
    private void initTextColorPress(TypedArray typedArray){
        textColorPress = typedArray.getColorStateList(R.styleable.CommonButton_textColorPress);
        if (textColorPress == null) {
            switch (mShapeMode){
                case 0:
                    textColorPress = ColorStateList.valueOf(defaultTvColor);
                    break;
                case 1:
                    textColorPress = ColorStateList.valueOf(defaultLineTvColor);
                    break;
                case 2:
                    textColorPress = ColorStateList.valueOf(defaultGrayLineTvColorPress);
                    break;
            }
        }
    }

    /**
     * 设置按钮被禁用时文字的颜色
     * @param typedArray
     */
    private void initTextColorDisable(TypedArray typedArray){
        textColorDisable = typedArray.getColorStateList(R.styleable.CommonButton_textColorDisable);
        if (textColorDisable == null) {
            switch (mShapeMode){
                case 0:
                    textColorDisable = ColorStateList.valueOf(defaultTvColor);
                    break;
                case 1:
                    textColorDisable = ColorStateList.valueOf(defaultLineTvColorDisable);
                    break;
                case 2:
                    textColorDisable = ColorStateList.valueOf(defaultGrayLineTvColorPressDisable);
                    break;
            }
        }
        if(!isEnabled()){
            setTextColor(textColorDisable);
        }
    }

    /**
     * 根据按下或者抬起来改变背景和文字样式
     *
     * @param state
     * @return isCost
     * 为解决onTouch和onClick冲突的问题
     * 根据事件分发机制，如果onTouch返回true，则不响应onClick事件
     * 因此采用isCost标识位，当用户设置了onClickListener则onTouch返回false
     */
    private boolean setTouchStyle(int state) {
        if (state == MotionEvent.ACTION_DOWN) {
            if(isEnabled()) {
                if (mShapeMode == 0 && backColorPress != 0) {
                    if (fillet) {
                        gradientDrawable.setColor(backColorPress);
                        //gradientDrawable.setAlpha(Color.alpha(backColorPress));
                        setBackgroundDrawable(gradientDrawable);
                    } else {
                        setBackgroundColor(backColorPress);
                    }
                }
                if (mShapeMode != 0 && backGroundDrawablePress != null) {
                    setBackgroundDrawable(backGroundDrawablePress);
                }
                if (textColorPress != null) {
                    setTextColor(textColorPress);
                }
            }
        }
        if (state == MotionEvent.ACTION_UP) {
            if(isEnabled()) {
                if (mShapeMode == 0 && backColor != 0) {
                    if (fillet) {
                        gradientDrawable.setColor(backColor);
                        //gradientDrawable.setAlpha/(Color.alpha(backColor));
                        setBackgroundDrawable(gradientDrawable);
                    } else {
                        setBackgroundColor(backColor);
                    }
                }
                if (mShapeMode != 0 && backGroundDrawable != null) {
                    setBackgroundDrawable(backGroundDrawable);
                }
                if (textColor != null) {
                    setTextColor(textColor);
                }
            }
        }
        return isCost;
    }

    /**
     * 重写setOnClickListener方法，解决onTouch和onClick冲突问题
     *
     * @param l
     */
    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        isCost = false;
    }

    /**
     * 设置按钮的背景色
     *
     * @param backColor
     */
    public void setBackColor(int backColor) {
        if(mShapeMode != 0)
            return;
        this.backColor = backColor;
        if (fillet) {
            gradientDrawable.setColor(backColor);
            //gradientDrawable.setAlpha(Color.alpha(backColor));
            setBackgroundDrawable(gradientDrawable);
        } else {
            setBackgroundColor(backColor);
        }
    }

    /**
     * 设置按钮被按下时的背景色
     *
     * @param backColorPress
     */
    public void setBackColorPress(int backColorPress) {
        if(mShapeMode != 0)
            return;
        this.backColorPress = backColorPress;
    }

    /**
     * 设置按钮禁用时背景色
     * @param backColorDisable
     */
    public void setBackColorDisable(int backColorDisable){
        if(mShapeMode != 0)
            return;
        this.backColorDisable = backColorDisable;
    }

    /**
     * 设置按钮的背景图片
     *
     * @param backGroundDrawable
     */
    public void setBackGroundDrawable(Drawable backGroundDrawable) {
        if(mShapeMode == 0)
            return;
        this.backGroundDrawable = backGroundDrawable;
        setBackgroundDrawable(backGroundDrawable);
    }

    /**
     * 设置按钮被按下时的背景图片
     *
     * @param backGroundDrawablePress
     */
    public void setBackGroundDrawablePress(Drawable backGroundDrawablePress) {
        if(mShapeMode == 0)
            return;
        this.backGroundDrawablePress = backGroundDrawablePress;
    }

    /**
     * 设置按钮禁用时的背景图片
     *
     * @param backGroundDrawableDisable
     */
    public void setBackGroundDrawableDisable(Drawable backGroundDrawableDisable) {
        if(mShapeMode == 0)
            return;
        this.backGroundDrawableDisable = backGroundDrawableDisable;
    }

    /**
     * 设置文字的颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        if (textColor == 0) return;
        this.textColor = ColorStateList.valueOf(textColor);
        //此处应加super关键字，调用父类的setTextColor方法，否则会造成递归导致内存溢出
        super.setTextColor(this.textColor);
    }

    /**
     * 设置按钮被按下时文字的颜色
     *
     * @param textColorPress
     */
    public void setTextColorPress(int textColorPress) {
        if (textColorPress == 0) return;
        this.textColorPress = ColorStateList.valueOf(textColorPress);
    }

    /**
     * 设置按钮被按下时文字的颜色
     *
     * @param textColorDisable
     */
    public void setTextColorDisable(int textColorDisable) {
        if (textColorDisable == 0) return;
        this.textColorDisable = ColorStateList.valueOf(textColorDisable);
    }

    /**
     * 设置按钮是否设置圆角或者圆形等样式
     *
     * @param fillet
     */
    private void setFillet(boolean fillet) {
        this.fillet = fillet;
        getGradientDrawable();
    }

    /**
     * 设置圆角按钮的角度
     *
     * @param radius
     */
    public void setRadius(float radius) {
        if (!fillet || mShapeMode != 0) return;
        getGradientDrawable();
        gradientDrawable.setCornerRadius(radius);
        setBackgroundDrawable(gradientDrawable);
    }

    /**
     * 设置按钮默认基础样式
     * @param shapeMode
     */
    public void setShapeMode(int shapeMode){
        if(shapeMode >= 0 && shapeMode < 3){
            mShapeMode = shapeMode;
        }
    }

    private void getGradientDrawable() {
        if (gradientDrawable == null) {
            gradientDrawable = new GradientDrawable();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled){
            if(mShapeMode == 0){
                if(backColor != 0){
                    if (fillet) {
                        gradientDrawable.setColor(backColor);
                        //gradientDrawable.setAlpha(Color.alpha(backColor));
                        setBackgroundDrawable(gradientDrawable);
                    } else {
                        setBackgroundColor(backColor);
                    }
                }
            }else{
                if (backGroundDrawable != null) {
                    setBackgroundDrawable(backGroundDrawable);
                }
            }
            if (textColor != null) {
                setTextColor(textColor);
            }
        }else{
            if(mShapeMode == 0){
                if(backColorDisable != 0){
                    if (fillet) {
                        gradientDrawable.setColor(backColorDisable);
                        //gradientDrawable.setAlpha(Color.alpha(backColorDisable));
                        setBackgroundDrawable(gradientDrawable);
                    } else {
                        setBackgroundColor(backColorDisable);
                    }
                }
            }else{
                if (backGroundDrawableDisable != null) {
                    setBackgroundDrawable(backGroundDrawableDisable);
                }
            }
            if (textColorDisable != null) {
                setTextColor(textColorDisable);
            }
        }
    }
}

