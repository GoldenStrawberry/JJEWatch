package so.wih.android.jjewatch.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.ui.activity.ContactActivity;

/**
 * 联系人主界面
 * Created by Administrator on 2016/11/22.
 */

public class ContactFragment extends Fragment {

    private View view;
    private ImageView contactImageView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contact_fragment, null);
        //初始化控件
        initView();
        return view;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        contactImageView = (ImageView) view.findViewById(R.id.contact_image);
        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ContactActivity.class);
                startActivity(intent);
            }
        });
    }
}
