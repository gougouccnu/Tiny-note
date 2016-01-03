package com.mycompany.tinynote;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 自定义支持横向滚动的ListView
 * @author 农民伯伯
 *
 */
public class HVListView extends ListView {

    /** 手势 */
    private GestureDetector mGesture;
    /** 列头 */
    public LinearLayout mListHead;
    /** 偏移坐标 */
    private int mOffset = 0;
    /** 屏幕宽度 */
    private int screenWidth;

    /** 构造函数 */
    public HVListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGesture = new GestureDetector(context, mOnGesture);
    }

    /** 分发触摸事件 */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return mGesture.onTouchEvent(ev);
    }

    /** 手势 */
    private GestureDetector.OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("MainActivity", "onDown happed");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("MainActivity", "onDoubleTap clicked");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            return false;
        }

        /** 滚动 */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            synchronized (HVListView.this) {
                int moveX = (int) distanceX;
                int curX = mListHead.getScrollX();
                int scrollWidth = getWidth();
                int dx = moveX;
                //控制越界问题
                if (curX + moveX < 0)
                    dx = 0;
                if (curX + moveX + getScreenWidth() > scrollWidth)
                    dx = scrollWidth - getScreenWidth() - curX;

                mOffset += dx;
                //根据手势滚动Item视图
                for (int i = 0, j = getChildCount(); i < j; i++) {
                    View child = ((ViewGroup) getChildAt(i)).getChildAt(1);
                    if (child.getScrollX() != mOffset)
                        child.scrollTo(mOffset, 0);
                }
                mListHead.scrollBy(dx, 0);
            }
            requestLayout();
            return true;
        }
    };


    /**
     * 获取屏幕可见范围内最大屏幕
     * @return
     */
    public int getScreenWidth() {
        if (screenWidth == 0) {
            screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
            if (getChildAt(0) != null) {
                screenWidth -= ((ViewGroup) getChildAt(0)).getChildAt(0)
                        .getMeasuredWidth();
            } else if (mListHead != null) {
                //减去固定第一列
                screenWidth -= mListHead.getChildAt(0).getMeasuredWidth();
            }
        }
        return screenWidth;
    }

    /** 获取列头偏移量 */
    public int getHeadScrollX() {
        return mListHead.getScrollX();
    }
}

