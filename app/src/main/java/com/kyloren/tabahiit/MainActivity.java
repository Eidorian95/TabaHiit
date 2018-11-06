package com.kyloren.tabahiit;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private Button btStart, btPause, btResume;
    private TextView tvTitles, tvTimes, tvFinish, tvSets,tvLaps, tvTotalTime;
    private CircularProgressBar csbReady,csbWork, csbRest;
    public CountDownTimer cdTimer, resumeTimer, restTimer, fullRestTimer, cdReadyTimer;
    public  int actualState;
    public  int laps, finalLap, sets, finalSet;
    long reminingTime = 0;
    public long milisInFuture=0;
    boolean isPaused, isCanceled;
    private TransparentFragment transFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        setViews();
        readyTime();
        pauseTime();
        resumeTime();

        transFragment = new TransparentFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment_place,transFragment);
        fragmentTransaction.commit();

        btStart.setVisibility(View.VISIBLE);
        btResume.setVisibility(View.INVISIBLE);
        btPause.setVisibility(View.INVISIBLE);

        csbWork.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int  sharedLaps= sharedPref.getInt("finalLaps",2);
        int  sharedSets= sharedPref.getInt("finalSets",3);
        int ready = sharedPref.getInt("ready",10);
        int work = sharedPref.getInt("work",20);
        int rest = sharedPref.getInt("rest",20);
        int fullRest = sharedPref.getInt("fullRest",20);

        //ready
        final int totalTimeWork = work  * sharedSets;
        final int totalTimeRest = rest  * sharedSets;
        final int totalTimeRestLaps = fullRest * sharedLaps;
        final int fullTime = (totalTimeWork + totalTimeRestLaps + totalTimeRest + ready  - rest - fullRest) * 1000 ;


        tvTotalTime.setText("" + String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(fullTime),
                TimeUnit.MILLISECONDS.toSeconds(fullTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(fullTime))));

    }

    public void readyTime(){

            btStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    actualState = 0;


                    isPaused = false;
                    isCanceled= false;

                    btStart.setVisibility(View.INVISIBLE);
                    btPause.setVisibility(View.VISIBLE);

                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    int  sharedLaps= sharedPref.getInt("finalLaps",2);
                    int  sharedSets= sharedPref.getInt("finalSets",3);
                    int ready = sharedPref.getInt("ready",10);
                    int work = sharedPref.getInt("work",20);
                    int rest = sharedPref.getInt("rest",20);
                    int fullRest = sharedPref.getInt("fullRest",20);


                    final int totalTimeWork = work  * sharedSets;
                    final int totalTimeRest = rest  * sharedSets;
                    final int totalTimeRestLaps = fullRest * sharedLaps;
                    final int fullTime = (totalTimeWork + totalTimeRestLaps + totalTimeRest + ready - rest - fullRest) * 1000 ;


                    tvTotalTime.setText("" + String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(fullTime),
                            TimeUnit.MILLISECONDS.toSeconds(fullTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(fullTime))));

                    finalLap =  sharedLaps ;
                    laps = 1;
                    tvLaps.setText(""+laps+" / "+finalLap);

                    sets = 1;
                    finalSet = sharedSets;
                    tvSets.setText(""+sets+" / "+finalSet);


                    milisInFuture = (ready * 1000)+150;
                    final long countDownInterval = 1000;

                    cdReadyTimer = new CountDownTimer(milisInFuture, countDownInterval) {

                        public void onTick(long millisUntilFinished) {

                            if(tvTimes.getText().toString().equals("00:03") || tvTimes.getText().toString().equals("00:02")){
                                setSound(R.raw.beep_short);
                            }


                            if (isPaused || isCanceled) {

                                cdReadyTimer.cancel();

                            }else{
                                actualState = 0;

                                csbReady.setMaximum((int) milisInFuture);
                                csbReady.setProgress((int) millisUntilFinished);


                                tvTitles.setTextColor(getResources().getColor(R.color.colorPrimary2));
                                tvTimes.setTextColor(getResources().getColor(R.color.colorPrimary2));
                                tvTimes.setText("" + String.format("%02d:%02d",
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                                reminingTime = millisUntilFinished;

                            }

                        }

                        public void onFinish() {
                            tvTimes.setText("00:00");
                            csbReady.setProgress(0);
                            setSound(R.raw.beep_long);

                            workTime();

                        }
                    }.start();
                }
            });

        }

    public  void workTime(){

           actualState = 1;

            isPaused = false;

            btStart.setVisibility(View.INVISIBLE);
            btPause.setVisibility(View.VISIBLE);

            csbWork.setVisibility(View.VISIBLE);
            csbRest.setVisibility(View.INVISIBLE);

            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

                   int work = sharedPref.getInt("work",20);
                   milisInFuture = (work * 1000)+150;
                   final long countDownInterval = 1000;

                   //WORK TIME TIMER
                   cdTimer = new CountDownTimer(milisInFuture, countDownInterval) {

                       public void onTick(long millisUntilFinished) {

                           if(tvTimes.getText().toString().equals("00:03") || tvTimes.getText().toString().equals("00:02")){
                               setSound(R.raw.beep_short);
                           }


                           if (isPaused || isCanceled) {

                               cdTimer.cancel();

                           } else {

                              actualState = 1;

                              csbWork.setVisibility(View.VISIBLE);

                              csbReady.setVisibility(View.INVISIBLE);
                              csbRest.setVisibility(View.INVISIBLE);

                              csbWork.setMaximum((int) milisInFuture);
                              csbWork.setProgress((int) millisUntilFinished);


                              tvTitles.setText(R.string.work);
                              tvTitles.setTextColor(getResources().getColor(R.color.colorWork));
                              tvTimes.setTextColor(getResources().getColor(R.color.colorWork));
                              tvTimes.setText("" + String.format("%02d:%02d",
                                       TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                       TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                               TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                               reminingTime = millisUntilFinished;


                           }
                       }

                       public void onFinish() {

                           setSound(R.raw.beep_long);
                           if (laps == finalLap && sets == finalSet) {

                               cdTimer.cancel();
                               restTimer.cancel();

                               btStart.setVisibility(View.INVISIBLE);
                               btPause.setVisibility(View.INVISIBLE);
                               tvFinish.setVisibility(View.VISIBLE);

                               tvTimes.setText(R.string.zero);
                               csbWork.setProgress(0);
                               csbRest.setProgress(0);

                           }else if(sets == finalSet) {




                              fullRestTimer();
                               laps++;
                               tvLaps.setText("" + laps + " / " + finalLap);

                               csbWork.setProgress(0);
                               tvTimes.setText("00:00");

                               }else{

                               csbWork.setProgress(0);
                               tvTimes.setText(R.string.zero);

                               restTime();
                               }
                       }
                   }.start();

   }

    public void restTime() {

        isPaused = false;

        csbRest.setVisibility(View.VISIBLE);
        csbWork.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int rest = sharedPref.getInt("rest",10);

        milisInFuture = (rest * 1000)+150;
        final long countDownInterval = 1000;


        restTimer = new CountDownTimer(milisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {

                if (isPaused || isCanceled) {

                    restTimer.cancel();

                } else {

                        if(tvTimes.getText().toString().equals("00:03") || tvTimes.getText().toString().equals("00:02")){
                            setSound(R.raw.beep_short);
                        }
                        actualState = 2;


                        csbRest.setMaximum((int) milisInFuture);
                        csbRest.setProgress((int) millisUntilFinished);



                        tvTitles.setText(R.string.rest);
                        tvTitles.setTextColor(getResources().getColor(R.color.colorRest));
                        tvTimes.setTextColor(getResources().getColor(R.color.colorRest));
                        tvTimes.setText("" + String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                        reminingTime = millisUntilFinished;


                }
            }

            public void onFinish() {

                setSound(R.raw.beep_long);

                csbRest.setProgress(0);
                    tvTimes.setText(R.string.zero);

                    sets++;
                    tvSets.setText("" + sets + " / " + finalSet);


                    workTime();

            }
        }.start();

    }

    public void pauseTime() {
               btPause.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       isPaused = true;
                       isCanceled = true;

                       btPause.setVisibility(View.INVISIBLE);
                       btResume.setVisibility(View.VISIBLE);

                   }
               });
    }

    public void resumeTime(){

        btResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                isPaused = false;
                isCanceled = false;



                btResume.setVisibility(View.INVISIBLE);
                btPause.setVisibility(View.VISIBLE);


                final long millisInFuture = reminingTime;
                long countDownInterval = 1000;

              resumeTimer = new CountDownTimer(millisInFuture, countDownInterval){
                    public void onTick(long millisUntilFinished){

                        if (isPaused || isCanceled) {

                            //If user requested to pause or cancel the count down timer
                            cancel();
                        }
                        else {
                            switch (actualState){
                                case 0:
                                    if(tvTimes.getText().toString().equals("00:03") || tvTimes.getText().toString().equals("00:02")){
                                        setSound(R.raw.beep_short);
                                    }
                                    csbReady.setProgress((int) millisUntilFinished);

                                    tvTimes.setText("" + String.format("%02d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                                    reminingTime = millisUntilFinished;



                                    break;

                                case 1:
                                    if(tvTimes.getText().toString().equals("00:03") || tvTimes.getText().toString().equals("00:02")){
                                        setSound(R.raw.beep_short);
                                    }
                                    csbWork.setProgress((int) millisUntilFinished);

                                    tvTimes.setText("" + String.format("%02d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));


                                    reminingTime = millisUntilFinished;



                                    break;
                                case 2:
                                    if(tvTimes.getText().toString().equals("00:03") || tvTimes.getText().toString().equals("00:02")){
                                        setSound(R.raw.beep_short);
                                    }

                                    csbRest.setProgress((int) millisUntilFinished);

                                    tvTimes.setText("" + String.format("%02d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));


                                    reminingTime = millisUntilFinished;



                                    break;

                                case 3:
                                    if(tvTimes.getText().toString().equals("00:03") || tvTimes.getText().toString().equals("00:02")){
                                        setSound(R.raw.beep_short);
                                    }
                                    csbRest.setProgress((int) millisUntilFinished);

                                    tvTimes.setText("" + String.format("%02d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                                    reminingTime = millisUntilFinished;


                                    break;
                            }

                        }
                    }
                    public void onFinish(){
                        switch (actualState){

                            case 0:
                                setSound(R.raw.beep_long);

                                tvTimes.setText(R.string.zero);
                                workTime();

                                break;

                            case 1:
                                setSound(R.raw.beep_long);

                                if (laps == finalLap && sets == finalSet) {

                                    cdTimer.cancel();
                                    btStart.setVisibility(View.INVISIBLE);
                                    btPause.setVisibility(View.INVISIBLE);
                                    tvFinish.setVisibility(View.VISIBLE);

                                }else if(sets == finalSet) {

                                    fullRestTimer();
                                    laps++;
                                    tvLaps.setText(laps + " / " + finalLap);

                                    csbWork.setProgress(0);
                                    tvTimes.setText(R.string.zero);

                                }else {

                                    csbWork.setProgress(0);
                                    tvTimes.setText(R.string.zero);

                                    restTime();
                                }

                                break;

                            case 2:
                                setSound(R.raw.beep_long);

                                csbRest.setProgress(0);
                                tvTimes.setText(R.string.zero);

                                sets++;
                                tvSets.setText(sets+" / "+finalSet);

                                workTime();

                                break;

                            case 3:
                                setSound(R.raw.beep_long);

                                workTime();

                                sets = 1;
                                tvSets.setText(sets+" / "+finalSet);
                                csbRest.setProgress(0);
                                tvTimes.setText(R.string.zero);
                        }
                    }
                }.start();
            }
        });
    }

    public  void fullRestTimer(){

        isPaused = false;

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int fullRest = sharedPref.getInt("fullRest",10);

        milisInFuture = (fullRest * 1000)+150;
        final long countDownInterval = 1000;


        fullRestTimer = new CountDownTimer(milisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isPaused || isCanceled) {


                    fullRestTimer.cancel();

                } else {

                    if(tvTimes.getText().toString().equals("00:03") || tvTimes.getText().toString().equals("00:02")){
                        setSound(R.raw.beep_short);
                    }

                    actualState = 3;

                    csbWork.setVisibility(View.INVISIBLE);
                    csbRest.setVisibility(View.VISIBLE);


                    csbRest.setMaximum((int) milisInFuture);
                    csbRest.setProgress((int) millisUntilFinished);


                    tvTitles.setText("REST");
                    tvTitles.setTextColor(getResources().getColor(R.color.colorRest));
                    tvTimes.setTextColor(getResources().getColor(R.color.colorRest));
                    tvTimes.setText("" + String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));


                    reminingTime = millisUntilFinished;

                }
            }

            @Override
            public void onFinish() {

                setSound(R.raw.beep_long);


                workTime();

                sets = 1;
                tvSets.setText(sets+" / "+finalSet);
                csbRest.setProgress(0);
                tvTimes.setText(R.string.zero);

            }

            }.start();
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:

                if (getFragmentManager().getBackStackEntryCount()==0) {

                    SettingsFragment stFragment = new SettingsFragment();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(this, fragmentTransaction, transFragment, stFragment, R.id.fragment_place);
                    fragmentTransactionExtended.addTransition(7);
                    fragmentTransactionExtended.commit();

                }else{

                    getFragmentManager().popBackStack();

                }


                return true;

            case R.id.action_play_again:

                refresh();
                return true;


            default:

                return super.onOptionsItemSelected(item);

        }

    }

    public void refresh(){


        isPaused = true;
        isCanceled = true;

        btStart.setVisibility(View.VISIBLE);
        btPause.setVisibility(View.INVISIBLE);
        btResume.setVisibility(View.INVISIBLE);
        tvFinish.setVisibility(View.INVISIBLE);


        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        int  sharedLaps= sharedPref.getInt("finalLaps",2);
        int  sharedSets= sharedPref.getInt("finalSets",3);
        int ready = sharedPref.getInt("ready",10);
        int work = sharedPref.getInt("work",20);
        int rest = sharedPref.getInt("rest",20);
        int fullRest = sharedPref.getInt("fullRest",20);


        final int totalTimeWork = work  * sharedSets;
        final int totalTimeRest = rest  * sharedSets;
        final int totalTimeRestLaps = fullRest * sharedLaps;
        final int fullTime = (totalTimeWork + totalTimeRestLaps + totalTimeRest + ready - rest - fullRest) * 1000 ;


        //tvTotalTime.setText(""+fullTime);
        tvTotalTime.setText("" + String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(fullTime),
                TimeUnit.MILLISECONDS.toSeconds(fullTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(fullTime))));
        tvLaps.setText(""+laps+" / "+sharedLaps);
        tvSets.setText(""+sets+" / "+sharedSets);
        tvTitles.setText(R.string.ready);
        tvTitles.setTextColor(Color.parseColor("#FFA000"));
        tvTimes.setText("00:00");
        tvTimes.setTextColor(Color.parseColor("#FFA000"));

        csbReady.setMaximum(100);
        csbReady.setProgress(100);

        csbReady.setVisibility(View.VISIBLE);
        csbWork.setVisibility(View.INVISIBLE);
        csbRest.setVisibility(View.INVISIBLE);

    }

    public void setViews(){

        btStart =  findViewById(R.id.btStart);
        btResume =  findViewById(R.id.btResume);
        btPause = findViewById(R.id.btPause);
        tvTimes = findViewById(R.id.tvTimes);
        tvLaps = findViewById(R.id.tvLaps);
        tvSets = findViewById(R.id.tvSets);
        tvTitles = findViewById(R.id.tvTitles);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvFinish = findViewById(R.id.tvFnish);
        csbReady = findViewById(R.id.csbReady);
        csbWork = findViewById(R.id.csbWork);
        csbRest = findViewById(R.id.csbRest);

    }

    public void setSound(int sound){

        MediaPlayer mp = MediaPlayer.create(getBaseContext(), sound);
        mp.start();
    }



}