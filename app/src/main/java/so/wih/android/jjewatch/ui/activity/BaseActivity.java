package so.wih.android.jjewatch.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * ==============================================
 * Created by HuWei on 2016/12/13.
 * Activity的基类
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class BaseActivity extends AppCompatActivity {
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
        initLayout();
        //绑定ButterKnife
        ButterKnife.bind(this);
        initView();
    }



    /**
     * 初始化操作
     * 在加载布局前需要干的事情
     */
    public void init(){}

    /**
     * 初始化布局
     */
    public void initLayout() {
    }

    /**
     * 初始化所有的控件
     */
    public void initView(){}

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
