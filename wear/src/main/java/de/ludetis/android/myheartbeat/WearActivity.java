package de.ludetis.android.myheartbeat;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Vibrator;

public class WearActivity extends WearableActivity implements HeartbeatService.OnChangeListener {


    private static final String LOG_TAG = "MyHeart";

    private TextView mTextView;
    private TextView alertText;
    public static ServiceConnection sc;
    //private ProgressBar pb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        // inflate layout depending on watch type (round or square)
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // as soon as layout is there...
                mTextView = (TextView) stub.findViewById(R.id.heartbeat);
                alertText = (TextView) stub.findViewById(R.id.alertText);
                // bind to our service.
//                ComponentName heartService = startService(new Intent(WearActivity.this, HeartbeatService.class));
                sc = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder binder) {
                        Log.d(LOG_TAG, "connected to service.");
                        // set our change listener to get change events
                        ((HeartbeatService.HeartbeatServiceBinder)binder).setChangeListener(WearActivity.this);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {

                    }
                };
                Intent intent = new Intent(WearActivity.this, HeartbeatService.class);
//                startService(intent);
                bindService(intent, sc, Service.BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onValueChanged(int newValue) {
        // will be called by the service whenever the heartbeat value changes.
        mTextView.setText(Integer.toString(newValue));
        if (newValue > 90) {
            vibrateOn();
            alertText.setText("Chill out");
            startAn();
        }else if(newValue>80){
            alertText.setText("Take it easy");
        }else if(newValue>70){
            alertText.setText("Take a breath");
        }
    }

    public void vibrateOn(){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500, 50, 300};
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;

    }

    public void startAn(){
        ProgressBar pb = (ProgressBar) this.findViewById(R.id.progressBarToday);
        Animation an = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        an.setFillAfter(true);
        //if(pb.getAnimation() != null && pb.getAnimation().hasEnded()){
        an.setDuration(10000);
        pb.startAnimation(an);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(sc);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        mTextView.setBackgroundColor(Color.BLACK);
        mTextView.getPaint().setAntiAlias(false);
        mTextView.setTextColor(Color.WHITE);
    }

    @Override
    public void onExitAmbient(){
        //mTextView.setBackgroundColor(Color.CYAN);
        mTextView.getPaint().setAntiAlias(true);
        //mTextView.setTextColor(Color.BLACK);
        super.onExitAmbient();
    }

}
