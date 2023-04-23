package com.example.frequencygenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = "MainActivity";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Float translationX = 100f;
    Float translationY = 100f;
    Boolean isMenuOpen = false;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    FloatingActionButton audioBalanceFv,volumeLevelFv,favPlusFv,waveTypeFv,timerFv,pickNoteFv,sweepGeneratorFv,playButtonFv,plusButtonFv,minusButtonFv,twoIntoFv,twoDivFv;
    SeekBar seekBar;
    float defaultFrequency = 350.00f;
    TextView startFreqTv,endFreqTv,volumeLevelTv;
    EditText currentFrequencyEt;

    int minFrequency = 0,maxFrequency = 22000;

    //Data for foreground service
    public static String waveType = "SINE_WAVE";
    public static float currentFrequency = 1000;
    public static boolean FrequencyRefreshFlag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//show back button
            getSupportActionBar().setTitle("");
        }
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        initFabMenu();
        initActivity();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentFrequency = progress;
                FrequencyRefreshFlag = true;
                currentFrequencyEt.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.opt_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_frequency_preset:
                Toast.makeText(this,"save selected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.export_frequency:
                Toast.makeText(this,"export_frequency selected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.load_preset:
                Toast.makeText(this,"load_preset selected",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                startActivity(new Intent(this,SettingActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.theme:
                break;
            case R.id.rate:
                break;
            case R.id.feedback:
                break;
            case R.id.share:
                break;
            case R.id.policy:
                break;
        }
        return true;
    }

    private void initFabMenu() {
        favPlusFv = findViewById(R.id.plus_sign);
        timerFv = findViewById(R.id.floatingActionButton6);
        pickNoteFv = findViewById(R.id.floatingActionButton7);
        sweepGeneratorFv = findViewById(R.id.floatingActionButton8);

        timerFv.setAlpha(0f);
        pickNoteFv.setAlpha(0f);
        sweepGeneratorFv.setAlpha(0f);

        timerFv.setTranslationY(translationY);
        pickNoteFv.setTranslationY(translationY);
        pickNoteFv.setTranslationX(translationX);
        sweepGeneratorFv.setTranslationX(translationX);

        timerFv.setRotation(-200f);
        pickNoteFv.setRotation(-200f);
        sweepGeneratorFv.setRotation(-200f);

        favPlusFv.setOnClickListener(this);
        timerFv.setOnClickListener(this);
        pickNoteFv.setOnClickListener(this);
        sweepGeneratorFv.setOnClickListener(this);
    }
    private void initActivity(){
        audioBalanceFv = findViewById(R.id.audio_balance);
        volumeLevelFv = findViewById(R.id.volume_control);
        waveTypeFv = findViewById(R.id.wave_type);
        playButtonFv = findViewById(R.id.play_button_Fv);
        plusButtonFv = findViewById(R.id.plus_button_Fv);
        minusButtonFv = findViewById(R.id.minus_button_Fv);
        twoDivFv = findViewById(R.id.div2_button_Fv);
        twoIntoFv = findViewById(R.id.into2_button_Fv);

        seekBar = findViewById(R.id.seekBar2);
        seekBar.setMax(20000); // set maximum frequency to 20kHz
        seekBar.setProgress((int) defaultFrequency); // set default frequency on seek bar

        volumeLevelTv = findViewById(R.id.textView4);
        startFreqTv = findViewById(R.id.textView2);
        endFreqTv = findViewById(R.id.textView3);

        currentFrequencyEt = findViewById(R.id.editTextNumberDecimal);

        audioBalanceFv.setOnClickListener(this);
        volumeLevelFv.setOnClickListener(this);
        waveTypeFv.setOnClickListener(this);
        playButtonFv.setOnClickListener(this);
        plusButtonFv.setOnClickListener(this);
        minusButtonFv.setOnClickListener(this);
        twoDivFv.setOnClickListener(this);
        twoIntoFv.setOnClickListener(this);
    }
    private void openMenu() {
        isMenuOpen = !isMenuOpen;
        favPlusFv.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.theme)));
        favPlusFv.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

        timerFv.animate().translationY(0f).alpha(1f).rotation(0f).setInterpolator(interpolator).setDuration(600).start();
        pickNoteFv.animate().translationY(0f).translationX(0f).alpha(1f).rotation(0f).setInterpolator(interpolator).setDuration(600).start();
        sweepGeneratorFv.animate().translationX(0f).alpha(1f).rotation(0f).setInterpolator(interpolator).setDuration(600).start();
    }

    private void closeMenu() {
        isMenuOpen = !isMenuOpen;
        favPlusFv.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.backgroundLite)));
        favPlusFv.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

        timerFv.animate().translationY(translationY).alpha(0f).rotation(-200f).setInterpolator(interpolator).setDuration(600).start();
        pickNoteFv.animate().translationY(translationY).translationX(translationX).alpha(0f).rotation(-200f).setInterpolator(interpolator).setDuration(600).start();
        sweepGeneratorFv.animate().translationX(translationX).alpha(0f).rotation(-200f).setInterpolator(interpolator).setDuration(600).start();
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plus_sign:
                //plus button
                Log.i(TAG, "onClick: fab main");
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            case R.id.floatingActionButton6:
                //timerFv
                Log.i(TAG, "onClick: timerFv");
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            case R.id.floatingActionButton7:
                //pick note
                Log.i(TAG, "onClick: pick note");
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            case R.id.floatingActionButton8:
                //sweep generator
                Log.i(TAG, "onClick: sweep generator");
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            case R.id.wave_type:
                Log.i(TAG, "onClick: wave type");
                waveTypeOptionDialog();
                break;
            case R.id.audio_balance:
                Log.i(TAG, "onClick: audio balance");
                break;
            case R.id.volume_control:
                Log.i(TAG, "onClick: volume control");
                break;
            case R.id.play_button_Fv:
                Log.i(TAG, "onClick: play button");
                startWaveGeneratorService();
                break;
            case R.id.plus_button_Fv:
                Log.i(TAG, "onClick: plus button");
                stopWaveGeneratorService();
                break;
            case R.id.minus_button_Fv:
                Log.i(TAG, "onClick: minus button");
                break;
            case R.id.into2_button_Fv:
                Log.i(TAG, "onClick: into two button");
                break;
            case R.id.div2_button_Fv:
                Log.i(TAG, "onClick: div two button");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private void waveTypeOptionDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.pick_wave_dialog, null);

        LinearLayout sineWaveLL,squareWaveLL,sawtoothWaveLL,triangleWaveLL;
        sineWaveLL = view.findViewById(R.id.llSine);
        squareWaveLL = view.findViewById(R.id.llSquare);
        sawtoothWaveLL = view.findViewById(R.id.llSawtooth);
        triangleWaveLL = view.findViewById(R.id.llTriangle);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

    public void startWaveGeneratorService(){
        int waveFrequency = 450;
        Log.d(TAG, "startWaveGeneratorService: ");
        Intent serviceIntent = new Intent(this,WaveGeneratorService.class);
        serviceIntent.putExtra("wave_frequency",waveFrequency);
        startForegroundService(serviceIntent);
    }

    public void stopWaveGeneratorService(){
        Intent serviceIntent = new Intent(this,WaveGeneratorService.class);
        stopService(serviceIntent);
    }
}