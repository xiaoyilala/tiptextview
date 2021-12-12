package com.ice.tiptext.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.ice.tiptext.R;


public class TipTextView extends View {

    private float textSpace;
    private float leftTextSize;
    private float rightTextSize;
    private String leftText;
    private String rightText;
    private int leftTextColor;
    private int rightTextColor;
    private int rightTextRectColor;

    private Paint leftPaint;
    private Paint rightPaint;
    private Paint roundRectPaint;

    private Rect leftRect;
    private Rect rightRect;
    private Rect tempRect;

    private int maxLeftWidth;

    private String dot = "...";
    private boolean isNeedDot;
    private String leftTemptext;

    public TipTextView(Context context) {
        this(context, null);
    }

    public TipTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TipTextView);
        leftTextSize = ta.getDimension(R.styleable.TipTextView_left_text_size, 16);
        rightTextSize = ta.getDimension(R.styleable.TipTextView_right_text_size, 16);
        textSpace = ta.getDimension(R.styleable.TipTextView_text_space, 3);
        leftTextColor = ta.getColor(R.styleable.TipTextView_left_text_color, Color.BLACK);
        rightTextColor = ta.getColor(R.styleable.TipTextView_right_text_color, Color.BLACK);
        rightTextRectColor = ta.getColor(R.styleable.TipTextView_right_text_rect_color, Color.BLACK);
        leftText = ta.getString(R.styleable.TipTextView_left_text);
        rightText = ta.getString(R.styleable.TipTextView_right_text);
        ta.recycle();

        leftPaint = new Paint();
        leftPaint.setAntiAlias(true);
        leftPaint.setTextSize(leftTextSize);
        leftPaint.setColor(leftTextColor);

        leftRect = new Rect();
        leftPaint.getTextBounds(leftText, 0, leftText.length(), leftRect);

        rightPaint = new Paint();
        rightPaint.setAntiAlias(true);
        rightPaint.setTextSize(rightTextSize);
        rightPaint.setColor(rightTextColor);

        rightRect = new Rect();
        rightPaint.getTextBounds(rightText, 0, rightText.length(), rightRect);

        tempRect = new Rect();

        roundRectPaint = new Paint();
        roundRectPaint.setAntiAlias(true);
        roundRectPaint.setColor(rightTextRectColor);
        roundRectPaint.setStrokeWidth(sp2px(1));
        roundRectPaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //指定控件的宽高，需要测量
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //左边文字最大宽度
        int specWidth = widthSize - getPaddingLeft() - getPaddingRight() - 3*sp2px((int)textSpace) - rightRect.width() - 2*sp2px(1);
        maxLeftWidth = leftRect.width();
        if(maxLeftWidth>specWidth){
            //左边文字长了需要裁剪
            while(leftText.length()>1 && maxLeftWidth>specWidth){
                leftText = leftText.substring(0,leftText.length()-1);
                leftPaint.getTextBounds(leftText+dot, 0, (leftText+dot).length(), tempRect);
                maxLeftWidth = tempRect.width();
            }
            isNeedDot = true;
        }else{
            maxLeftWidth = leftRect.width();
        }

        int width = 0;
        if (widthMode == MeasureSpec.AT_MOST) {
            //在布局中指定了wrap_content
            width = Math.min(widthSize, maxLeftWidth + getPaddingLeft() + getPaddingRight() + 3*sp2px((int)textSpace) + rightRect.width()+2*sp2px(1));
        } else if (widthMode == MeasureSpec.EXACTLY) {
            //在布局中指定了确定的值
            width = widthSize;
        }

        int height = 0;
        if (heightMode == MeasureSpec.AT_MOST) {
            //在布局中指定了wrap_content
            if(leftRect.height()>rightRect.height()){
                height = Math.max(leftRect.height(), rightRect.height()+2*sp2px(1)+2*(int)textSpace) +getPaddingTop()+getPaddingBottom();
            }else{
                height = Math.max(rightRect.height(), leftRect.height()+2*sp2px(1)+2*(int)textSpace) +getPaddingTop()+getPaddingBottom();
            }

        } else if (heightMode == MeasureSpec.EXACTLY) {
            //在布局中指定了确定的值
            height = heightSize;
        }

        setMeasuredDimension(width, height);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isNeedDot){
            leftTemptext = leftText+dot;
        }else{
            leftTemptext = leftText;
        }
        Paint.FontMetrics fontMetrics = leftPaint.getFontMetrics();
        int dy = (int)((fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent);
        //得到基线（BaseLine）
        int baseLine = getHeight() / 2 + dy;
        int x = getPaddingLeft();
        canvas.drawText(leftTemptext, x, baseLine, leftPaint);

        leftPaint.getTextBounds(leftTemptext, 0, leftTemptext.length(), leftRect);

        fontMetrics = rightPaint.getFontMetrics();
        dy = (int)((fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent);
        //得到基线（BaseLine）
        baseLine = getHeight() / 2 + dy;

        x = (int)(getPaddingLeft()+2*sp2px((int)textSpace)+leftRect.width()+sp2px(1));
        canvas.drawText(rightText, x, baseLine, rightPaint);

        int left = getPaddingLeft()+sp2px((int)textSpace)+leftRect.width()+sp2px(1);
        int top = (getHeight() - rightRect.height()- 2*sp2px((int)textSpace))/2;
        int bottom = top+2*sp2px((int)textSpace)+rightRect.height();
        int right = left+rightRect.width()+2*sp2px((int)textSpace);
        canvas.drawRoundRect(left, top, right, bottom, sp2px(2), sp2px(2),roundRectPaint);
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }
}
