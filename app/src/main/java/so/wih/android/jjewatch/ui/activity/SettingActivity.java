package so.wih.android.jjewatch.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.provider.Settings;
import android.widget.SeekBar;

import butterknife.BindView;
import so.wih.android.jjewatch.R;

/**
 * Created by Administrator on 2016/11/23.
 */

public class SettingActivity extends BaseActivity
        implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.sb_sys_voice)
    SeekBar sbSysVoice;
    @BindView(R.id.sb_sunlight)
    SeekBar sbSunlight;
    private SharedPreferences sp;

    @Override
    public void initLayout() {
        super.initLayout();
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void initView() {
        super.initView();

        sp = getSharedPreferences("FILE_CONFIG", MODE_PRIVATE);

        sbSysVoice.setOnSeekBarChangeListener(this);
        sbSunlight.setOnSeekBarChangeListener(this);
        int sunlight_progress = sp.getInt("sunlight_progress", 0);
        int voice_progress = sp.getInt("voice_progress", 0);

        sbSunlight.setProgress(sunlight_progress);
        sbSysVoice.setProgress(voice_progress);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        switch (id) {
            case R.id.sb_sys_voice://改变系统声音
                //音频管理器
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
                sp.edit().putInt("voice_progress", progress).commit();
                break;
            case R.id.sb_sunlight: //改变系统亮度

                //设置系统亮度
                Settings.System.putInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, progress);
                sp.edit().putInt("sunlight_progress", progress).commit();
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}
