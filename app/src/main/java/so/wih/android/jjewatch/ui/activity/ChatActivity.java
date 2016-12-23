package so.wih.android.jjewatch.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.AudioPlayManager;
import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.IAudioPlayListener;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import so.wih.android.jjewatch.AppContexts;
import so.wih.android.jjewatch.MyApplication;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.utils.CommonUtils;
import so.wih.android.jjewatch.utils.Constants;
import so.wih.android.jjewatch.utils.MyToast;

/**
 * Created by HuWei on 2016/11/25.
 * 会话界面，需要会话对象的手机号
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener,
        View.OnTouchListener, RongIM.OnSendMessageListener, AppContexts.OnReceiveMessageListener {

    @BindView(R.id.tv_contact_name)
    TextView tvContactName;
    @BindView(R.id.lv_msg)
    ListView lvMsg;
    @BindView(R.id.btn_call)
    Button btnCall;
    @BindView(R.id.btn_location)
    Button btn_location;
    @BindView(R.id.btn_mic)
    Button btn_mic;


    private String userId = "18317890128";
    private List<Message> data;

    private AudioRecordManager mAudioRecordManager;
    private MyAdapter adapter;
    private ImageView iv_SendView;
    private AudioPlayManager mAudioPlayManager;
    private ImageView iv_ReceiveView;


    @Override
    public void init() {
        super.init();
        //录音机
        mAudioRecordManager = AudioRecordManager.getInstance();
        //录音播放
        mAudioPlayManager = AudioPlayManager.getInstance();


    }

    @Override
    public void initLayout() {
        super.initLayout();
        setContentView(R.layout.chat_activity);
    }

    @Override
    public void initView() {
        super.initView();
        //TODO 获取电话号码
        Intent intent = getIntent();
        //设置聊天对象的姓名
        tvContactName.setText(userId);
        //接收消息回调
        AppContexts appContexts = new AppContexts(MyApplication.getCtx());
        appContexts.setOnReceiveMessageListener(ChatActivity.this);

        if (RongIM.getInstance() != null) {
            //设置自己发出的消息监听器。
            RongIM.getInstance().setSendMessageListener(this);
        }

        //装消息的集合
        data = new ArrayList<>();
        adapter = new MyAdapter(data);

        lvMsg.setAdapter(adapter);

        lvMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = data.get(position);
                Message.MessageDirection messageDirection = message.getMessageDirection();
                MessageContent content = message.getContent();

                if (content instanceof VoiceMessage) { //语音消息
                    VoiceMessage voiceMessage = (VoiceMessage) content;
                    if (messageDirection == Message.MessageDirection.SEND) {

                        if (iv_SendView != null) {
                            iv_SendView.setImageResource(R.drawable.rc_ic_voice_sent_play3);
                            iv_SendView = null;
                        }
                        iv_SendView = (ImageView) view.findViewById(R.id.iv_send_voice);
                        iv_SendView.setImageResource(R.drawable.rc_an_voice_sent);
                        playVoice(voiceMessage, iv_SendView, R.drawable.rc_ic_voice_sent_play3);
                    }
                    if (messageDirection == Message.MessageDirection.RECEIVE) {
                        if (iv_ReceiveView != null) {
                            iv_ReceiveView.setImageResource(R.drawable.rc_ic_voice_receive_play3);
                            iv_ReceiveView = null;
                        }
                        iv_ReceiveView = (ImageView) view.findViewById(R.id.iv_receive_voice);
                        iv_ReceiveView.setImageResource(R.drawable.rc_an_voice_receive);
                        playVoice(voiceMessage, iv_ReceiveView, R.drawable.rc_ic_voice_receive_play3);
                    }
                } else if (content instanceof LocationMessage) {   //点击在地图界面显示
                    LocationMessage locationMessage = (LocationMessage) content;
                    Intent intent = new Intent(ChatActivity.this, AMAPLocationActivity.class);
                    intent.putExtra("location", locationMessage);
                    startActivity(intent);
                }
            }
        });

        btnCall.setOnClickListener(this);
        btn_mic.setOnTouchListener(this);
        btn_location.setOnClickListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.btn_mic) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    btn_mic.setText("松开结束");
                    //按下时录音
                    //startRecorder();
                    mAudioRecordManager.startRecord(btn_mic,
                            Conversation.ConversationType.PRIVATE, userId);
                    MyToast.showToast(context,"发往 ："+userId);
                    break;
                case MotionEvent.ACTION_UP:
                    btn_mic.setText("按住说话");

                    //松开停止录音
                    mAudioRecordManager.stopRecord();
                    break;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_call:
                MyToast.showToast(context, "正在拨通电话...");
                // 打电话
                getCallConnection();
                break;
            case R.id.btn_location:
                MyToast.showToast(context, "正在发送位置...");
                // 发送位置
                sendLocation();
                break;
        }
    }

    /**
     * 发送位置
     */
    private void sendLocation() {
        AppContexts.getInstance().onStartLocation(MyApplication.getCtx(), new RongIM.LocationProvider.LocationCallback() {
            @Override
            public void onSuccess(LocationMessage locationMessage) {
                Message message = Message.obtain(userId, Conversation.ConversationType.PRIVATE, locationMessage);
                RongIM.getInstance().sendLocationMessage(message, (String) null, (String) null, (IRongCallback.ISendMessageCallback) null);
            }

            @Override
            public void onFailure(String s) {

            }
        });

    }

    /**
     * 打电话
     */
    private void getCallConnection() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + userId);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        ChatActivity.this.startActivity(intent);
    }

    /**
     * 播放语音
     *
     * @param voiceMessage
     * @param iv
     * @param resId
     */
    private void playVoice(VoiceMessage voiceMessage, final ImageView iv, final int resId) {
        //播放语音
        mAudioPlayManager.startPlay(ChatActivity.this, voiceMessage.getUri(), new IAudioPlayListener() {
            @Override
            public void onStart(Uri uri) {
                Log.d(Constants.TAG, "onStart: " + uri.toString());
            }

            @Override
            public void onStop(Uri uri) {
            }

            @Override
            public void onComplete(Uri uri) {
                //播放完成显示的图标
                iv.setImageResource(resId);
            }
        });
    }

    /**
     * 消息发送前监听器处理接口（是否发送成功可以从 SentStatus 属性获取）。
     *
     * @param message 发送的消息实例。
     * @return 处理后的消息实例。
     */
    @Override
    public Message onSend(Message message) {

        return message;
    }

    /**
     * 消息在 UI 展示后执行/自己的消息发出后执行,无论成功或失败。
     *
     * @param message              消息实例。
     * @param sentMessageErrorCode 发送消息失败的状态码，消息发送成功 SentMessageErrorCode 为 null。
     * @return true 表示走自已的处理方式，false 走融云默认处理方式。
     */
    @Override
    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        Message.SentStatus sentStatus = message.getSentStatus();

        if(sentStatus==null){
            Log.d(Constants.TAG, "onSent: null");
        }else {
            int value = sentStatus.getValue();
            Log.d(Constants.TAG, "onSent: 发送状态:"+value );
        }

        if (message.getSentStatus() == Message.SentStatus.FAILED) {
            if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_CHATROOM) {
                //不在聊天室
            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_DISCUSSION) {
                //不在讨论组
            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_GROUP) {
                //不在群组
            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.REJECTED_BY_BLACKLIST) {
                //你在他的黑名单中
            }
        }

        data.add(message);
        //数据发生改变，刷新数据
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void receiveMessage(Message message) {
        Log.d(Constants.TAG, "chatActivity 方法 receiveMessage: 我执行了接收");
        data.add(message);
        //数据发生改变，刷新数据
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }


    public class MyAdapter extends BaseAdapter {
        private final List<Message> data;

        public MyAdapter(List<Message> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
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
            View view;
            ViewHolder vh;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.chat_show_desc, null);
                vh = new ViewHolder();
                // 聊天的时间
                vh.tv_chat_time = (TextView) view.findViewById(R.id.tv_chat_time);
                //接收的语音总管
                vh.ll_receive_voice = (LinearLayout) view.findViewById(R.id.ll_receive_voice);
                vh.iv_receive_voice = (ImageView) view.findViewById(R.id.iv_receive_voice);
                vh.tv_receive_second = (TextView) view.findViewById(R.id.tv_receive_second);
                //接收的文字
                vh.tv_receive_desc_left = (TextView) view.findViewById(R.id.tv_receive_desc_left);
                //发送的地图消息
                vh.ll_receive_location = (FrameLayout) view.findViewById(R.id.ll_receive_location);
                vh.aiv_receive_img = (AsyncImageView) view.findViewById(R.id.aiv_receive_img);
                vh.tv_receive_content = (TextView) view.findViewById(R.id.tv_receive_content);

                //发送语音和语音时间
                vh.ll_send_voice = (LinearLayout) view.findViewById(R.id.ll_send_voice);
                //文字
                vh.tv_send_desc_right = (TextView) view.findViewById(R.id.tv_send_desc_right);
                //语音
                vh.iv_send_voice = (ImageView) view.findViewById(R.id.iv_send_voice);
                //语音时间
                vh.tv_send_second = (TextView) view.findViewById(R.id.tv_send_second);
                //发送的地图消息
                vh.ll_send_location = (FrameLayout) view.findViewById(R.id.ll_send_location);
                vh.aiv_img = (AsyncImageView) view.findViewById(R.id.aiv_img);
                vh.tv_location_content = (TextView) view.findViewById(R.id.tv_location_content);

                // 背在view 的身上
                view.setTag(vh);

            } else {
                view = convertView;
                vh = (ViewHolder) view.getTag();
            }
            Message currentMessage = data.get(position);

            //获取发送的时间
            long currentSentTime = currentMessage.getSentTime();

            String str = CommonUtils.FormatTime(currentSentTime);
            vh.tv_chat_time.setText(str);

            MessageContent content = currentMessage.getContent();

            Message.MessageDirection messageDirection = currentMessage.getMessageDirection();

            if (messageDirection == Message.MessageDirection.SEND) {  // 代表发送消息

                vh.ll_receive_voice.setVisibility(View.GONE);
                vh.tv_receive_desc_left.setVisibility(View.GONE);
                vh.ll_receive_location.setVisibility(View.GONE);
                vh.ll_send_voice.setVisibility(View.VISIBLE);
                vh.tv_send_desc_right.setVisibility(View.VISIBLE);
                vh.ll_send_location.setVisibility(View.VISIBLE);
                //发送消息的处理
                setSendMessage(vh, content);
            } else if (messageDirection == Message.MessageDirection.RECEIVE) {  // 代表接受的消息
                vh.ll_receive_voice.setVisibility(View.VISIBLE);
                vh.tv_receive_desc_left.setVisibility(View.VISIBLE);
                vh.ll_receive_location.setVisibility(View.VISIBLE);
                vh.ll_send_voice.setVisibility(View.GONE);
                vh.tv_send_desc_right.setVisibility(View.GONE);
                vh.ll_send_location.setVisibility(View.GONE);
                //接收消息的处理
                setReceiveMessage(vh, content);
            }
            return view;
        }

    }


    /**
     * 为控件赋接收到的消息的值
     *
     * @param vh
     * @param content
     */
    private void setSendMessage(ViewHolder vh, MessageContent content) {
        //发送的处理
        if (content instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) content;
            //语音
            vh.ll_send_voice.setVisibility(View.GONE);
            //地图位置
            vh.ll_send_location.setVisibility(View.GONE);

            vh.tv_send_desc_right.setText(textMessage.getContent());

//                Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
        } else if (content instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) content;
            //语音
            vh.ll_send_voice.setVisibility(View.GONE);
            //地图位置
            vh.ll_send_location.setVisibility(View.GONE);
            //文字
            vh.tv_send_desc_right.setVisibility(View.GONE);
