package so.wih.android.jjewatch.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * ==============================================
 * Created by HuWei on 2016/12/13.
 * Activity的基类
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public abstract class BaseActivity extends SwipeBackActivity {
    // 共享资源
    public static List<BaseActivity> activities = new ArrayList<>();
    public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加到集合中统一维护
        synchronized (activities) {
            activities.add(this);
        }
        context = this;

        init();
        setContentView(getLayoutResId());

        //获取屏幕的宽度
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;

        /*SwipeBackLayout mSwipeBackLayout  = getSwipeBackLayout();
        //设置可以滑动的区域，推荐用屏幕像素的1/3来指定
        mSwipeBackLayout.setEdgeSize(widthPixels/4);
        //设定滑动关闭的方向，SwipeBackLayout.EDGE_ALL表示向下、左、右滑动均可。EDGE_LEFT，EDGE_RIGHT，EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);*/

        //绑定ButterKnife
        ButterKnife.bind(this);
        initData();
        initListener();

    }



    /**
     * 初始化操作
     * 在加载布局前需要干的事情
     */
    public void init(){}
    /**
     * 返回当前Activity的布局id
     *
     */
    public abstract int getLayoutResId();
    /**
     * 为控件赋值
     * 加载数据等
     */
    public abstract void initData();
    /**
     * 初始化监听
     * 子类所有的监听设置,必须在initListener方法中
     */
    public abstract void initListener();

    /**
     * 初始化toolbar
     */
    private void initToolBar() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        synchronized (activities) {
            activities.remove(this);
        }
    }
    public void killAll() {
        //遍历中不允许增删
        //1. 复制一份
        //2. CopyOnWriteArrayList 可以在遍历中做增删操作
        List<BaseActivity> copy;
        synchronized (activities) {
            copy = new ArrayList<>(activities);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }

        //  自杀进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
