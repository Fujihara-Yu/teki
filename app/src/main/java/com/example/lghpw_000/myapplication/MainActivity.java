package com.example.lghpw_000.myapplication;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SoundLevelMeter.SoundLevelMeterListener{
    private AudioManager audio;
    private int musicMaxVol;
    private int musicVol;
    private TextView curMusicVolTex;
    private TextView textView3;
    private SoundLevelMeter soundLevelMeter;
    static boolean thread_open =true;
    private double testh;

    static Handler mainHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soundLevelMeter = new SoundLevelMeter();
        TextView mvol = (TextView) findViewById(R.id.mvol);
        curMusicVolTex = (TextView) findViewById(R.id.crvol);
        textView3 = (TextView) findViewById(R.id.crdb);


        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        musicMaxVol = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        musicVol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        mvol.setText(String.valueOf(musicMaxVol));
        curMusicVolTex.setText(String.valueOf(musicVol));
        textView3.setText("DBが表示されまーす");

        mainHandler = new Handler();

        soundLevelMeter.setListener(this);
        //(new Thread(soundLevelMeter)).start();

        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thread_open) {
                    Log.d("thraedの数","!!!!!!!!");
                    (new Thread(soundLevelMeter)).start();
                    thread_open=false;
                    Log.d("スタートです","thread="+thread_open);
                    musicVol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                    curMusicVolTex.setText(String.valueOf(musicVol));
                }

            }
        });
        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thread_open != true) {
                    soundLevelMeter.stop();
                    //soundLevelMeter.interrupt();
                    thread_open=true;
                    Log.d("ストップです","thread="+thread_open);
                    musicVol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                    curMusicVolTex.setText(String.valueOf(musicVol));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundLevelMeter.stop();
    }

    public void onMeasure(double db,double db2){

        textView3.setText(String.valueOf(db));
        Log.d("MAです","音量判定");
        if(db2 < 30.0){
            audio.setStreamVolume(AudioManager.STREAM_MUSIC,9,0);
            musicVol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            curMusicVolTex.setText(String.valueOf(musicVol));
        }else if(db2<35.0){
            audio.setStreamVolume(AudioManager.STREAM_MUSIC,6,0);
            musicVol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            curMusicVolTex.setText(String.valueOf(musicVol));
        }else{
            audio.setStreamVolume(AudioManager.STREAM_MUSIC,3,0);
            musicVol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            curMusicVolTex.setText(String.valueOf(musicVol));
        }

        testh = db;

    }
}
