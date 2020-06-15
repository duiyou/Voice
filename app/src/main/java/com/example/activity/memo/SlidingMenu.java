package com.example.activity.memo;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.RelativeLayout;

import com.example.application.MyApplication;
import com.example.voice.R;

import java.lang.reflect.Field;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义侧滑菜单
 * 参考：https://blog.csdn.net/yhaolpz/article/details/77366154
 * 修改默认动画时长参考：https://cyxcw1.github.io/2015/06/27/android-horizontalscrollview-e8-87-aa-e5-ae-9a-e4-b9-89-e6-bb-91-e5-8a-a8-e5-8a-a8-e7-94-bb-e6-97-b6-e9-95-bf/
 *
 * 问题：
 *      1、布局点击时设置背景出现问题，因为只有UP和DOWN，当长按时拉出item范围不触发UP
 *
 */
public class SlidingMenu extends HorizontalScrollView {
    private static final String TAG = "SlidingMenu";

    //监听接口
    public interface SlidingMenuUtilListener{
        //打开item时调用，即打开UpdateMemoActivity
        void openItem();
    }
    //回调监听
    private SlidingMenuUtilListener mListener;
    public void setSlidingMenuUtilListener(SlidingMenuUtilListener mListener){
        this.mListener = mListener;
    }


    private static final float radio = 0.2f;//菜单占屏幕宽度比
    private int mScreenWidth;
    private int mMenuWidth;
    private boolean once = true;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        //获取屏幕宽度
        WindowManager wm = (WindowManager) MyApplication.context.getSystemService(Context.WINDOW_SERVICE);
        Point outSize = new Point();
        wm.getDefaultDisplay().getRealSize(outSize);
        mScreenWidth = outSize.x;

        //计算出菜单宽度
        mMenuWidth = (int) (mScreenWidth * radio);

        //初始化修改滚动时长的对象
        init();

        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setHorizontalScrollBarEnabled(false);
    }


    /**
     * 显示的设置一个宽度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //不知道为啥一开始就会打开到最后边并且调用了onTouchEvent的MotionEvent.ACTION_UP，所以isOpen一开始就是true
        //初始化时就在左边
        this.smoothScrollTo(0, 0);
        isOpen = false;
        if (once) {
            //初始化布局控件
            initLayout();
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            wrapper.getChildAt(0).getLayoutParams().width = mScreenWidth;
            wrapper.getChildAt(1).getLayoutParams().width = mMenuWidth;

            once = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int downScrollX;

    /**
     * 触摸时监听
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            //触摸时，就去关闭之前记住的item
            case MotionEvent.ACTION_DOWN:
                //触摸时记下x
                downScrollX = getScrollX();
                //设置背景为按下状态
                //listLayout.setBackground(MyApplication.context.getResources().getDrawable(R.drawable.memo_activity_list_touchbackgroundpress));
                closeOpenMenu();
                break;
            // Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
            case MotionEvent.ACTION_UP:
                //设置背景为默认状态
               // listLayout.setBackground(MyApplication.context.getResources().getDrawable(R.drawable.memo_activity_list_touchbackgrounddefault));
                int scrollX = getScrollX();

                //如果触摸时和放开时没动过则打开这个item
                if (downScrollX == 0 && scrollX == 0) {
                    if (mListener != null) {
                        mListener.openItem();
                    }
                    return false;
                }

                //移动过则判断打开菜单还是拉回去
                if (Math.abs(scrollX) > mMenuWidth / 2) {
                    //运行动画，时长duration为0
                    myScroller.startScroll(scrollX, getScrollY(), mMenuWidth, 0, 0);
                    invalidate();
                    //this.smoothScrollTo(mMenuWidth, 0);
                    //划过去了，就让适配器记住这个item
                    setLastMenu();
                } else {
                    this.smoothScrollTo(0, 0);
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 设置滑动速度
     * @param velocityX
     */
    @Override
    public void fling(int velocityX) {
        super.fling(velocityX * 100);
    }

    private OverScroller myScroller;

    private void init()
    {
        try
        {
            Class parent = this.getClass();
            do
            {
                parent = parent.getSuperclass();
            } while (!parent.getName().equals("android.widget.HorizontalScrollView"));

            Log.i("Scroller", "class: " + parent.getName());
            Field field = parent.getDeclaredField("mScroller");
            field.setAccessible(true);
            myScroller = (OverScroller) field.get(this);

        } catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }


    //有框框的那个布局控件
    private RelativeLayout listLayout;
    /**
     * 得到那个框框的布局
     */
    private void initLayout() {
        listLayout = (RelativeLayout)findViewById(R.id.memo_activity_list_layoutleft);
    }


    //是否打开了这个菜单
    public boolean isOpen = false;

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        this.smoothScrollTo(0, 0);
        isOpen = false;
    }

    /**
     * 菜单是否打开
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 当打开菜单时记录此 view ,方便下次关闭
     */
    private void setLastMenu() {
        View view = this;
        while (true) {
            view = (View) view.getParent();
            if (view instanceof RecyclerView) {
                break;
            }
        }
        Log.i(TAG, "setLastMenu: 设置最后一个打开的菜单");
        //传过去适配器，这个item是最后一个划过的item
        ((MessageAdapter) ((RecyclerView) view).getAdapter()).setLastMenu(this);
        isOpen = true;
    }

    /**
     * 当触摸此 item 时，关闭上一次打开的 item
     */
    private void closeOpenMenu() {
        //触摸时就会触发，所以只有没被open才去open这个item并且关闭之前的item
        if (!isOpen) {
            View view = this;
            while (true) {
                view = (View) view.getParent();
                if (view instanceof RecyclerView) {
                    break;
                }
            }
            ((MessageAdapter) ((RecyclerView) view).getAdapter()).closeLastMenu();
        }
    }


}

