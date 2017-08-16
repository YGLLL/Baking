package com.example.ygl.baking;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ygl.baking.Util.Util;
import com.example.ygl.baking.player.VideoPlayer;
import com.example.ygl.baking.sql.model.Step;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by YGL on 2017/7/12.
 */

public class DescriptionFragment extends Fragment {
    private static final String TAG = "DescriptionFragment";
    private Context mContext;
    private VideoPlayer videoPlayer;
    private TextView stepTitle;
    private TextView description;
    private Button nextButton;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private String stepId="";
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        mContext=getActivity();
        scrollView=new ScrollView(mContext);
        linearLayout=new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        scrollView.addView(linearLayout,params);
        return scrollView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        stepId=getArguments().getString(getString(R.string.step_id));
        List<Step> stepList= DataSupport.select("StepTitle","Description","VideoUrl").where("StepId=?",stepId).find(Step.class);
        if(stepList.size()>0){
            addDescriptionView(stepList.get(0));
        }else {
            Log.i(TAG,"stepList.size()<=0");
        }
    }

    private void addDescriptionView(Step step){
        //20dp对应的PX
        int dp= Util.convertDipOrPx(mContext,20);

        //标题
        stepTitle=new TextView(mContext);
        stepTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,dp*2);
        stepTitle.setTextColor(getResources().getColor(R.color.dimgray));
        stepTitle.setText(step.getStepTitle());
        final LinearLayout.LayoutParams stepTitleParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        stepTitleParams.setMargins(dp,dp,dp,dp);
        stepTitle.setLayoutParams(stepTitleParams);

        //具体内容TextView
        description=new TextView(mContext);
        description.setTextSize(TypedValue.COMPLEX_UNIT_PX,dp);
        description.setTextColor(getResources().getColor(R.color.dimgray));
        description.setText(step.getDescription());
        final LinearLayout.LayoutParams descriptionParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descriptionParams.setMargins(dp,dp,dp,dp);
        description.setLayoutParams(descriptionParams);

        //Next按键
        nextButton=new Button(mContext);
        nextButton.setText(getString(R.string.next_button));
        nextButton.setTextColor(getResources().getColor(R.color.colorAccent));
        nextButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,dp);
        nextButton.setBackgroundColor(getResources().getColor(R.color.white));
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=Integer.valueOf(stepId);
                String newStepId=String.valueOf(i+1);
                DescriptionFragment descriptionFragment=new DescriptionFragment();
                //使用Bundle携带数据
                Bundle fragmentBundle=new Bundle();
                fragmentBundle.putString(getString(R.string.step_id),newStepId);
                descriptionFragment.setArguments(fragmentBundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                if(getActivity().findViewById(R.id.description)!=null){
                    transaction.replace(R.id.description,descriptionFragment).commit();
                }
                if(getActivity().findViewById(R.id.frame_layout)!=null){
                    transaction.replace(R.id.frame_layout,descriptionFragment).commit();
                }
            }
        });
        LinearLayout.LayoutParams nextButtonParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nextButtonParams.setMargins(dp,dp,dp,dp);
        nextButtonParams.gravity=Gravity.CENTER_HORIZONTAL;
        nextButton.setLayoutParams(nextButtonParams);

        final String videoUrl=step.getVideoUrl();
        if (!TextUtils.isEmpty(videoUrl)) {
            linearLayout.post(new Runnable() {
                @Override
                public void run() {
                    linearLayout.addView(stepTitle);
                    videoPlayer=VideoPlayer.getInstance(mContext,linearLayout.getWidth(),videoUrl);
                    linearLayout.addView(videoPlayer);
                    linearLayout.addView(description);
                    linearLayout.addView(nextButton);
                }
            });
        }else {
            linearLayout.addView(stepTitle);
            linearLayout.addView(description);
            linearLayout.addView(nextButton);
        }
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
