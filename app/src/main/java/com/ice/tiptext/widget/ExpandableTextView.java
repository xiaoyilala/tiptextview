/**
 * 展开收缩TextView
 * <p>
 * created by cywu4 on 2017/06/14
 */
package com.ice.tiptext.widget;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ice.tiptext.R;

public class ExpandableTextView extends LinearLayout implements View.OnClickListener {

    /* The default number of lines */
    private static final int MAX_COLLAPSED_LINES = 3;

    /* The default animation duration */
    private static final int DEFAULT_ANIM_DURATION = 300;

    /* The default alpha value when the animation starts */
    private static final float DEFAULT_ANIM_ALPHA_START = 0.7f;

    // TextView to content
    protected TextView mTv;

    // Button to expand/collapse
    protected TextView mButton;

    // Show short version as default.
    private boolean mCollapsed = true;

    private String mStrExpand = "展开";

    private String mStrCollapse = "收起";

    private boolean mRelayout;

    private int mCollapsedHeight;

    private int mTextHeightWithMaxLines;

    private int mMaxCollapsedLines;

    private int mMarginBetweenTxtAndBottom;

    private int mAnimationDuration;

    //是否执行alpha动画
    private boolean mAlphaAnimEnable = false;

    private float mAnimAlphaStart;

    private boolean mAnimating;

    /* Listener for callback */
    private OnExpandStateChangeListener mListener;

    /* For saving collapsed status when used in ListView */
    private SparseBooleanArray mCollapsedStatus;

    private int mPosition;
    private boolean mIsGone = false;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * 对外提供监听
     *
     * @param listener
     */
    public void setOnExpandStateChangeListener(@Nullable OnExpandStateChangeListener listener) {
        mListener = listener;
    }

