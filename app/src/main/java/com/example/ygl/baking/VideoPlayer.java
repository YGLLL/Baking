package com.example.ygl.baking;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YGL on 2017/7/13.
 */

public class VideoPlayer implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {
    //MediaPlayer.OnBufferingUpdateListener监听事件，网络流媒体的缓冲监听
    //MediaPlayer.OnCompletionListener监听事件，网络流媒体播放结束监听
    //MediaPlayer.OnPreparedListener准备完毕后会回调该接口的方法
    /*SurfaceHolder.Callback
    *SurfaceHolder是 Surface的控制器, 用于控制 SurfaceView 绘图, 处理画布上的动画, 渲染效果, 大小等;
    *该接口中的方法 :
    *-- surfaceChanged() : 在 Surface 大小改变时回调;
    *-- surfaceCreated() : 在 Surface 创建时回调;
    *-- surfaceDestroyed() : 在 Surface 销毁时回调;*/

    private static final String TAG = "VideoPlayer";
    private int videoWidth;
    private int videoHeight;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private SeekBar seekBar;
    private TextView startTime;
    private TextView endTime;
    private Timer mTimer=new Timer();

    public static int PLAYSTATE=0;
    public static final int STOPPLAY=0;
    public static final int STARTPLAY=1;
    public static final int PAUSEPLAY=2;
    public static final int CONTINUEPLAY=3;

    public VideoPlayer(SurfaceView surfaceView, SeekBar seekBar,TextView startTime,TextView endTime) {
        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mediaPlayer = new MediaPlayer();

        this.seekBar=seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        this.startTime=startTime;
        this.endTime=endTime;

        mTimer.schedule(mTimerTask, 0, 500);
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
                startTime.setText(progresstime(position));
                endTime.setText(progresstime(duration));
                Log.i(TAG,"endTime:"+duration);
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

    public void startPlay(final String videoUrl) {
        Log.i(TAG,"public void playUrl(String videoUrl)");
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

    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        PLAYSTATE=STOPPLAY;
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.e("mediaPlayer", "surface changed");
    }
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i(TAG,"public void surfaceCreated(SurfaceHolder arg0)");
        mediaPlayer.setDisplay(surfaceHolder);//设置播放载体
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音量
        mediaPlayer.setOnBufferingUpdateListener(this);//监听事件，网络流媒体的缓冲监听
        mediaPlayer.setOnPreparedListener(this);//设置准备完毕监听器
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
    }

    //缓冲进度更新回调的方法
    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        Log.i(TAG,"public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress)");
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            this.progress = progress *mediaPlayer.getDuration() / seekBar.getMax();
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(progress);
        }
    }
}
