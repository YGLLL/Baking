package com.example.ygl.baking;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ygl.baking.sql.model.Recipe;

import java.util.List;

/**
 * Created by YGL on 2017/7/7.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private Context mContext;
    private List<Recipe> recipeList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView recipeImageView;
        TextView recipeName;
        TextView recipeServings;
        public ViewHolder(View view){
            super(view);
            cardView=(CardView) view;
            recipeImageView=(ImageView)view.findViewById(R.id.recipe_image_view);
            recipeName=(TextView)view.findViewById(R.id.recipe_name);
            recipeServings=(TextView)view.findViewById(R.id.recipe_servings);
        }
    }

    public RecipeAdapter(List<Recipe> list,View emptyPrompt){
        recipeList=list;
        emptyPrompt.setVisibility(list.size()==0?View.VISIBLE:View.GONE);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent,int viewType){
        if (mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_recipe,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                Intent intent=new Intent(mContext,StepActivity.class);
                intent.putExtra(mContext.getString(R.string.recip_name),
                        recipeList.get(viewHolder.getAdapterPosition()).getRecipeName());
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Recipe recipe=recipeList.get(position);
        if (TextUtils.isEmpty(recipe.getImageUrl())){
            Glide.with(mContext).load(R.drawable.logo_black).into(holder.recipeImageView);
        }else {
            //加载网络图片
            Glide.with(mContext).load(recipe.getImageUrl()).into(holder.recipeImageView);
        }
        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeServings.append(recipe.getServings());
    }

    @Override
    public int getItemCount(){return recipeList.size();}
}
