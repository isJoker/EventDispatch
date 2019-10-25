package cn.com.eventlibrary;

import org.jetbrains.annotations.NotNull;

import cn.com.eventlibrary.listener.OnClickListener;
import cn.com.eventlibrary.listener.OnTouchListener;

/**
 * Created by JokerWan on 2019-10-24.
 * Function:
 */
public class View {

    private int left;
    private int top;
    private int right;
    private int bottom;
    private OnTouchListener onTouchListener;
    private OnClickListener onClickListener;

    public View() {
    }

    public View(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public OnTouchListener getOnTouchListener() {
        return onTouchListener;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public boolean isContainer(int x, int y) {
        if (x > left && x < right && y > top && y < bottom) {
            return true;
        }
        return false;
    }

    /**
     * 接收事件分发（在这里不具备事件分发的能力）
     *
     * @param ev
     * @return
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        System.out.println(name + "View  dispatchTouchEvent  " + System.currentTimeMillis());
        //消费
        boolean result = false;
        //是否有onTouchListener 有则执行
        if (onTouchListener != null && onTouchListener.onTouch(this, ev)) {
            result = true;
        }
        //没有消费事件 执行onTouchEvent
        if (!result && onTouchEvent(ev)) {
            result = true;
        }
        return result;
    }

    private boolean onTouchEvent(MotionEvent ev) {
        System.out.println(name + "   onTouchEvent");
        //判断当前控件是否有设置点击事件
        if (onClickListener != null) {
            onClickListener.onClick(this);
            return true;
        }
        return false;
    }

    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String toString() {
        return "" + name;
    }

}
