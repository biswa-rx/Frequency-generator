package com.example.frequencygenerator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    Toolbar settingToolbar;
//    Dialog setRangeDialog;

    double rangeFrom = 1f , rangeTo = 22000f;
    SwitchMaterial runBg,showWav,decPrecision,octaveShow;
    TextView setRange,setScale,setStep,previewRange,previewScale,previewStep;

    //range dialog
    TextInputEditText setRangeToEdit,setRangeFromEdit;
    TextView rangeResetTv,rangeCancelTv,rangeOkTv;
    //Pick scale dialog
    RadioGroup pickScaleRadioGroup;
    TextView pickScaleOk,pickScaleCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settingToolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(settingToolbar);
        if(getSupportActionBar() != null){
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//show back button
            getSupportActionBar().setTitle("Setting");
            settingToolbar.setTitleTextAppearance(this,R.style.OrbitronTextAppearance);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

//        setRangeDialog=new Dialog(SettingActivity.this);
//        setRangeDialog.setContentView(R.layout.set_range_dialog);
//        setRangeDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        setRangeDialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(SettingActivity.this,R.drawable.round_corner_dialog));
        initSetting();
    }

    private void initSetting(){
        runBg = findViewById(R.id.run_bg_button);
        showWav = findViewById(R.id.show_wave_animation_button);
        decPrecision = findViewById(R.id.decPrecisionButton);
        octaveShow = findViewById(R.id.octative_button);
        setRange = findViewById(R.id.set_frequency_range);
        setScale = findViewById(R.id.set_frequency_scale);
        setStep = findViewById(R.id.set_step);
        previewRange = findViewById(R.id.frequency_range);
        previewScale = findViewById(R.id.frequency_scale);
        previewStep = findViewById(R.id.step_frequency);

        runBg.setOnClickListener(this);
        showWav.setOnClickListener(this);
        decPrecision.setOnClickListener(this);
        octaveShow.setOnClickListener(this);
        setRange.setOnClickListener(this);
        setScale.setOnClickListener(this);
        setStep.setOnClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.set_frequency_range:
//                setRangeDialog.show();
                showSetRangeDialog();
                break;
            case R.id.set_frequency_scale:
                showPickScaleDialog();
        }
    }

    private void showPickScaleDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.set_scale_dialog, null);
        pickScaleRadioGroup = view.findViewById(R.id.pick_scale_radio_group);
        pickScaleOk = view.findViewById(R.id.pick_scale_ok);
        pickScaleCancel = view.findViewById(R.id.pick_scale_cancel);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pickScaleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        pickScaleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void showSetRangeDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.set_range_dialog, null);

        setRangeFromEdit = view.findViewById(R.id.set_range_from_edit);
        setRangeToEdit = view.findViewById(R.id.set_range_to_edit);
        rangeResetTv = view.findViewById(R.id.set_range_reset);
        rangeCancelTv = view.findViewById(R.id.set_range_cancel);
        rangeOkTv = view.findViewById(R.id.set_range_ok);
        setRangeFromEdit.requestFocus();
//        setRangeFromEdit.addTextChangedListener(this);
//        setRangeToEdit.addTextChangedListener(this);
        rangeOkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setRangeFromEdit.getText() !=null){
                    if(setRangeFromEdit.getText().toString().equals("")){
                        setRangeFromEdit.setError("Field is empty.");
                        return;
                    }
                    rangeFrom = Double.parseDouble(setRangeFromEdit.getText().toString());
                    if (rangeFrom>=1f) {
                        setRangeFromEdit.setError(null); // Clear any previous error message
                    } else {
                        setRangeFromEdit.setError("Value must be greater than 1"); // Show an error message
                        return;
                    }
                }
                if(setRangeToEdit.getText() !=null){
                    if(setRangeToEdit.getText().toString().equals("")){
                        setRangeToEdit.setError("Field is empty.");
                        return;
                    }
                    rangeTo = Double.parseDouble(setRangeToEdit.getText().toString());

                    if (rangeTo<=22000f) {
                        setRangeToEdit.setError(null); // Clear any previous error message
                    } else {
                        setRangeToEdit.setError("Value must be less than 22000"); // Show an error message
                        return;
                    }
                }
                if(setRangeFromEdit.getText() !=null && setRangeToEdit.getText() !=null) {
                    if (rangeFrom <= rangeTo) {
                        setRangeToEdit.setError(null);
                    } else {
                        setRangeFromEdit.setError("'From' value must be less than 'To' value");
                        setRangeToEdit.setError("'To value must be less than 'From' value");
                    }
                }
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        rangeCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        rangeResetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Update shared preference here
                // range from 1 Hz to 22000 Hz
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}