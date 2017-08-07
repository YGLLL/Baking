package com.example.ygl.baking;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ygl.baking.sql.model.Step;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by YGL on 2017/7/12.
 */

public class StepFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_step,container,false);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        RecyclerView recyclerView=(RecyclerView)getActivity().findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager=new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(layoutManager);
        String recipeName=getArguments().getString("RecipeName");
        List<Step> stepList= DataSupport.select("StepTitle","StepId").where("ForRecipe=?",recipeName).find(Step.class);
        StepAdapter stepAdapter;
        if(getArguments().getBoolean("IsLand")){
            //平板电脑横屏模式
            stepAdapter=new StepAdapter(true,stepList);
        }else {
            stepAdapter=new StepAdapter(stepList);
        }
        recyclerView.setAdapter(stepAdapter);
    }
}
