package com.ice.tiptext.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.ice.tiptext.R;


/**
 * 通用小红点
 * Created by flli3 on 2019-12-06.
 */
public class CommonBadgeView extends View {
    // 未读数; 在显示的时候 未读数默认显示形式9/23/99+
    private int badgeNum;
    private int badgeBackgroundColor;
    private int badgeNumColor;
    private float badgeNumSize;
    // 是否显示数字, 默认显示小红点
    private boolean showNum = false;

    private boolean showBitmap = false;

    // 不显示数字时, 小红点的大小, 不包括边线
    private float badgeRedSize;
    // 边线, 有些小红点外边有白边, 若是设置了宽度,则会添加边线; 边线算在Badge整个的大小当中
    private float badgeBorderWidth;
    private int badgeBorderColor;
    // 有些设计要求未读前面加"+", (至少我们设计师这么设计) 显示成 +1/+34/+99
    private String badgeNumPre;
    // badge的左下角 相对于 text/icon 右上角的相对位置,
    // 默认是( badgeHeight/2 ), 正好覆盖一个角
    private float badgeBottom;
    private float badgeLeft;
    // 是否自己设置了
    private boolean hasBadgeBottomAttr;
    private boolean hasBadgeLeftAttr;

    // view设置的padding
    private float viewPaddingLeft;
    private float viewPaddingTop;
    private float viewPaddingRight;
    private float viewPaddingBootom;
    ////可设置部分 end///////////////////////////////////////////////

    // 小红点真实大小 比 文本 的margin(不包括白边)
    private static final int BADGE_TEXT_MARGIN_LEFT = 10;
    private static final int BADGE_TEXT_MARGIN_TOP = 6;
    private static final int BADGE_TEXT_MARGIN_RIGHT = 10;
    private static final int BADGE_TEXT_MARGIN_BOOTOM = 6;

    /*private static final int BADGE_TEXT_MARGIN_LEFT = 0;
    private static final int BADGE_TEXT_MARGIN_TOP = 0;
    private static final int BADGE_TEXT_MARGIN_RIGHT = 0;
    private static final int BADGE_TEXT_MARGIN_BOOTOM = 0;*/


    // 可以设置padding
    private static final int VIEW_PADDING = 0;

    ////以下是辅助变量///////////////////////////////////////////////
    // 整个View的真实大小
    private float viewHeight;
    private float viewWidth;

    // 小红点有向右突出部分,为保证主体部分水平居中, 需要设置两边的margin
    private float mainMarginHorizontal;
    // 小红点有向上突出部分,就算没有未读数,也需要预留出位置, 设置Top即可
    private float mainMarginTop;
    // badge的整体宽高
    private float badgeHeight;
    private float badgeWidth;
    // badgeNum/小红点 的真实宽高
    private float badgeNumHeight;
    private float badgeNumWidth;
    // icon
    private Bitmap mBitmap;
    // 未读数显示的文案; 未读数默认显示形式9/23/99+
    private String showUneadText;

    // 画笔
    private Paint contentPaint;
    private TextPaint badgeNumPaint;

    public CommonBadgeView(Context context) {
        this(context, null);
    }

