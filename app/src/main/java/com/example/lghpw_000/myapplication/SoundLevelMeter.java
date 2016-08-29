package com.example.lghpw_000.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by lghpw_000 on 2016/08/28.
 */
public class SoundLevelMeter extends Service implements Runnable {
    private static final int SAMPLE_RATE = 8000;

    private int bufferSize;
    private AudioRecord audioRecord;
    private boolean isRecording;
    private boolean isPausing;
    private double baseValue;

    public double test;
    public double tempo;
    public double avgdb;
    private short avg;

    public interface SoundLevelMeterListener {
        void onMeasure(double db,double db2);
    }

    private SoundLevelMeterListener listener;

    public SoundLevelMeter() {

        baseValue = 12.0;
        test = 0;
        tempo = 0;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setListener(SoundLevelMeterListener l) {
        listener = l;
    }

    public void run() {
       if(MainActivity.thread_open!=true) {

           bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                   AudioFormat.CHANNEL_CONFIGURATION_MONO,
                   AudioFormat.ENCODING_PCM_16BIT);
           audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                   SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                   AudioFormat.ENCODING_PCM_16BIT, bufferSize*2);
           start();
           Log.d("SLMdです", "スタート");
           Log.d("SLMdです", "isrecording"+isRecording);
           android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
           audioRecord.startRecording();
           short[] buffer = new short[bufferSize];
           while (isRecording) {
               Log.d("SLMdです", "isrecording");

               int read = audioRecord.read(buffer, 0, bufferSize);

               if (read < 0) {

               }

               int maxValue = 0;
               long sum = 0;
               for (int i = 0; i < read; i++) {
                   maxValue = Math.max(maxValue, buffer[i]);
                   sum += Math.abs(buffer[i]);

               }

               avg = (short) (sum / bufferSize);

               test = 20.0 * Math.log10(maxValue / baseValue);
               avgdb = 20.0 * Math.log10(avg / baseValue);
               tempo = tempo + 1;

               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   // TODO Auto-generated catch block

               }

               MainActivity.mainHandler.post(new Runnable() {
                   @Override
                   public void run() {
                       listener.onMeasure(test, avgdb);
                   }

               });

           }

           Log.d("audioRecord.stop();の上", "thread" + MainActivity.thread_open);
           audioRecord.stop();
           Log.d("audioRecord.stop();の↓", "thread" + MainActivity.thread_open);
           audioRecord.release();
           if(MainActivity.thread_open==true){
               stop();
           }
       }
    }
    public void start() {
        isRecording = true;
    }

    public void stop() {
        isRecording = false;
    }

    public void pause() {
        if (!isPausing)
            audioRecord.stop();
        isPausing = true;
    }
}
