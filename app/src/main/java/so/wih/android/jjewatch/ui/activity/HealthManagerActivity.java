package so.wih.android.jjewatch.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.TextView;

import butterknife.BindView;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.service.StepService;
import so.wih.android.jjewatch.utils.Constants;

/**
 * Created by HuWei on 2016/11/24.
 */

public class HealthManagerActivity extends BaseActivity implements Handler.Callback {

    @BindView(R.id.tv_steps)
    TextView tvSteps;

    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL = 500;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constants.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case  Constants.MSG_FROM_SERVER:
                // 更新界面上的步数
                tvSteps.setText( msg.getData().getInt("step") +" 步");
                delayHandler.sendEmptyMessageDelayed( Constants.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case  Constants.REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, Constants.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
        }
        return false;
    }
    @Override
    public void initLayout() {
        super.initLayout();
        setContentView(R.layout.health_manager_activity);
    }

    @Override
    public void initView() {
        super.initView();
        delayHandler = new Handler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupService();
    }

    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

}
