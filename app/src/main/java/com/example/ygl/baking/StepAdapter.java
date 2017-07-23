package com.example.ygl.baking;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ygl.baking.sql.model.Step;

import java.util.List;

/**
 * Created by YGL on 2017/7/12.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {
    private Context mContext;
    private List<Step> stepList;
    private static final String TAG = "StepAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout stepLayout;
        TextView stepName;
        public ViewHolder(View view){
            super(view);
            stepLayout=(LinearLayout) view;
            stepName=(TextView)view.findViewById(R.id.step_name);
        }
    }

    public StepAdapter(List<Step> list){
        stepList=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        if (mContext==null){
            mContext=parent.getContext();
        }

        View view= LayoutInflater.from(mContext).inflate(R.layout.item_step,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.stepLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do string
                Intent intent=new Intent(mContext,DescriptionActivity.class);
                intent.putExtra("StepId",stepList.get(viewHolder.getAdapterPosition()).getStepId());
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        String stepName=stepList.get(position).getStepTitle();
        holder.stepName.setText(stepName);
    }

    @Override
    public int getItemCount(){return stepList.size();}
}
