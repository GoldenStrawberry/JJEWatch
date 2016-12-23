package so.wih.android.jjewatch.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.ui.activity.HealthManagerActivity;

/**
 * 健康管理
 * Created by Administrator on 2016/11/24.
 */

public class HealthManagerFragment extends Fragment {

    private View healthView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        healthView = inflater.inflate(R.layout.health_manager_fragment, null);
        //初始化控件
        initView();
        return healthView;
    }

    private void initView() {
        ImageView iv_health_manager = (ImageView) healthView.findViewById(R.id.iv_health_manager);
        iv_health_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HealthManagerActivity.class);
                startActivity(intent);
            }
        });
    }
}
