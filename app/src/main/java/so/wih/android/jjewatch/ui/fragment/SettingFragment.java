package so.wih.android.jjewatch.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.ui.activity.SettingActivity;

/**
 *

 * 设置页面
 * Created by Administrator on 2016/11/22.
 */

public class SettingFragment extends Fragment {

    private View setView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setView = inflater.inflate(R.layout.setting_fragment, null);
        initView();
        return setView;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ImageView iv_setting = (ImageView) setView.findViewById(R.id.iv_setting);
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