    public CommonBadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CommonBadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BadgeView);

        badgeNum = array.getInteger(R.styleable.BadgeView_badgeNum, 0);
        badgeBackgroundColor = array.getColor(R.styleable.BadgeView_badgeBackgroundColor, getResources().getColor(R.color.color_common_tip_point));
        badgeNumColor = array.getColor(R.styleable.BadgeView_badgeNumColor, Color.WHITE);
        badgeNumSize = array.getDimension(R.styleable.BadgeView_badgeNumSize, getResources().getDimension(R.dimen.sw_px20));
        showNum = array.getBoolean(R.styleable.BadgeView_showNum, false);
        badgeRedSize = array.getDimension(R.styleable.BadgeView_badgeRedSize, getResources().getDimension(R.dimen.sw_px12));
        badgeBorderColor = array.getColor(R.styleable.BadgeView_badgeBorderColor, Color.WHITE);
        badgeBorderWidth = array.getDimension(R.styleable.BadgeView_badgeBorderWidth, 0);
        if (badgeBorderWidth < 0) {
            badgeBorderWidth = 0;
        }
        badgeNumPre = array.getString(R.styleable.BadgeView_badgeNumPre);

        // 初始化badgeNum的画笔
        badgeNumPaint = new TextPaint();
        badgeNumPaint.setAntiAlias(true);
        badgeNumPaint.setColor(badgeNumColor);
        badgeNumPaint.setTextSize(badgeNumSize);
        badgeNumPaint.setTextAlign(Paint.Align.CENTER);
        // 计算 未读数的高度
        String minBadge = getUnreadText(0);
        Rect minBadgeRect = new Rect();
        badgeNumPaint.getTextBounds(minBadge, 0, minBadge.length(), minBadgeRect);
        // 计算badge的高度
        badgeNumHeight = minBadgeRect.height();
        badgeHeight = badgeNumHeight + BADGE_TEXT_MARGIN_TOP + BADGE_TEXT_MARGIN_BOOTOM + badgeBorderWidth * 2;
        Log.i("lifangliang", "badgeHeight ==" + badgeHeight);
        // 限制设置小红点的大小不能超过数字显示模式; 显示在文字模式大小的左下角
        if (badgeRedSize > badgeNumHeight + BADGE_TEXT_MARGIN_TOP + BADGE_TEXT_MARGIN_BOOTOM) {
            badgeRedSize = badgeNumHeight + BADGE_TEXT_MARGIN_TOP + BADGE_TEXT_MARGIN_BOOTOM;
        }
        // 获取位置
        hasBadgeBottomAttr = array.hasValue(R.styleable.BadgeView_badgeBottom);
        hasBadgeLeftAttr = array.hasValue(R.styleable.BadgeView_badgeLeft);
        badgeBottom = array.getDimension(R.styleable.BadgeView_badgeBottom, 0);
        badgeLeft = array.getDimension(R.styleable.BadgeView_badgeLeft,  0);
        //关闭清空TypedArray
        array.recycle();

        contentPaint = new Paint();
        contentPaint.setAntiAlias(true);
    }

    public void setNum(int badgeNum) {
        this.badgeNum = badgeNum;
        setShowNum(true);
    }

    public void setShowNum(boolean isShow) {
        this.showNum = isShow;
        redraw();
    }

    public void setBitmap(Bitmap bitmap){
        mBitmap = bitmap;
        showBitmap = true;
        redraw();
    }

    public void setBadgeLocation(float bottom, float left) {
        this.badgeBottom = bottom;
        this.badgeLeft = left;
        hasBadgeBottomAttr = true;
        hasBadgeLeftAttr = true;
    }

    /**
     * 重新计算绘制这个View
     */
    public void redraw() {
        // 需要重新计算高宽,所以用这个
        requestLayout();
//        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*if (viewWidth != viewMinWidth || viewHeight != viewMinHeight) {
            canvas.save();
            // 若是设置的高宽大于所需要的高宽, 对画布进行操作
            float paddingLeft = viewPaddingLeft + (viewWidth - viewPaddingLeft - viewPaddingRight - viewMinWidth) / 2;
            float paddingTop = viewPaddingTop + (viewHeight -viewPaddingTop -viewPaddingBootom - viewMinHeight) / 2;
            // 移动布局, 改变原点
            canvas.translate(paddingLeft, paddingTop);
        }*/

        onDrawContent(canvas);

        /*if (viewWidth != viewMinWidth || viewHeight != viewMinHeight) {
            canvas.restore();
        }*/

    }

    /**
     * 绘制整个内容
     * @param canvas
     */
    private void onDrawContent(Canvas canvas) {
        canvas.save();
        // 移动布局, 改变原点
        //canvas.translate(badgeWidth, 0);
        canvas.translate(0, 0);
        oDrawBadge(canvas);

        canvas.restore();
    }

    private void oDrawBadge(Canvas canvas) {
        if(showBitmap){
            if(mBitmap != null)
                canvas.drawBitmap(mBitmap,0,0,  new Paint(Paint.ANTI_ALIAS_FLAG));
            return;
        }
        // 若有小红点有边缘线, 画边缘线
        if (badgeBorderWidth > 0) {
            contentPaint.setStyle(Paint.Style.STROKE);
            contentPaint.setColor(badgeBorderColor);
            contentPaint.setStrokeWidth(badgeBorderWidth);
            if (!showNum) {
                // 不显示数字
                canvas.drawCircle(badgeWidth / 2, badgeHeight - badgeRedSize / 2 - badgeBorderWidth, badgeRedSize / 2, contentPaint);
            } else if (badgeWidth == badgeHeight) {
                // 显示是字符串长度为1时, 为正圆
                canvas.drawCircle(badgeWidth / 2, badgeHeight / 2, badgeWidth / 2, contentPaint);
            } else {
                // 椭圆
                Path borderPath = new Path();
                borderPath.addArc(new RectF(0, 0, badgeHeight, badgeHeight), 90, 180);
                borderPath.lineTo(badgeWidth - badgeHeight / 2, 0);
                borderPath.addArc(new RectF(badgeWidth - badgeHeight, 0, badgeWidth, badgeHeight), 270, 180);
                borderPath.lineTo(badgeHeight / 2, badgeHeight);
                canvas.drawPath(borderPath, contentPaint);
            }
        }
        contentPaint.setColor(badgeBackgroundColor);
        contentPaint.setStyle(Paint.Style.FILL);
        if (showNum) {
            // 绘制红色背景图
            Path path = new Path();
            path.addArc(new RectF(badgeBorderWidth, badgeBorderWidth, badgeHeight - badgeBorderWidth, badgeHeight - badgeBorderWidth), 90, 180);
            path.lineTo(badgeWidth - badgeHeight / 2 + badgeBorderWidth, badgeBorderWidth);
            path.addArc(new RectF(badgeWidth - badgeHeight + badgeBorderWidth, badgeBorderWidth, badgeWidth - badgeBorderWidth, badgeHeight - badgeBorderWidth), 270, 180);
            path.lineTo(badgeHeight / 2 - badgeBorderWidth, badgeHeight - badgeBorderWidth);
            canvas.drawPath(path, contentPaint);
            // 写上数字
            canvas.drawText(showUneadText, badgeWidth / 2, badgeHeight - BADGE_TEXT_MARGIN_BOOTOM - badgeBorderWidth, badgeNumPaint);

        } else {
            // 画实心圆
            canvas.drawCircle(badgeRedSize / 2 + badgeBorderWidth, /*badgeHeight - badgeRedSize / 2 - badgeBorderWidth*/badgeRedSize / 2, badgeRedSize / 2, contentPaint);
        }
    }

    private void intParams() {
        // 初始化Badge的数据
        if (showNum) {
            showUneadText = getUnreadText(badgeNum);
            Rect badgeRect = new Rect();
            badgeNumPaint.getTextBounds(showUneadText, 0, showUneadText.length(), badgeRect);

            badgeNumWidth = badgeRect.width();

            if (showUneadText.length() == 1) {
                // 当长度为1的时候,显示正圆
                badgeWidth = badgeHeight;
            } else {
                badgeWidth = badgeNumWidth + BADGE_TEXT_MARGIN_LEFT + BADGE_TEXT_MARGIN_RIGHT + badgeBorderWidth * 2;
            }
        } else {
            badgeWidth = badgeRedSize + badgeBorderWidth * 2;
        }
        // badgeHeight在构造方法中初始化了, 全部使用数字模式的高度

        // Badge位置设置的范围做一个限制
        /*if (!hasBadgeLeftAttr || badgeLeft > mainWidth) {
            badgeLeft = getBadgeDefaultLocation();
        }
        if (!hasBadgeBottomAttr || badgeBottom > mainHeight) {
            badgeBottom = getBadgeDefaultLocation();
        }
*/
        // 计算整体内容的大小
        mainMarginHorizontal = badgeWidth - badgeLeft;
        mainMarginTop = badgeHeight - badgeBottom;
    }

    /**
     * 获取默认的位置
     * @return
     */
    private float getBadgeDefaultLocation() {
        // 文字的时候默认往上些, 盖住文字了
        //return iconSrc != 0 ? (showNum ? badgeHeight / 2 : badgeRedSize / 2 + badgeBorderWidth) : badgeRedSize / 2 + badgeBorderWidth - 3;
        return showNum ? badgeHeight / 2 : badgeRedSize / 2 + badgeBorderWidth;
    }

    /**
     * 构造未读数显示的文本
     * 1) 未读数默认显示形式9/23/99+
     * 2) 有些设计要求未读前面加"+", (至少我们设计师这么设计) 显示成 +1/+34/+99, 取配置badgeNumPre
     * @param unread
     * @return
     */
    private String getUnreadText(int unread) {
        String text = String.valueOf(unread);
        if (TextUtils.isEmpty(badgeNumPre)) {
            if (unread > 99) {
                text = "99+";
            }
        } else {
            if (unread > 99) {
                text = badgeNumPre + "99";
            } else if (unread >= 0) {
                text = badgeNumPre + unread;
            }
        }
        return text;
    }

    private int dip2px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    private int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }


    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算高宽
        intParams();
        viewPaddingLeft = getPaddingLeft();
        viewPaddingTop = getPaddingTop();
        viewPaddingRight = getPaddingRight();
        viewPaddingBootom = getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //Measure Width
        /*if (widthMode == MeasureSpec.EXACTLY) {
            // 设置的大小不能比内容还小
            viewWidth = widthSize < viewMinWidth ? viewMinWidth : widthSize;
        } else {
            viewWidth = viewMinWidth;
        }*/
        viewWidth = badgeWidth;
        viewWidth += viewPaddingLeft + viewPaddingRight;
        Log.i("lifangliang","viewWidth ===" + viewWidth);
        //Measure Height
        /*if (heightMode == MeasureSpec.EXACTLY) {
            // 设置的大小不能比内容还小
            viewHeight = heightSize < viewMinHeight ? viewMinHeight : heightSize;
        } else {
            viewHeight = viewMinHeight;
        }
        if (viewHeight < viewMinHeight + VIEW_PADDING * 2) {
            viewHeight =  viewMinHeight + VIEW_PADDING * 2;
        }*/
        viewHeight = badgeHeight;
        viewHeight += viewPaddingTop + viewPaddingBootom;
        Log.i("lifangliang","viewHeight ===" + viewHeight);
        //MUST CALL THIS
        setMeasuredDimension((int) Math.ceil(viewWidth), (int) Math.ceil(viewHeight));
    }
}
