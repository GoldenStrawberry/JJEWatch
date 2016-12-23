package so.wih.android.jjewatch.ui.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.bean.ContactInfor;
import so.wih.android.jjewatch.bean.TokenBean;


/**
 *  选择会话联系人界面
 * Created by Administrator on 2016/11/29.
 *
 */

public class MyChat extends AppCompatActivity {

    private Serializable tokenbean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        Button btn = (Button) findViewById(R.id.btn_chat);
        Intent intent = getIntent();
        tokenbean = intent.getSerializableExtra("tokenbean");
        TokenBean tokenbean2 = (TokenBean) this.tokenbean;
        btn.setText("这是登录人的手机号:"+tokenbean2.getUserId());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyChat.this,ChatActivity.class);
                //需要将会话对象的手机号传递给聊天页面
                startActivity(intent);
            }
        });
    }

    /**
     * 查询数据库获取联系人数据
     */
    private void queryContacts() {
        //TODO 反应有点慢
        ContentResolver resolver = this.getContentResolver();
        //联系人Cursor
        Cursor personCur = resolver.query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (personCur.moveToNext()) {
            //创建联系人对象
            ContactInfor cInfor = new ContactInfor();
            //获取联系人的名字
            String contact_name = personCur.getString(
                    personCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            //获取联系人的id
            int id = personCur.getInt(personCur.getColumnIndex(ContactsContract.Contacts._ID));
            //根据id查询联系人电话
            Cursor phoneCur = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
            //联系人的电话
            String contact_phone = "";
            while (phoneCur.moveToNext()) {
                contact_phone = phoneCur.getString(
                        phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }

            if (contact_phone != "") {     //去除电话为""的联系人
                cInfor.contact_name = contact_name;
                cInfor.contact_phone = contact_phone;
            }
        }
    }
}
