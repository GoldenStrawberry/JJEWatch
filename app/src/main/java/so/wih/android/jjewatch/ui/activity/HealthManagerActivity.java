package so.wih.android.jjewatch.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.utils.MyToast;

/**
 * 健康管理界面
 * Created by HuWei on 2016/11/24.
 */

public class HealthManagerActivity extends BaseActivity{

    @BindView(R.id.lv_health)
    ListView lvHealth;

    public String[] healthItem ={"运动","心率","血压","血糖"};
    public int[] resId = {R.drawable.run, R.drawable.heart_rate,
            R.drawable.blood_pressure,R.drawable.blood_glucose};


    @Override
    public int getLayoutResId() {
        return R.layout.health_manager_activity;
    }

    @Override
    public void initData() {
        lvHealth.setAdapter(new HealthAdapter());
    }

    @Override
    public void initListener() {
        lvHealth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position < 4){
                    Intent intent = new Intent(HealthManagerActivity.this,HealthDescActivity.class);
                    intent.putExtra("position",position);
                    startActivity(intent);

                }else{
                    MyToast.showToast(context,"该功能尚在开发！");
                }

            }
        });
    }

    public class HealthAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return healthItem.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.health_list_item, null);
            TextView tv_health_desc = (TextView) view.findViewById(R.id.tv_health_desc);
            Drawable drawable= getResources().getDrawable(resId[position]);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth()*2, drawable.getMinimumHeight()*2);
            tv_health_desc.setCompoundDrawables(drawable,null,null,null);
            tv_health_desc.setText(healthItem[position]);
            return view;
        }
    }

}
