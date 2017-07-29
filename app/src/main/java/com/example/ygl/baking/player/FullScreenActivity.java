package com.example.ygl.baking.player;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ygl.baking.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YGL on 2017/7/28.
 */

public class FullScreenActivity extends AppCompatActivity implements SurfaceHolder.Callback,SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "FullScreenActivity";
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private VideoPlayer videoPlayer;
    private Button startOrPause;
    private TextView progressTime;
    private TextView endTime;
    private SeekBar seekBar;
    private Button smallScreen;
    private Timer mTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //设置横屏
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //隐藏状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        //隐藏工具栏
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        videoPlayer=VideoPlayer.getInstance(null,0,null);
        mTimer=new Timer();

        addPlayerView();

        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        startOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayer.play();
                updateButtonBackground();
            }
        });
        smallScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killSelf(videoPlayer.PLAYSTATE);
            }
        });
        //开始更新进度条
        mTimer.schedule(mTimerTask,0,1000);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void addPlayerView(){
        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearLayoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

        surfaceView=new SurfaceView(this);
        DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
        int windowWidth=displayMetrics.widthPixels;
        int windowHeight=displayMetrics.heightPixels;
        Log.i(TAG,"windowWidth:"+windowWidth+"        windowHeight:"+windowHeight);
        LinearLayout.LayoutParams surfaceViewParams=new LinearLayout.LayoutParams(windowWidth,windowHeight-150);
        linearLayout.addView(surfaceView,surfaceViewParams);

        RelativeLayout relativeLayout=new RelativeLayout(this);
        //50dp对应的px
        int relativeLayoutHeight=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams relativeLayoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,relativeLayoutHeight);
        startOrPause=new Button(this);
        startOrPause.setId(R.id.fullStartOrPause);
        RelativeLayout.LayoutParams startOrPauseParams=new RelativeLayout.LayoutParams(relativeLayoutHeight,relativeLayoutHeight);
        startOrPauseParams.addRule(RelativeLayout.ALIGN_LEFT);
        startOrPauseParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(startOrPause,startOrPauseParams);

        progressTime=new TextView(this);
        progressTime.setText("00:00");
        progressTime.setId(R.id.fullProgressTime);
        RelativeLayout.LayoutParams progressTimeParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressTimeParams.addRule(RelativeLayout.RIGHT_OF,R.id.fullStartOrPause);
        progressTimeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(progressTime,progressTimeParams);

        seekBar=new SeekBar(this);
        RelativeLayout.LayoutParams seekBarParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        seekBarParams.addRule(RelativeLayout.RIGHT_OF,R.id.fullProgressTime);
        seekBarParams.addRule(RelativeLayout.LEFT_OF,R.id.fullEndTime);
        seekBarParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(seekBar,seekBarParams);

        endTime=new TextView(this);
        endTime.setText("00:00");
        endTime.setId(R.id.fullEndTime);
        RelativeLayout.LayoutParams endTimeParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        endTimeParams.addRule(RelativeLayout.LEFT_OF,R.id.smallScreen);
        endTimeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(endTime,endTimeParams);

        smallScreen=new Button(this);
        smallScreen.setId(R.id.smallScreen);
        smallScreen.setBackgroundResource(R.drawable.small);
        RelativeLayout.LayoutParams smallScreenParams=new RelativeLayout.LayoutParams(relativeLayoutHeight,relativeLayoutHeight);
        smallScreenParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        endTimeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(smallScreen,smallScreenParams);
        linearLayout.addView(relativeLayout,relativeLayoutParams);

        addContentView(linearLayout,linearLayoutParams);
    }

    private void updateButtonBackground(){
        if(startOrPause!=null){
            switch (videoPlayer.PLAYSTATE){
                case VideoPlayer.STOPPLAY:
                    startOrPause.setBackgroundResource(R.drawable.start);
                    break;
                case VideoPlayer.STARTPLAY:
                    startOrPause.setBackgroundResource(R.drawable.pause);
                    break;
                case VideoPlayer.PAUSEPLAY:
                    startOrPause.setBackgroundResource(R.drawable.start);
                    break;
                case VideoPlayer.CONTINUEPLAY:
                    startOrPause.setBackgroundResource(R.drawable.pause);
                    break;
            }
        }
    }

    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if(videoPlayer.mediaPlayer==null)
                return;
            if (videoPlayer.mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
                handler.sendEmptyMessage(0);
            }
        }
    };

    Handler handler=new Handler(){
        public void handleMessage(Message message){
            int position = videoPlayer.mediaPlayer.getCurrentPosition();
            int duration = videoPlayer.mediaPlayer.getDuration();

            if (duration > 0) {
                long pos = seekBar.getMax() * position / duration;
                seekBar.setProgress((int) pos);

                progressTime.setText(progresstime(position));
                endTime.setText(progresstime(duration));
            }
        }
    };
    // 将毫秒数转换为时间格式
    private String progresstime(int progress) {
        Date date = new Date(progress);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(date);
    }

    /**
     * 设置为横屏
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"protected void onResume()");
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(videoPlayer!=null){
            videoPlayer.pausePlay();
        }
    }

    @Override
    public void onBackPressed() {
        killSelf(videoPlayer.PLAYSTATE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        videoPlayer.setPlayerDisplay(surfaceHolder);
        //恢复播放
        Intent intent=getIntent();
        int state=intent.getIntExtra("PLAYSTATE",-1);
        if (state==VideoPlayer.STARTPLAY||state==VideoPlayer.CONTINUEPLAY){
            videoPlayer.continuePlay();
        }
        updateButtonBackground();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }

    //触摸进度条回调方法
    int changedProgress;
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
        this.changedProgress = progress *videoPlayer.mediaPlayer.getDuration() / seekBar.getMax();
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        videoPlayer.mediaPlayer.seekTo(changedProgress);
    }

    private void killSelf(int state){
        mTimer.cancel();
        finish();
        videoPlayer.fullScreenBlack(state);
    }
}
