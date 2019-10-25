package cn.com.eventlibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JokerWan on 2019-10-24.
 * Function: 容器类
 */
public class ViewGroup extends View {

    private static final int MAX_RECYCLED = 32;
    //存放子控件,用List方便转为数组
    List<View> childList = new ArrayList<>();
    private View[] mChildren = new View[0];
    //存放事件分发顺序
    private TouchTarget mFirstTouchTarget;

    public ViewGroup(int left, int top, int right, int bottom) {
        super(left, top, right, bottom);
    }

    /**
     * 添加子控件
     *
     * @param view .
     */
    public void addView(View view) {
        if (view == null) {
            return;
        }
        childList.add(view);
        mChildren = childList.toArray(new View[childList.size()]);
    }

    //事件分发的入口
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        System.out.println(name + "  dispatchTouchEvent  " + System.currentTimeMillis());
        boolean handled = false;

        //判断是否需要拦截
        TouchTarget newTouchTarget = null;
        boolean intercepted = onInterceptTouchEvent(ev);
        int actionMasked = ev.getActionMasked();
        if (actionMasked != MotionEvent.ACTION_CANCEL && !intercepted) {
            //down事件
            if (actionMasked == MotionEvent.ACTION_DOWN) {
                //循环遍历控件 倒叙遍历
                View[] children = mChildren;
                //耗时 在源码中会根据Z轴重新排序
                for (int i = children.length - 1; i >= 0; i--) {
                    //获取当前的子控件
                    View child = children[i];
                    //child能否去接受事件
                    if (!child.isContainer(ev.getX(), ev.getY())) {
                        //不在控件内
                        continue;
                    }

                    //child能接受事件 分发给子控件
                    if (dispatchTransformedTouchEvent(ev, child)) {
                        handled = true;
                        newTouchTarget = addTouchTarget(child);
                        break;
                    }
                }
            }
        }
        if (mFirstTouchTarget == null) {
            //没有事件链表（无子控件消费事件，自己用）
            handled = dispatchTransformedTouchEvent(ev, null);
        } else {
            //有链表，则按照链表传递事件
            TouchTarget target = mFirstTouchTarget;
            while (target != null) {
                TouchTarget next = target.next;
                if (target == newTouchTarget) {
                    handled = true;
                } else {
                    handled = dispatchTransformedTouchEvent(ev, target.child);
                }
                target = next;
            }
        }
        return handled;
    }

    private TouchTarget addTouchTarget(View child) {
        TouchTarget target = TouchTarget.obtain(child);
        target.next = mFirstTouchTarget;
        mFirstTouchTarget = target;
        return target;
    }

    /**
     * 分发事件到子控件控件
     *
     * @param ev
     * @param child
     * @return
     */
    private boolean dispatchTransformedTouchEvent(MotionEvent ev, View child) {
        boolean handled;
        if (child != null) {
            handled = child.dispatchTouchEvent(ev);
        } else {
            handled = super.dispatchTouchEvent(ev);
        }
        return handled;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    private static final class TouchTarget {
        //当前缓存的View
        private View child;
        //回收池    单链表的表头，第一个元素
        private static TouchTarget sRecycledBin;
        //size
        private static int sRecycledCount;
        private static final Object sRecycleLock = new Object[0];
        //next
        public TouchTarget next;

        public static TouchTarget obtain(View child) {
            TouchTarget target;
            synchronized (sRecycleLock) {
                if (sRecycledBin == null) {
                    target = new TouchTarget();
                } else {
                    target = sRecycledBin;
                }
                sRecycledBin = target.next;
                if(sRecycledCount > 0) {
                    sRecycledCount--;
                }
                target.next = null;
            }
            target.child = child;
            return target;
        }

        /**
         * 只有在cancel等系统处理中才会调用事件
         */
        public void recycle() {
            if (child == null) {
                throw new IllegalStateException("已经回收过了");
            }
            synchronized (sRecycleLock) {
                if (sRecycledCount < MAX_RECYCLED) {
                    next = sRecycledBin;
                    sRecycledBin = this;
                    sRecycledCount++;
                }
            }
        }
    }
}
