package com.example.ygl.baking;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by YGL on 2017/7/12.
 */

public class DescriptionActivity extends AppCompatActivity {
    private static final String TAG = "DescriptionActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("Description");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DescriptionFragment descriptionFragment=new DescriptionFragment();
        Bundle fragmentBundle=new Bundle();
        fragmentBundle.putString("StepId",getIntent().getStringExtra("StepId"));
        descriptionFragment.setArguments(fragmentBundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout,descriptionFragment).commit();
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
