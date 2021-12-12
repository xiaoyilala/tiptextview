package com.ice.tiptext.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.core.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ice.tiptext.R;


/**
 * 通用开关组件
 * Created by flli3 on 2019-07-12.
 */

public class CommonSwitch extends View {

    public int SHAPE_RECT = 1;
    public int SHAPE_CIRCLE = 2;
    private int RIM_SIZE = getResources().getDimensionPixelSize(R.dimen.sw_px2);
    // 3 attributes
    private int mColorOn;
    private int mColorOff;
    private int mColorOnDisable;
    private int mColorOffDisable;

    private boolean isOpen;
    private int shape;
    // varials of drawing
    private Paint paint;
    private Rect backRect;
    private Rect secondRect;
    private Rect frontRect;
    private int mAlpha;
    private int max_left;
    private int min_left;
    private int frontRect_left;
    private int frontRect_left_begin = RIM_SIZE;
    private int eventStartX;
    private int eventLastX;
    private int diffX = 0;

    private int defaultColorOn = getResources().getColor(R.color.color_common_cyan1);
    private int defaultColorOff = getResources().getColor(R.color.color_common_gray_font);
    private int defaultColorOnDisable = getResources().getColor(R.color.color_common_cyan1);
    private int defaultColorOffDisable = getResources().getColor(R.color.color_common_gray_split);
    private int defaultWidth = getResources().getDimensionPixelSize(R.dimen.sw_px100);
    private int defaultHeight = getResources().getDimensionPixelSize(R.dimen.sw_px60);

    private SlideListener listener;

    public interface SlideListener {
        public void open();

        public void close();
    }