//                Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (content instanceof VoiceMessage) {//语音消息
            //地图位置
            vh.ll_send_location.setVisibility(View.GONE);
            //文字
            vh.tv_send_desc_right.setVisibility(View.GONE);

            VoiceMessage voiceMessage = (VoiceMessage) content;
//                Log.e(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
            //语音时间
            int duration = voiceMessage.getDuration();
            vh.tv_send_second.setText(" " + duration + "\"  ");

        } else if (content instanceof RichContentMessage) {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) content;
            //语音
            vh.ll_send_voice.setVisibility(View.GONE);
            //地图位置
            vh.ll_send_location.setVisibility(View.GONE);
            //文字
            vh.tv_send_desc_right.setVisibility(View.GONE);
            Log.d(Constants.TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
        } else if (content instanceof LocationMessage) {  //地理位置消息
            //语音
            vh.ll_send_voice.setVisibility(View.GONE);
            //文字
            vh.tv_send_desc_right.setVisibility(View.GONE);

            LocationMessage locationMessage = (LocationMessage) content;
            Uri imgUri = locationMessage.getImgUri();
            String poi = locationMessage.getPoi();
            vh.aiv_img.setResource(imgUri);
            vh.tv_location_content.setText(poi);
        } else {
//                Log.d(TAG, "onSent-其他消息，自己来判断处理");
            //语音
            vh.ll_send_voice.setVisibility(View.GONE);
            //地图位置
            vh.ll_send_location.setVisibility(View.GONE);
            //文字
            vh.tv_send_desc_right.setVisibility(View.GONE);
        }
    }

    /**
     * 为控件赋接收到的消息的值
     *
     * @param vh
     * @param content
     */

    private void setReceiveMessage(ViewHolder vh, MessageContent content) {
        //接收的消息处理
        if (content instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) content;
            //语音
            vh.ll_receive_voice.setVisibility(View.GONE);
            //地图位置
            vh.ll_receive_location.setVisibility(View.GONE);
            //文字
            vh.tv_receive_desc_left.setText(textMessage.getContent());
//                Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
        } else if (content instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) content;
            //语音
            vh.ll_receive_voice.setVisibility(View.GONE);
            //地图位置
            vh.ll_receive_location.setVisibility(View.GONE);
            //文字
            vh.tv_receive_desc_left.setVisibility(View.GONE);
