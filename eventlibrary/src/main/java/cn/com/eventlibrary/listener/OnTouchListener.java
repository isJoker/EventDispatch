package cn.com.eventlibrary.listener;


import cn.com.eventlibrary.MotionEvent;
import cn.com.eventlibrary.View;


public interface OnTouchListener {
    boolean onTouch(View view, MotionEvent event);
}