    public CommonSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        listener = null;
        paint = new Paint();
        paint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CommonSwitch);
        mColorOn = a.getColor(R.styleable.CommonSwitch_switchColorOn,
                defaultColorOn);
        mColorOff = a.getColor(R.styleable.CommonSwitch_switchColorOff,
                defaultColorOff);
        mColorOnDisable = a.getColor(R.styleable.CommonSwitch_switchColorOnDisable,
                defaultColorOnDisable);
        mColorOffDisable = a.getColor(R.styleable.CommonSwitch_switchColorOffDisable,
                defaultColorOffDisable);
        isOpen = a.getBoolean(R.styleable.CommonSwitch_isOpen, false);
        //shape = a.getInt(R.styleable.CommonSwitch_shapeType, SHAPE_CIRCLE);
        shape = SHAPE_CIRCLE;
        a.recycle();
    }

    public CommonSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonSwitch(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(defaultWidth, widthMeasureSpec);
        int height = measureDimension(defaultHeight, heightMeasureSpec);
        if (shape == SHAPE_CIRCLE) {
            if (width < height)
                width = height * 2;
        }
        setMeasuredDimension(width, height);
        initDrawingVal();
    }

    public void initDrawingVal() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        backRect = new Rect(0, 0, width, height);
        secondRect = new Rect(RIM_SIZE, RIM_SIZE, width - RIM_SIZE, height - RIM_SIZE);
        min_left = RIM_SIZE;
        if (shape == SHAPE_RECT)
            max_left = width / 2;
        else
            max_left = width - (height - 2 * RIM_SIZE) - RIM_SIZE;
        if (isOpen) {
            frontRect_left = max_left;
            mAlpha = 255;
        } else {
            frontRect_left = RIM_SIZE;
            mAlpha = 0;
        }
        frontRect_left_begin = frontRect_left;
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize; // UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shape == SHAPE_RECT) {
            paint.setColor(mColorOff);
            canvas.drawRect(backRect, paint);
            paint.setColor(mColorOn);
            paint.setAlpha(mAlpha);
            canvas.drawRect(backRect, paint);

            frontRect = new Rect(frontRect_left, RIM_SIZE, frontRect_left
                    + getMeasuredWidth() / 2 - RIM_SIZE, getMeasuredHeight()
                    - RIM_SIZE);
            paint.setColor(Color.WHITE);
            canvas.drawRect(frontRect, paint);
        } else {
            // 画圆形
            int radius;
            //灰色边缘 最底层
            radius = backRect.height() / 2;// - RIM_SIZE;
            paint.setColor(mColorOff);
            canvas.drawRoundRect(new RectF(backRect), radius, radius, paint);
            //灰色边缘 最底层 end

            //白色底层 中间层
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(new RectF(secondRect), secondRect.height() / 2, secondRect.height() / 2, paint);
            //白色底层 中间层 end

            paint.setColor(mColorOn);
            paint.setAlpha(mAlpha);
            canvas.drawRoundRect(new RectF(backRect), radius, radius, paint);

            //开关边缘绘制 start
            frontRect = new Rect(frontRect_left - RIM_SIZE / 2, RIM_SIZE / 2, frontRect_left
                    + backRect.height() - RIM_SIZE / 2 * 3, backRect.height() - RIM_SIZE / 2
            );
            paint.setColor(getResources().getColor(R.color.color_common_gray_split));
            paint.setAlpha(255 - mAlpha);
            canvas.drawRoundRect(new RectF(frontRect), radius, radius, paint);
            //开关边缘绘制 end

            frontRect = new Rect(frontRect_left, RIM_SIZE, frontRect_left
                    + backRect.height() - 2 * RIM_SIZE, backRect.height()
                    - RIM_SIZE);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(new RectF(frontRect), radius, radius, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                eventStartX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                eventLastX = (int) event.getRawX();
                diffX = eventLastX - eventStartX;
                int tempX = diffX + frontRect_left_begin;
                tempX = (tempX > max_left ? max_left : tempX);
                tempX = (tempX < min_left ? min_left : tempX);
                if (tempX >= min_left && tempX <= max_left) {
                    frontRect_left = tempX;
                    mAlpha = (int) (255 * (float) tempX / (float) max_left);
                    invalidateView();
                }
                break;
            case MotionEvent.ACTION_UP:
                int wholeX = (int) (event.getRawX() - eventStartX);
                frontRect_left_begin = frontRect_left;
                boolean toRight;
                toRight = (frontRect_left_begin > max_left / 2 ? true : false);
                if (Math.abs(wholeX) < 3) {
                    toRight = !toRight;
                }
                moveToDest(toRight,true);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * draw again
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setSlideListener(SlideListener listener) {
        this.listener = listener;
    }

    public void moveToDest(final boolean toRight,final boolean shouldCallback) {
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    listener.open();
                } else {
                    listener.close();
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (toRight) {
                    while (frontRect_left <= max_left) {
                        mAlpha = (int) (255 * (float) frontRect_left / (float) max_left);
                        invalidateView();
                        frontRect_left += 3;
                        try {
                            Thread.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mAlpha = 255;
                    frontRect_left = max_left;
                    isOpen = true;
                    if (listener != null && shouldCallback)
                        handler.sendEmptyMessage(1);
                    frontRect_left_begin = max_left;

                } else {
                    while (frontRect_left >= min_left) {
                        mAlpha = (int) (255 * (float) frontRect_left / (float) max_left);
                        invalidateView();
                        frontRect_left -= 3;
                        try {
                            Thread.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mAlpha = 0;
                    frontRect_left = min_left;
                    isOpen = false;
                    if (listener != null && shouldCallback)
                        handler.sendEmptyMessage(0);
                    frontRect_left_begin = min_left;
                }
            }
        }).start();

    }

    public void setOpenState(boolean isOpen) {
        this.isOpen = isOpen;
        /*initDrawingVal();
        invalidateView();*/
        moveToDest(isOpen,false);
        /*if (listener != null)
            if (isOpen == true) {
                listener.open();
            } else {
                listener.close();
            }*/
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    private void setShapeType(int shapeType) {
        this.shape = shapeType;
    }
}

