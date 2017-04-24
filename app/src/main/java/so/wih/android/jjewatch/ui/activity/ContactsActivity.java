package so.wih.android.jjewatch.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.realm.MyFriends;
import so.wih.android.jjewatch.realm.RealmHelper;
import so.wih.android.jjewatch.utils.CommonUtils;
import so.wih.android.jjewatch.utils.Constants;
import so.wih.android.jjewatch.utils.LogUtils;
import so.wih.android.jjewatch.utils.MyToast;
import so.wih.android.jjewatch.utils.ServerHelpter;


/**
 * 选择会话联系人界面
 * Created by HuWei on 2016/12/2.
 */

public class ContactsActivity extends BaseActivity implements AddContactsActivity.ReceiveTelephoneListener{

    @BindView(R.id.lv_contact)
    ListView lvContact;
    @BindView(R.id.tv_no_contacts)
    TextView tvNoContacts;
    private MyContactAdapter myContactAdapter;
    private SharedPreferences sp;
    private RealmResults<MyFriends> contactData;
    private Realm realm;
    private PopupWindow mPopupWindow;
    private int wuid;
    private List<MyFriends> simFriends;

    @Override
    public void init() {
        super.init();

        realm=Realm.getDefaultInstance();
        simFriends = CommonUtils.SimQuery(ContactsActivity.this);
        //查询SIM卡里的联系人
        sp = context.getSharedPreferences(Constants.JJE_CONFIG, MODE_PRIVATE);
        wuid = sp.getInt(Constants.WATCH_USER_ID, 0);
        //获取所有的联系人
        ServerHelpter.getAllWatchContacts(realm,wuid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.chat;
    }

    @Override
    public void initData() {

        contactData= RealmHelper.getAllMsg(realm);

        //没有联系人的处理
        showNoContactsText();

        contactData.addChangeListener(new RealmChangeListener<RealmResults<MyFriends>>() {
            @Override
            public void onChange(RealmResults<MyFriends> element) {
                myContactAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 没有联系人的处理: 显示没有联系人的文本
     */
    private void showNoContactsText() {
        if((contactData.size()+simFriends.size()) == 0){
            tvNoContacts.setVisibility(View.VISIBLE);
        }else {
            tvNoContacts.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 删除联系人
     * @param name
     * @param phone
     */
    public void deleteContact(String name, String phone) {
        String where = "tag='" + name + "'";

        where += " AND number='" + phone + "'";
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ContactsActivity.this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    Constants.MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }else{
            int delete = getContentResolver().delete(Constants.uri_contact, where, null);
            if(delete == 1){
                LogUtils.logdHu("删除联系人成功！");
                myContactAdapter.notifyDataSetChanged();
            }

        }



    }
    @Override
    public void initListener() {

        //声波添加好友
        AddContactsActivity addContactsActivity = new AddContactsActivity();
        addContactsActivity.setReceiveTelephoneListener(this);
        //添加头布局。不能放在下面Cannot add header view to list -- setAdapter has already been called.
        View view = getLayoutInflater().inflate(R.layout.contact_header, null);
        lvContact.addHeaderView(view);

        myContactAdapter = new MyContactAdapter();
        lvContact.setAdapter(myContactAdapter);
        //条目点击监听
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismissPopWindow();
                //判断点击的是那个条目
                if (position == 0) {
                    Intent intent = new Intent(ContactsActivity.this, AddContactsActivity.class);
                    startActivity(intent);
                } else if(position <= contactData.size()){

                    //需要将会话对象的手机号传递给聊天页面
                    final MyFriends contactInfor = contactData.get(position - 1);
                    Intent intent = new Intent(ContactsActivity.this,ContactDescActivity.class);
                    intent.putExtra("name", contactInfor.getName());
                    intent.putExtra("phone", contactInfor.getPhone());
                    startActivity(intent);

                }else{
                    MyFriends friends = simFriends.get(position - contactData.size() -1);
                    Intent intent = new Intent(ContactsActivity.this,ContactDescActivity.class);
                    intent.putExtra("name", friends.getName());
                    intent.putExtra("phone", friends.getPhone());
                    startActivity(intent);
                }
            }
        });
        //长按删除联系人
        lvContact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dismissPopWindow();
                if(position>0){
                    showPopupWindow(parent,view,position);
                }
                //返回true即消费了长按事件
                return true;
            }
        });

    }

