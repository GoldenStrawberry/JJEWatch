package so.wih.android.jjewatch.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.ui.fragment.AddContactFragment;
import so.wih.android.jjewatch.ui.fragment.ContactFragment;
import so.wih.android.jjewatch.ui.fragment.HealthManagerFragment;
import so.wih.android.jjewatch.ui.fragment.MainFragment;
import so.wih.android.jjewatch.ui.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private List<Fragment> fragments ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //使用ViewPager填充
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        initdata();
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }



    /**
     * 加载数据
     */
    private void initdata() {
        fragments = new ArrayList<>();
        MainFragment mainFragment = new MainFragment();
        ContactFragment contactFragment = new ContactFragment();
        AddContactFragment addContactFragment = new AddContactFragment();
        HealthManagerFragment healthManagerFragment = new HealthManagerFragment();
        SettingFragment settingFragment = new SettingFragment();

        fragments.add(mainFragment);
        fragments.add(contactFragment);
        fragments.add(addContactFragment);
        fragments.add(healthManagerFragment);
        fragments.add(settingFragment);

    }

    /**
     *  viewpager的适配器
     */
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
