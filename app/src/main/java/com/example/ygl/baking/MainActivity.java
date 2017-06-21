package com.example.ygl.baking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ygl.baking.sync.SyncAdapter;

import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Connector.getDatabase();//执行一次数据库操作，以创建数据库
        SyncAdapter.CreateSyncAccount(MainActivity.this);
    }
}
