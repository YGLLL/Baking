package com.example.ygl.baking.player;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ygl.baking.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YGL on 2017/7/13.
 */

public class VideoPlayer extends LinearLayout implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, SurfaceHolder.Callback,SeekBar.OnSeekBarChangeListener {
    /*
        MediaPlayer.OnBufferingUpdateListener监听事件，网络流媒体的缓冲监听
        MediaPlayer.OnCompletionListener监听事件，网络流媒体播放结束监听
        MediaPlayer.OnPreparedListener准备完毕后会回调该接口的方法
        SurfaceHolder.Callback
        SurfaceHolder是 Surface的控制器, 用于控制 SurfaceView 绘图, 处理画布上的动画, 渲染效果, 大小等;
        该接口中的方法 :
        surfaceChanged() : 在 Surface 大小改变时回调;
        surfaceCreated() : 在 Surface 创建时回调;
        surfaceDestroyed() : 在 Surface 销毁时回调;
        */

    private static final String TAG = "VideoPlayer";

    private Context mContext;

    private SurfaceView surfaceView;
    private Button startOrPause;
    private TextView progressTime;
    private TextView endTime;
    private SeekBar seekBar;
    private Button fullScreen;

    public MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private Timer mTimer;

    private int videoWidth;
    private int videoHeight;

    public int PLAYSTATE=0;
    public static final int STOPPLAY=0;
    public static final int STARTPLAY=1;
    public static final int PAUSEPLAY=2;
    public static final int CONTINUEPLAY=3;

    private String videoUrl="";

    private static VideoPlayer videoPlayer;

    public synchronized static VideoPlayer getInstance(final Context context, int windowWidth, final String videoUrl){
        if(videoPlayer==null){
            if(context==null){
                return null;
            }else {
                videoPlayer=new VideoPlayer(context,windowWidth,videoUrl);
            }
        }
        return videoPlayer;
    }