    private void showPopupWindow(AdapterView<?> parent,View view,final int position) {
        //长按弹出popUpWindow
        mPopupWindow = new PopupWindow(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.popwindow, null);
        Button btn_delete_friend = (Button) inflate.findViewById(R.id.btn_delete_friend);
        Button btn_cancel = (Button) inflate.findViewById(R.id.btn_cancel);
        btn_delete_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(position <= contactData.size()){
                        MyFriends friends = contactData.get(position-1);
                        String phone = friends.getPhone();
                        int cid = friends.getId();
                        //删除服务器里的联系人 ,有先后顺序 cid
                        ServerHelpter.watchDeleteFriend(context,wuid,cid);
                        //删除数据库里的联系人
                        RealmHelper.realmDeleteByPhone(realm,phone);
                        showNoContactsText();
                        mPopupWindow.dismiss();
                    }else{
                        // 删除系统联系人
                        MyFriends friends = simFriends.get(position - contactData.size() - 1);
                        String name = friends.getName();
                        String phone = friends.getPhone();
                        deleteContact(name,phone);
//                        simFriends.remove(position - contactData.size() - 1);
                        simFriends.remove(friends);
                        showNoContactsText();
                        myContactAdapter.notifyDataSetChanged();
                        mPopupWindow.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        int[] location = new int[2];
        // 获得view 在窗体中的位置,参数是 长度为2的整数数组，方法执行完后，数组中下标0是X坐标，下标1是Y坐标
        view.getLocationInWindow(location );

        int top = location[1];
        mPopupWindow.setContentView(inflate);
        mPopupWindow.setWidth(250);
        mPopupWindow.setHeight(70);
        //显示PopupWindow
        mPopupWindow.showAtLocation(parent, Gravity.RIGHT+Gravity.TOP, 0, top);
    }

    /**
     * 隐藏弹出窗体
     */
    private void dismissPopWindow() {
        if(mPopupWindow!=null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*//连接服务器查询联系人
        ServerHelpter.getAllWatchContacts(realm,wuid);*/
    }

    @Override
    public void receiveTelephone(String oid) {
        //防止格式化错误
        try {
            int friendWuid = Integer.valueOf(oid);
            //根据oid查询数据库
            MyFriends friend = RealmHelper.realmQueryOid(realm, friendWuid);
            if(friend == null){ //没有查到
                //添加好友到服务器
                ServerHelpter.watchAddFriend(context,realm, wuid , friendWuid);
            }else{
                MyToast.showToast(context,"已经是好友了！");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class MyContactAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contactData.size()+simFriends.size();
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
            ViewHolder vh;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.contact_list, null);
                vh = new ViewHolder();
                vh.tv_contact_name = (TextView) convertView.findViewById(R.id.tv_name);
                vh.iv_icon = (ImageView)convertView.findViewById(R.id.iv_icon);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            showNoContactsText();
            if(contactData.size()>0&&position < contactData.size()){
                vh.iv_icon.setVisibility(View.VISIBLE);
            }else if(position >= contactData.size()&&simFriends.size()>0){
                vh.iv_icon.setVisibility(View.INVISIBLE);
            }else{
                vh.iv_icon.setVisibility(View.INVISIBLE);
            }
            if(position<contactData.size()){
                //为控件赋值
                MyFriends contactInfor = contactData.get(position);
                vh.tv_contact_name.setText(contactInfor.getName());
            }else{
                MyFriends friends = simFriends.get(position - contactData.size());
                vh.tv_contact_name.setText(friends.getName());
            }

            return convertView;
        }
    }

    private class ViewHolder {
        public TextView tv_contact_name;
        public ImageView iv_icon;
    }

    @Override
    protected void onDestroy() {
        contactData.removeChangeListeners();
        realm.close();
        super.onDestroy();
    }

}
