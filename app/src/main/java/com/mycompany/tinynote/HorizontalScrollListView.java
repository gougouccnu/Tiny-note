package com.mycompany.tinynote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * 解决横向滑动控件在ListView中的减点问题的ListView
 *
 * @author LRChao
 * @date 2013-3-6
 *
 */
public class HorizontalScrollListView extends ListView {

    private final String TAG = "HorizontalScrollListView";
    private GestureDetector mGestureDetector;
    View.OnTouchListener mGestureListener;

    public HorizontalScrollListView(Context context) {
        super(context);
    }

    public HorizontalScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(new YScrollDetector());
        setFadingEdgeLength(0);
    }

    public HorizontalScrollListView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//		getParent().requestDisallowInterceptTouchEvent(true);
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			LogTag.showTAG_e("onInterceptTouchEvent", "ACTION_DOWN");
//			break;
//		case MotionEvent.ACTION_UP:
//			LogTag.showTAG_e("onInterceptTouchEvent", "ACTION_UP");
//			break;
//		case MotionEvent.ACTION_MOVE:
//			LogTag.showTAG_e("onInterceptTouchEvent", "ACTION_MOVE");
//			break;
//
//		default:
//			break;
//		}
        //LogTag.showTAG_e("onInterceptTouchEvent", super.onInterceptTouchEvent(ev)
        //        || mGestureDetector.onTouchEvent(ev));
        return super.onInterceptTouchEvent(ev)
                || mGestureDetector.onTouchEvent(ev);
    }

    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if(distanceY !=0 && distanceX != 0){
                if (Math.abs(distanceY) >= Math.abs(distanceX)) {
                    return true;
                }
                return false;
            }
            return false;
        }
    }
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			LogTag.showTAG_e("onTouchEvent", "ACTION_DOWN");
//			break;
//		case MotionEvent.ACTION_UP:
//			LogTag.showTAG_e("onTouchEvent", "ACTION_UP");
//			break;
//		case MotionEvent.ACTION_MOVE:
//			LogTag.showTAG_e("onTouchEvent", "ACTION_MOVE");
//			break;
//
//
//		default:
//			break;
//		}
//		LogTag.showTAG_e("onTouchEvent", super.onTouchEvent(ev));
//		LogTag.showTAG_e("onTouchEvent", super.onTouchEvent(ev)|| mGestureDetector.onTouchEvent(ev));
//		return super.onTouchEvent(ev);
//	}
}