    private VideoPlayer(final Context context, int windowWidth, final String videoUrl) {
        super(context);

        mContext=context;
        mTimer=new Timer();
        mediaPlayer = new MediaPlayer();

        addPlayerView(windowWidth);

        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        seekBar.setOnSeekBarChangeListener(this);

        if (!TextUtils.isEmpty(videoUrl)){
            this.videoUrl=videoUrl;
            startOrPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play();
                    updateButtonBackground();
                }
            });
            fullScreen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext,FullScreenActivity.class);
                    intent.putExtra("PLAYSTATE",PLAYSTATE);
                    mContext.startActivity(intent);
                }
            });
            //开始更新进度条
            mTimer.schedule(mTimerTask,0,1000);
        }else {
            Log.i(TAG,"isEmpty(stepList.get(0).getVideoUrl())");
        }
    }

    //动态添加控件
    private void addPlayerView(int windowWidth){
        this.setOrientation(LinearLayout.VERTICAL);

        surfaceView=new SurfaceView(mContext);
        LinearLayout.LayoutParams surfaceViewParams=new LinearLayout.LayoutParams(windowWidth,windowWidth*9/16);
        this.addView(surfaceView,surfaceViewParams);

        RelativeLayout relativeLayout=new RelativeLayout(mContext);
        //50dp对应的px
        int relativeLayoutHeight=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50, getResources().getDisplayMetrics());

        startOrPause=new Button(mContext);
        startOrPause.setId(R.id.startOrPause);
        RelativeLayout.LayoutParams startOrPauseParams=new RelativeLayout.LayoutParams(relativeLayoutHeight,relativeLayoutHeight);
        startOrPauseParams.addRule(RelativeLayout.ALIGN_LEFT);
        startOrPauseParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(startOrPause,startOrPauseParams);

        progressTime=new TextView(mContext);
        progressTime.setText("00:00");
        progressTime.setId(R.id.progressTime);
        RelativeLayout.LayoutParams progressTimeParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressTimeParams.addRule(RelativeLayout.RIGHT_OF,R.id.startOrPause);
        progressTimeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(progressTime,progressTimeParams);

        seekBar=new SeekBar(mContext);
        RelativeLayout.LayoutParams seekBarParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        seekBarParams.addRule(RelativeLayout.RIGHT_OF,R.id.progressTime);
        seekBarParams.addRule(RelativeLayout.LEFT_OF,R.id.endTime);
        seekBarParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(seekBar,seekBarParams);

        endTime=new TextView(mContext);
        endTime.setText("00:00");
        endTime.setId(R.id.endTime);
        RelativeLayout.LayoutParams endTimeParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        endTimeParams.addRule(RelativeLayout.LEFT_OF,R.id.fullScreen);
        endTimeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(endTime,endTimeParams);

        fullScreen=new Button(mContext);
        fullScreen.setId(R.id.fullScreen);
        fullScreen.setBackgroundResource(R.drawable.full);
        RelativeLayout.LayoutParams fullScreenParams=new RelativeLayout.LayoutParams(relativeLayoutHeight,relativeLayoutHeight);
        fullScreenParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        endTimeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(fullScreen,fullScreenParams);

        this.addView(relativeLayout);
    }

    /*******************************************************
     *Timer是一种定时器工具，用来在一个后台线程计划执行指定任务。
     * 它可以计划执行一个任务一次或反复多次。
     * TimerTask一个抽象类，它的子类代表一个可以被Timer计划的任务。
     ******************************************************/
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if(mediaPlayer==null)
                return;
            if (mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
                handler.sendEmptyMessage(0);
            }
        }
    };

    Handler handler=new Handler(){
        public void handleMessage(Message message){
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();

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
    //*****************************************************

    public void play(){
        switch (PLAYSTATE){
            case STOPPLAY:
                startPlay();
                break;
            case STARTPLAY:
                pausePlay();
                break;
            case PAUSEPLAY:
                continuePlay();
                break;
            case CONTINUEPLAY:
                pausePlay();
                break;
        }
    }

    private void updateButtonBackground(){
        if(startOrPause!=null){
            switch (PLAYSTATE){
                case STOPPLAY:
                    startOrPause.setBackgroundResource(R.drawable.start);
                    break;
                case STARTPLAY:
                    startOrPause.setBackgroundResource(R.drawable.pause);
                    break;
                case PAUSEPLAY:
                    startOrPause.setBackgroundResource(R.drawable.start);
                    break;
                case CONTINUEPLAY:
                    startOrPause.setBackgroundResource(R.drawable.pause);
                    break;
            }
        }
    }

    public void startPlay() {
        Log.i(TAG,"public void startPlay");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.reset();//重置 MediaPlayer 对象
                    mediaPlayer.setDataSource(videoUrl);
                    mediaPlayer.prepare();//准备好之后自动播放
                    //mediaPlayer.start();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        PLAYSTATE=STARTPLAY;
    }

    public void pausePlay() {
        mediaPlayer.pause();
        PLAYSTATE=PAUSEPLAY;
    }

    public void continuePlay() {
        mediaPlayer.start();
        PLAYSTATE=CONTINUEPLAY;
    }

    public void killSelf(){
        mTimer.cancel();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        videoPlayer=null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.i(TAG, "surface changed");
    }
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i(TAG,"public void surfaceCreated(SurfaceHolder arg0)");
        setPlayerDisplay(surfaceHolder);//设置播放载体
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音量
        mediaPlayer.setOnBufferingUpdateListener(this);//监听事件，网络流媒体的缓冲监听
        mediaPlayer.setOnPreparedListener(this);//设置准备完毕监听器
        mediaPlayer.setOnCompletionListener(this);//设置播放完毕监听事件
        updateButtonBackground();
    }

    //设置播放载体
    public void setPlayerDisplay(SurfaceHolder surfaceHolder){
        mediaPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }


    //准备完毕后会回调的方法
    @Override
    public void onPrepared(MediaPlayer arg0) {
        Log.i(TAG,"public void onPrepared(MediaPlayer arg0)");
        videoWidth = mediaPlayer.getVideoWidth();
        videoHeight = mediaPlayer.getVideoHeight();
        if (videoHeight != 0 && videoWidth != 0) {
            arg0.start();
        }
    }

    //播放完毕回调方法
    @Override
    public void onCompletion(MediaPlayer arg0) {
        Log.i(TAG,"public void onCompletion(MediaPlayer arg0)");
        mediaPlayer.stop();
        //mediaPlayer.release();
        PLAYSTATE=STOPPLAY;
    }

    //缓冲进度更新回调的方法
    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
    }

    //触摸进度条回调方法
    private int changedProgress;
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
        this.changedProgress = progress *mediaPlayer.getDuration() / seekBar.getMax();
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.seekTo(changedProgress);
    }

    //重全屏状态回归调用方法
    protected void fullScreenBlack(int state){
        if (state==VideoPlayer.STARTPLAY||state==VideoPlayer.CONTINUEPLAY){
            videoPlayer.continuePlay();
        }
    }
}
