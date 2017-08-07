package com.example.ygl.baking;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.example.ygl.baking.sql.StubProvider;
import com.example.ygl.baking.sql.model.Recipe;
import com.example.ygl.baking.sync.SyncAdapter;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;
import java.util.List;
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    private List<Recipe> recipeList;
    private RecipeAdapter recipeAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //600dp对应的px
        int dp=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,600, getResources().getDisplayMetrics());
        //屏幕横向的px
        DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
        int widthPx=displayMetrics.widthPixels;

        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        if(widthPx>=dp){
            //强制横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            //强制竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            layoutManager=new GridLayoutManager(this,3);
        }
        if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            layoutManager=new GridLayoutManager(this,1);
        }

        recyclerView=(RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        recipeList= DataSupport.findAll(Recipe.class);
        if(recipeList.size()<=0){
            SyncAdapter.CreateSyncAccount(MainActivity.this);
        }else {
            recipeAdapter=new RecipeAdapter(recipeList);
            recyclerView.setAdapter(recipeAdapter);
        }

        getLoaderManager().initLoader(0, null,this);
    }

    //并非加载器的正确使用方法
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        Log.i(TAG,"onCreateLoader");
        return new CursorLoader(MainActivity.this, StubProvider.bakingUri,null,null,null,null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG,"onLoadFinished(Loader<Cursor> loader, Cursor cursor)");
        recipeList= DataSupport.findAll(Recipe.class);
        recipeAdapter=new RecipeAdapter(recipeList);
        recipeAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recipeAdapter);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        //屏幕方向改变需要做的事
    }
}
