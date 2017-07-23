package com.example.ygl.baking;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.example.ygl.baking.sql.model.Step;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by YGL on 2017/7/12.
 */

public class StepActivity extends AppCompatActivity {
    private static final String TAG = "StepActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        Intent intent=getIntent();
        String recipeName=intent.getStringExtra("RecipeName");

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(recipeName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        StepFragment stepFragment=new StepFragment();
        Bundle fragmentBundle=new Bundle();
        fragmentBundle.putString("RecipeName",recipeName);
        stepFragment.setArguments(fragmentBundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout,stepFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }
}
