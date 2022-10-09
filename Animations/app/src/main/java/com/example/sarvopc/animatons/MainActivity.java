package com.example.sarvopc.animatons;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    boolean bartIsShowing=true;

    public void fade(View view){

        ImageView bartimageView=(ImageView) findViewById(R.id.bartimageView);
        ImageView homerimageView= (ImageView) findViewById(R.id.homerimageView);

        if(bartIsShowing){
            bartIsShowing=false;
        bartimageView.animate().alpha(0).setDuration(2000);
        homerimageView.animate().alpha(1).setDuration(2000);}
        else {
            bartIsShowing=true;
            bartimageView.animate().alpha(1).setDuration(2000);
            homerimageView.animate().alpha(0).setDuration(2000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
