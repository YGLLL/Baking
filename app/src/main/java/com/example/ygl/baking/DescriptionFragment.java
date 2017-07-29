package com.example.ygl.baking;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ygl.baking.player.VideoPlayer;
import com.example.ygl.baking.sql.model.Step;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by YGL on 2017/7/12.
 */

public class DescriptionFragment extends Fragment {
    private static final String TAG = "DescriptionFragment";
    VideoPlayer videoPlayer;
    private TextView stepTitle;
    private TextView description;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_description,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        stepTitle=(TextView)getActivity().findViewById(R.id.step_title);

        String stepId=getArguments().getString("StepId");
        final List<Step> stepList= DataSupport.select("StepTitle","Description","VideoUrl").where("StepId=?",stepId).find(Step.class);
        if(stepList.size()>0){
            stepTitle.setText(stepList.get(0).getStepTitle());
            addView(stepList.get(0).getDescription(),stepList.get(0).getVideoUrl());
        }else {
            Log.i(TAG,"stepList.size()<=0");
        }
    }

    private void addView(String descriptionStr,String videoUrl){
        LinearLayout layout=(LinearLayout)getActivity().findViewById(R.id.description_layout) ;
        if (!TextUtils.isEmpty(videoUrl)) {
            DisplayMetrics displayMetrics=getActivity().getResources().getDisplayMetrics();
            int windowWidth = displayMetrics.widthPixels;
            videoPlayer=VideoPlayer.getInstance(getActivity(),windowWidth,"http://36.250.248.34/v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4");
            //"http://36.250.248.34/v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4"
            LinearLayout.LayoutParams playerParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.addView(videoPlayer,playerParams);
        }
        description=new TextView(getActivity());
        description.setText(descriptionStr);
        description.setTextSize(20);
        description.setTextColor(getResources().getColor(R.color.dimgray));
        LinearLayout.LayoutParams descriptionParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int dp=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20, getResources().getDisplayMetrics());
        descriptionParams.setMargins(dp,dp,dp,dp);
        layout.addView(description,descriptionParams);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(videoPlayer!=null){
            videoPlayer.pausePlay();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(videoPlayer!=null){
            videoPlayer.killSelf();
            videoPlayer=null;
        }
    }
}
