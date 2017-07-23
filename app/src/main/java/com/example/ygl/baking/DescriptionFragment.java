package com.example.ygl.baking;

import android.app.Fragment;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ygl.baking.sql.model.Step;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

/**
 * Created by YGL on 2017/7/12.
 */

public class DescriptionFragment extends Fragment {
    private static final String TAG = "DescriptionFragment";
    private SurfaceView surfaceView;
    private Button start_pause;
    private VideoPlayer player;
    private LinearLayout videoPlayerLayout;
    private TextView stepTitle;
    private TextView description;
    private SeekBar seekBar;
    private TextView startTime;
    private TextView endTime;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_description,container,false);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        videoPlayerLayout=(LinearLayout)getActivity().findViewById(R.id.video_player);
        surfaceView=(SurfaceView)getActivity().findViewById(R.id.surface_view);
        start_pause=(Button)getActivity().findViewById(R.id.start_pause);
        stepTitle=(TextView)getActivity().findViewById(R.id.step_title);
        description=(TextView)getActivity().findViewById(R.id.description);
        seekBar=(SeekBar)getActivity().findViewById(R.id.seek_bar);
        startTime=(TextView)getActivity().findViewById(R.id.start_time);
        endTime=(TextView)getActivity().findViewById(R.id.end_time);
        player=new VideoPlayer(surfaceView,seekBar,startTime,endTime);

        String stepId=getArguments().getString("StepId");
        final List<Step> stepList= DataSupport.select("StepTitle","Description","VideoUrl").where("StepId=?",stepId).find(Step.class);
        if(stepList.size()>0){
            stepTitle.setText(stepList.get(0).getStepTitle());
            description.setText(stepList.get(0).getDescription());
            final String videoUrl=stepList.get(0).getVideoUrl();
            if (!TextUtils.isEmpty(videoUrl)){
                start_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startPlay(videoUrl);
                    }
                });
            }else {
                hideVideoPlayer();
                Log.i(TAG,"isEmpty(stepList.get(0).getVideoUrl())");
            }
        }else {
            hideVideoPlayer();
            Log.i(TAG,"stepList.size()<=0");
        }
    }

    private void hideVideoPlayer(){
        if(videoPlayerLayout!=null){
            LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)videoPlayerLayout.getLayoutParams();
            params.width=0;
            params.height=0;
            videoPlayerLayout.setLayoutParams(params);
        }
    }

    private void startPlay(String videoUrl){
        switch (VideoPlayer.PLAYSTATE){
            case VideoPlayer.STOPPLAY:
                player.startPlay("http://36.250.248.34/v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4");
                start_pause.setText("pause");
                break;
            case VideoPlayer.STARTPLAY:
                player.pausePlay();
                start_pause.setText("start");
                break;
            case VideoPlayer.PAUSEPLAY:
                player.continuePlay();
                start_pause.setText("pause");
                break;
            case VideoPlayer.CONTINUEPLAY:
                player.pausePlay();
                start_pause.setText("start");
                break;
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        player.stopPlay();
    }


}
