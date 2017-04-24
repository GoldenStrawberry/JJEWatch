package so.wih.android.jjewatch.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libra.sinvoice.Common;
import com.libra.sinvoice.LogHelper;
import com.libra.sinvoice.SinVoicePlayer;
import com.libra.sinvoice.SinVoiceRecognition;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import so.wih.android.jjewatch.MyApplication;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.utils.Constants;
import so.wih.android.jjewatch.utils.NotificationUtils;

/**
 * ==============================================
 * Created by HuWei on 2016/12/23.
 * 声波添加好友界面
 *
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class AddContactsActivity extends BaseActivity implements
        SinVoiceRecognition.Listener, SinVoicePlayer.Listener{
    private final static String TAG = "MainActivityxx";

    private final static int MSG_SET_RECG_TEXT = 1;
    private final static int MSG_RECG_START = 2;
    private final static int MSG_RECG_END = 3;
    private final static int MSG_PLAY_TEXT = 4;

    //    private final static int[] TOKENS = null;
//    private final static String TOKENS_str = null;
//    private final static int TOKEN_LEN = 10;
    private final static int[] TOKENS = {32, 32, 32, 32, 32, 32 };
    private final static String TOKENS_str = "Beeba20141";
    private final static int TOKEN_LEN = TOKENS.length;

    private final static String BAKCUP_LOG_PATH = "/sinvoice_backup";
    private static Activity act ;

    private Handler mHanlder;
    private SinVoicePlayer mSinVoicePlayer;
    private static SinVoiceRecognition mRecognition;
    private static boolean mIsReadFromFile;
    private String mSdcardPath;
    private PowerManager.WakeLock mWakeLock;
    private TextView mPlayTextView;
//    private TextView mRecognisedTextView;
     private TextView mRegState;
    private String mPlayText;
    private char mRecgs[] = new char[100];
    private int mRecgCount;

    static {
        System.loadLibrary("sinvoice");
        LogHelper.d(TAG, "sinvoice jnicall loadlibrary");
    }

    private static String strReg;
    private static int my_oid;


    @Override
    public int getLayoutResId() {
        return R.layout.add_contacts_activity;
    }

    @Override
    public void initData() {
        act = AddContactsActivity.this ;

        mIsReadFromFile = false;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);

        mSdcardPath = Environment.getExternalStorageDirectory().getPath();

    }

    @Override
    public void initListener() {
        mSinVoicePlayer = new SinVoicePlayer();
        mSinVoicePlayer.init(this);
        mSinVoicePlayer.setListener(this);

        mRecognition = new SinVoiceRecognition();
        mRecognition.init(MyApplication.getCtx());
        mRecognition.setListener(this);

        //我登陆的电话号码
//        mPlayTextView = (TextView) findViewById(R.id.playtext);
        SharedPreferences shared = getSharedPreferences(Constants.JJE_CONFIG, MODE_PRIVATE);
        my_oid = shared.getInt(Constants.WATCH_USER_ID, -1);
//        if(my_oid != -1)
//        mPlayTextView.setText(my_oid +"");
//        mPlayTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

//        mRecognisedTextView = (TextView) findViewById(R.id.regtext);
        mHanlder = new RegHandler(this);
        ImageView imageView = (ImageView) findViewById(R.id.iv_annimate);
        imageView.setBackgroundResource(R.drawable.sound_wave);
        // 将背景资源强转为动画资源
        AnimationDrawable ad = (AnimationDrawable) imageView.getBackground();
        // 动画开始播放
        ad.start();


        //发送声波
        sendSoundWave();
//        Button playStart = (Button) findViewById(R.id.start_play);
//        playStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                try {
//                    byte[] strs = mPlayTextView.getText().toString().getBytes("UTF8");
//                    if ( null != strs ) {
//                        int len = strs.length;
//                        LogHelper.d(TAG, "长度为:" + len);
//                        int []tokens = new int[len];
//                        int maxEncoderIndex = mSinVoicePlayer.getMaxEncoderIndex();
//                        LogHelper.d(TAG, "maxEncoderIndex:" + maxEncoderIndex);
//                        String encoderText = mPlayTextView.getText().toString();
//                        for ( int i = 0; i < len; ++i ) {
//                            if ( maxEncoderIndex < 255 ) {
//                                tokens[i] = Common.DEFAULT_CODE_BOOK.indexOf(encoderText.charAt(i));
//                            } else {
//                                tokens[i] = strs[i];
//                            }
//                        }
//                        mSinVoicePlayer.play(tokens, len, true, 2000);
//                    } else {
//                        mSinVoicePlayer.play(TOKENS, TOKEN_LEN, true, 2000);
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        Button playStop = (Button) findViewById(R.id.stop_play);
//        playStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                mSinVoicePlayer.stop();
//            }
//        });

        //开始接收
//        Button recognitionStart = (Button) findViewById(R.id.start_reg);
//        recognitionStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                mRecognition.start(TOKEN_LEN, mIsReadFromFile);
//            }
//        });

//        Button recognitionStop = (Button) findViewById(R.id.stop_reg);
//        recognitionStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                mRecognition.stop();
//            }
//        });

//        CheckBox cbReadFromFile = (CheckBox) findViewById(R.id.fread_from_file);
//        cbReadFromFile
//                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton arg0,
//                                                 boolean isChecked) {
//                        mIsReadFromFile = isChecked;
//                    }
//                });

//        Button btBackup = (Button) findViewById(R.id.back_debug_info);
//        btBackup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                backup();
//            }
//        });
//
//        Button btClearBackup = (Button) findViewById(R.id.clear_debug_info);
//        btClearBackup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                new AlertDialog.Builder(AddContactsActivity.this)
//                        .setTitle("information")
//                        .setMessage("Sure to clear?")
//                        .setPositiveButton("yes",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int whichButton) {
//                                        clearBackup();
//                                    }
//                                })
//                        .setNegativeButton("no",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int whichButton) {
//                                    }
//                                }).show();
//            }
//        });

//        Button btNextEffect = (Button) findViewById(R.id.next_mix);
//        btNextEffect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                mSinVoicePlayer.stop();
//                mSinVoicePlayer.uninit();
//                mSinVoicePlayer.init(AddContactsActivity.this);
//            }
//        });
    }

    /**
     * 发送声波
     */
    private void sendSoundWave() {
        //音量最大
        NotificationUtils.setVolume(context);
        //开始接收
        mRecognition.start(TOKEN_LEN, mIsReadFromFile);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
                try {
                    byte[] strs = (my_oid+"").toString().getBytes("UTF8");
                    if ( null != strs ) {
                        int len = strs.length;
                        LogHelper.d(TAG, "长度为:" + len);
                        int []tokens = new int[len];
                        int maxEncoderIndex = mSinVoicePlayer.getMaxEncoderIndex();
                        LogHelper.d(TAG, "maxEncoderIndex:" + maxEncoderIndex);
                        String encoderText = (my_oid+"").toString();
                        for ( int i = 0; i < len; ++i ) {
                            if ( maxEncoderIndex < 255 ) {
                                tokens[i] = Common.DEFAULT_CODE_BOOK.indexOf(encoderText.charAt(i));
                            } else {
                                tokens[i] = strs[i];
                            }
                        }
                        mSinVoicePlayer.play(tokens, len, true, 2000);
                    } else {
                        mSinVoicePlayer.play(TOKENS, TOKEN_LEN, true, 2000);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
    }

    @Override
    public void onResume() {
        super.onResume();

        mWakeLock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();

        mWakeLock.release();

        mSinVoicePlayer.stop();
        mRecognition.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRecognition.uninit();
        mSinVoicePlayer.uninit();
    }

    private void clearBackup() {
        delete(new File(mSdcardPath + BAKCUP_LOG_PATH));

        Toast.makeText(this, "clear backup log info successful",
                Toast.LENGTH_SHORT).show();
    }

    private static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    private void backup() {
        mRecognition.stop();

        String timestamp = getTimestamp();
        String destPath = mSdcardPath + BAKCUP_LOG_PATH + "/back_" + timestamp;
        try {
            copyDirectiory(destPath, mSdcardPath + "/sinvoice");
            copyDirectiory(destPath, mSdcardPath + "/sinvoice_log");

            FileOutputStream fout = new FileOutputStream(destPath + "/text.txt");
            String str = (my_oid+"").toString();
            byte[] bytes = str.getBytes();
            fout.write(bytes);

            if(strReg != null)
            str = strReg.toString();

            bytes = str.getBytes();
            fout.write(bytes);

            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "backup log info successful", Toast.LENGTH_SHORT)
                .show();
    }

    private static class RegHandler extends Handler {
        private StringBuilder mTextBuilder = new StringBuilder();
        private AddContactsActivity mAct;

        public RegHandler(AddContactsActivity act) {
            mAct = act;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_RECG_TEXT:
                    char ch = (char) msg.arg1;
//                mTextBuilder.append(ch);
                    mAct.mRecgs[mAct.mRecgCount++] = ch;
                    break;

                case MSG_RECG_START:
//                mTextBuilder.delete(0, mTextBuilder.length());
                    mAct.mRecgCount = 0;
                    break;

                case MSG_RECG_END:
                    LogHelper.d(TAG, "recognition end gIsError:" + msg.arg1);
                    if ( mAct.mRecgCount > 0 ) {
                        byte[] strs = new byte[mAct.mRecgCount];
                        for ( int i = 0; i < mAct.mRecgCount; ++i ) {
                            strs[i] = (byte)mAct.mRecgs[i];
                        }
                        try {
                            strReg = new String(strs, "UTF8");
                            if(strReg.equals((""+my_oid))){
                                //开始接收
                                mRecognition.start(TOKEN_LEN, mIsReadFromFile);
                            }else{

                                if (msg.arg1 >= 0) {
                                    Log.d(TAG, "reg ok!!!!!!!!!!!!");
                                    if (null != mAct) {
//                                        mAct.mRecognisedTextView.setText(strReg);

                                        // mAct.mRegState.setText("reg ok(" + msg.arg1 + ")");
                                    }else{
                                        LogHelper.d(TAG, "接收到的消息: mAct为空" );
                                    }
                                } else {
                                    Log.d(TAG, "reg error!!!!!!!!!!!!!");
                                    //设置接收到电话号码的监听
                                    telephoneListener.receiveTelephone(strReg);
//                                    mAct.mRecognisedTextView.setText(strReg);

                                    //结束页面
                                    act.finish();

                                    // mAct.mRegState.setText("reg err(" + msg.arg1 + ")");
                                    // mAct.mRegState.setText("reg err");

                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case MSG_PLAY_TEXT:
//                mAct.mPlayTextView.setText(mAct.mPlayText);
                    break;
            }
            super.handleMessage(msg);
        }

    }

    private static ReceiveTelephoneListener telephoneListener ;
    public void setReceiveTelephoneListener(ReceiveTelephoneListener receiveTelephoneListener){
        telephoneListener = receiveTelephoneListener ;
    }
    interface ReceiveTelephoneListener{
        void receiveTelephone(String phone);
    }


    @Override
    public void onSinVoiceRecognitionStart() {
        mHanlder.sendEmptyMessage(MSG_RECG_START);
    }

    @Override
    public void onSinVoiceRecognition(char ch) {
        mHanlder.sendMessage(mHanlder.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
    }

    @Override
    public void onSinVoiceRecognitionEnd(int result) {
        Log.d(TAG, "onSinVoiceRecognitionEnd: 胡巍"+result);
        mHanlder.sendMessage(mHanlder.obtainMessage(MSG_RECG_END, result, 0));
    }

    @Override
    public void onSinVoicePlayStart() {
        LogHelper.d(TAG, "start play");
    }

    @Override
    public void onSinVoicePlayEnd() {
        LogHelper.d(TAG, "stop play");
    }

    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return sdf.format(new Date());
    }

    private static void copyFile(File targetFile, File sourceFile)
            throws IOException {
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        outBuff.flush();

        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    private static void copyDirectiory(String targetDir, String sourceDir)
            throws IOException {
        (new File(targetDir)).mkdirs();
        File[] file = (new File(sourceDir)).listFiles();
        if (null != file) {
            for (int i = 0; i < file.length; i++) {
                if (file[i].isFile()) {
                    File sourceFile = file[i];
                    File targetFile = new File(
                            new File(targetDir).getAbsolutePath()
                                    + File.separator + file[i].getName());
                    copyFile(targetFile, sourceFile);
                }
                if (file[i].isDirectory()) {
                    String srcPath = sourceDir + "/" + file[i].getName();
                    String targetPath = targetDir + "/" + file[i].getName();
                    copyDirectiory(targetPath, srcPath);
                }
            }
        }
    }

    @Override
    public void onSinToken(int[] tokens) {
//        if (null != tokens) {
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < tokens.length; ++i) {
//                sb.append(Common.DEFAULT_CODE_BOOK.charAt(tokens[i]));
//            }
//
//            mPlayText = sb.toString();
//            LogHelper.d(TAG, "onSinToken " + mPlayText);
//            mHanlder.sendEmptyMessage(MSG_PLAY_TEXT);
//        }
    }

}
