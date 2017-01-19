package cn.wycode.game.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v){
        Intent i = new Intent();
        switch (v.getId()){
            case R.id.btn_brick:
                i.setClass(this,BrickDemo.class);
                break;
            case R.id.btn_run:
                i.setClass(this,RunDemo.class);
                break;
            case R.id.btn_bird:
                i.setClass(this,BirdDemo.class);
                break;
            case R.id.btn_airplane:
                i.setClass(this,AirplaneDemo.class);
                break;
        }
        startActivity(i);
    }
}