    /**
     * 对外提供设置文字
     *
     * @param text
     */
    public void setText(@Nullable CharSequence text) {
        mRelayout = true;
        mTv.setText(text);
        mTv.setMaxLines(mCollapsed ? MAX_COLLAPSED_LINES : Integer.MAX_VALUE);
        setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    public void setTextColor(int color){
        mTv.setTextColor(color);
    }


    /**
     * 对外提供设置文字,对List使用，记录了item是否展开等信息
     *
     * @param text
     * @param collapsedStatus
     * @param position
     */
    public void setText(@Nullable CharSequence text, @NonNull SparseBooleanArray collapsedStatus, int position) {
        mCollapsedStatus = collapsedStatus;
        mPosition = position;
        boolean isCollapsed = collapsedStatus.get(position, true);
        clearAnimation();
        mCollapsed = isCollapsed;
        mButton.setText(mCollapsed ? mStrExpand : mStrCollapse);
        setText(text);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        animateArrow(mCollapsed);
        requestLayout();
    }


    @Override
    public void setOrientation(int orientation) {
        if (LinearLayout.HORIZONTAL == orientation) {
            throw new IllegalArgumentException("ExpandableTextView only supports Vertical Orientation.");
        }
        super.setOrientation(orientation);
    }

    /**
     * true 向上旋转
     * false 向下旋转
     *
     * @param shouldRotateUp
     */
    private void animateArrow(boolean shouldRotateUp) {
        Drawable drawable = null;
        if (shouldRotateUp) {
            drawable = ContextCompat.getDrawable(getContext(), R.mipmap.ic_zhankai);
        } else {
            drawable = ContextCompat.getDrawable(getContext(), R.mipmap.ic_shouqi);
        }
        if (drawable == null) {
            return;
        }
        drawable.setBounds(0, 0, getResources().getDimensionPixelOffset(R.dimen.sw_px24), getResources().getDimensionPixelOffset
                (R.dimen.sw_px12));
        mButton.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.sw_px10));
        mButton.setCompoundDrawables(null, null, drawable, null);
    }


    @Override
    public void onClick(View view) {
        if (mButton.getVisibility() != View.VISIBLE) {
            return;
        }
        mCollapsed = !mCollapsed;
        mButton.setText(mCollapsed ? mStrExpand : mStrCollapse);
        animateArrow(mCollapsed);
        if (mCollapsedStatus != null) {
            mCollapsedStatus.put(mPosition, mCollapsed);
        }
        // mark that the animation is in progress
//        mAnimating = true;
//        Animation animation;
//        int endHeight = mCollapsed ? mCollapsedHeight : getHeight() + mTextHeightWithMaxLines - mTv.getHeight();
//        animation = new ExpandCollapseAnimation(this, getHeight(), endHeight);
//        animation.setFillAfter(true);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                applyAlphaAnimation(mTv, mAnimAlphaStart);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                //设置行数，负责结尾不显示...
//                mTv.setMaxLines(mCollapsed ? MAX_COLLAPSED_LINES : Integer.MAX_VALUE);
//                // clear animation here to avoid repeated applyTransformation() calls
//                clearAnimation();
//                // clear the animation flag
//                mAnimating = false;
//                // notify the listener
//                if (mListener != null) {
//                    mListener.onExpandStateChanged(mTv, !mCollapsed);
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//
//        clearAnimation();
//        startAnimation(animation);
        mTv.setMaxLines(mCollapsed ? MAX_COLLAPSED_LINES : Integer.MAX_VALUE);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // while an animation is in progress, intercept all the touch events to children to
        // prevent extra clicks during the animation
        return mAnimating;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViews();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // If no change, measure and return
        if (!mRelayout || getVisibility() == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mRelayout = false;

        // Setup with optimistic case
        // i.e. Everything fits. No button needed
        mButton.setVisibility(View.GONE);
        mTv.setMaxLines(Integer.MAX_VALUE);

        // Measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // If the text fits in collapsed mode, we are done.
        if (mTv.getLineCount() <= mMaxCollapsedLines) {
            return;
        }

        // Saves the text height w/ max lines
        mTextHeightWithMaxLines = getRealTextViewHeight(mTv);

        // Doesn't fit in collapsed mode. Collapse text view as needed. Show
        // button.
        if (mCollapsed) {
            mTv.setMaxLines(mMaxCollapsedLines);
        }

        if (mIsGone) {
            mButton.setVisibility(View.GONE);
        } else {
            mButton.setVisibility(View.VISIBLE);
        }

        // Re-measure with new setup
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mCollapsed) {
            // Gets the margin between the TextView's bottom and the ViewGroup's bottom
            mTv.post(new Runnable() {
                @Override
                public void run() {
                    mMarginBetweenTxtAndBottom = getHeight() - mTv.getHeight();
                }
            });
            // Saves the collapsed height of this ViewGroup
            mCollapsedHeight = getMeasuredHeight();
        }
    }

    @Nullable
    public CharSequence getText() {
        if (mTv == null) {
            return "";
        }
        return mTv.getText();
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        mMaxCollapsedLines = typedArray.getInt(R.styleable.ExpandableTextView_exMaxCollapsedLines, MAX_COLLAPSED_LINES);
        mAnimationDuration = typedArray.getInt(R.styleable.ExpandableTextView_exAnimDuration, DEFAULT_ANIM_DURATION);
        mAlphaAnimEnable = typedArray.getBoolean(R.styleable.ExpandableTextView_exAnimAlphaEnable, mAlphaAnimEnable);
        mAnimAlphaStart = typedArray.getFloat(R.styleable.ExpandableTextView_exAnimAlphaStart, DEFAULT_ANIM_ALPHA_START);
        String expand = typedArray.getString(R.styleable.ExpandableTextView_exExpandButton);
        String collapse = typedArray.getString(R.styleable.ExpandableTextView_exCollapseButton);
        if (expand != null) {
            mStrExpand = expand;
        }
        if (collapse != null) {
            mStrCollapse = collapse;
        }
        typedArray.recycle();
        // enforces vertical orientation
        setOrientation(LinearLayout.VERTICAL);
        // default visibility is gone
        setVisibility(GONE);

    }

    private void findViews() {
        mTv = (TextView) findViewById(R.id.expandable_text);
//        mTv.setOnClickListener(this);
        mButton = (TextView) findViewById(R.id.expandable_button);
        mButton.setText(mCollapsed ? mStrExpand : mStrCollapse);
        mButton.setOnClickListener(this);
    }


    public void setButtonVisibility(boolean isGone) {
        this.mIsGone = isGone;
        if (isGone) {
            mButton.setVisibility(View.GONE);
        } else {
            mButton.setVisibility(View.GONE);
        }

    }

    private static boolean isPostHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void applyAlphaAnimation(View view, float alpha) {
        if (mAlphaAnimEnable) {
            if (isPostHoneycomb()) {
                view.setAlpha(alpha);
            } else {
                AlphaAnimation alphaAnimation = new AlphaAnimation(alpha, alpha);
                // make it instant
                alphaAnimation.setDuration(0);
                alphaAnimation.setFillAfter(true);
                view.startAnimation(alphaAnimation);
            }
        }
    }

    private static int getRealTextViewHeight(@NonNull TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return textHeight + padding;
    }

    class ExpandCollapseAnimation extends Animation {
        private final View mTargetView;
        private final int mStartHeight;
        private final int mEndHeight;

        public ExpandCollapseAnimation(View view, int startHeight, int endHeight) {
            mTargetView = view;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setDuration(mAnimationDuration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final int newHeight = (int) ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight);
            mTv.setMaxHeight(newHeight - mMarginBetweenTxtAndBottom);
            if (Float.compare(mAnimAlphaStart, 1.0f) != 0) {
                applyAlphaAnimation(mTv, mAnimAlphaStart + interpolatedTime * (1.0f - mAnimAlphaStart));
            }
            mTargetView.getLayoutParams().height = newHeight;
            mTargetView.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public interface OnExpandStateChangeListener {
        /**
         * Called when the expand/collapse animation has been finished
         *
         * @param textView   - TextView being expanded/collapsed
         * @param isExpanded - true if the TextView has been expanded
         */
        void onExpandStateChanged(TextView textView, boolean isExpanded);
    }
}