//                Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (content instanceof VoiceMessage) {//语音消息
            //文字
            vh.tv_receive_desc_left.setVisibility(View.GONE);
            //地图位置
            vh.ll_receive_location.setVisibility(View.GONE);
            //语音
            VoiceMessage voiceMessage = (VoiceMessage) content;
//                Log.e(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
            //语音时间
            int duration = voiceMessage.getDuration();
            vh.tv_receive_second.setText(" " + duration + "\"  ");

        } else if (content instanceof RichContentMessage) {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) content;
            //语音
            vh.ll_receive_voice.setVisibility(View.GONE);
            //地图位置
            vh.ll_receive_location.setVisibility(View.GONE);
            //文字
            vh.tv_receive_desc_left.setVisibility(View.GONE);
            Log.d(Constants.TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
        } else if (content instanceof LocationMessage) {  //地理位置消息
            //语音
            vh.ll_receive_voice.setVisibility(View.GONE);
            //文字
            vh.tv_receive_desc_left.setVisibility(View.GONE);

            LocationMessage locationMessage = (LocationMessage) content;
            Uri imgUri = locationMessage.getImgUri();
            String poi = locationMessage.getPoi();
            vh.aiv_receive_img.setResource(imgUri);
            vh.tv_receive_content.setText(poi);
        } else {
//                Log.d(TAG, "onSent-其他消息，自己来判断处理");
            //语音
            vh.ll_receive_voice.setVisibility(View.GONE);
            //地图位置
            vh.ll_receive_location.setVisibility(View.GONE);
            //文字
            vh.tv_receive_desc_left.setVisibility(View.GONE);
        }
    }

    private class ViewHolder {
        //聊天时间
        public TextView tv_chat_time;


        public LinearLayout ll_send_voice;
        public TextView tv_send_desc_right;
        public TextView tv_send_second;
        public ImageView iv_send_voice;
        public FrameLayout ll_send_location;
        public AsyncImageView aiv_img;
        public TextView tv_location_content;


        public LinearLayout ll_receive_voice;
        public TextView tv_receive_desc_left;
        public ImageView iv_receive_voice;
        public TextView tv_receive_second;
        public FrameLayout ll_receive_location;
        public AsyncImageView aiv_receive_img;
        public TextView tv_receive_content;

    }
}
