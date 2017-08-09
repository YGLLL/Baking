package com.example.ygl.baking;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.ygl.baking.sql.model.Step;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by YGL on 2017/7/12.
 */

public class StepActivity extends AppCompatActivity {
    private static final String TAG = "StepActivity";
    private FrameLayout stepList;
    private FrameLayout description;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        //600dp对应的px
        int dp=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,600, getResources().getDisplayMetrics());
        //屏幕横向的px
        DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
        int widthPx=displayMetrics.widthPixels;
        if(widthPx<dp){
            //强制竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            //强制横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        Intent intent=getIntent();
        String recipeName=intent.getStringExtra("RecipeName");

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(recipeName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            //竖屏状态
            StepFragment stepFragment=new StepFragment();
            //使用Bundle携带数据
            Bundle fragmentBundle=new Bundle();
            fragmentBundle.putString("RecipeName",recipeName);
            stepFragment.setArguments(fragmentBundle);
            transaction.add(R.id.step_list,stepFragment).commit();
        }
        if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            //平板电脑横屏状态
            //重新设置weight
            stepList=(FrameLayout) findViewById(R.id.step_list);
            LinearLayout.LayoutParams stepListParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            stepListParams.weight=2;
            stepList.setLayoutParams(stepListParams);
            description=(FrameLayout)findViewById(R.id.description);
            LinearLayout.LayoutParams descriptionParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            descriptionParams.weight=1;
            description.setLayoutParams(descriptionParams);

            StepFragment stepFragment=new StepFragment();
            Bundle fragmentBundle=new Bundle();
            fragmentBundle.putString("RecipeName",recipeName);
            //告诉StepFragment现在是平板电脑横屏模式
            fragmentBundle.putBoolean("IsLand",true);
            stepFragment.setArguments(fragmentBundle);
            transaction.add(R.id.step_list,stepFragment).commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }
}
