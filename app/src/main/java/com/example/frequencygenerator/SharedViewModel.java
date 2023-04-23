package com.example.frequencygenerator;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Double> currentFrequency = new MutableLiveData<>();
    private MutableLiveData<Character> waveModeChange = new MutableLiveData<>();

    public MutableLiveData<Double> getCurrentFrequency() {
        return currentFrequency;
    }

    public void setCurrentFrequency(Double currentFrequency) {
        this.currentFrequency.setValue(currentFrequency);
    }

    public MutableLiveData<Character> getWaveModeChange() {
        return waveModeChange;
    }

    public void setWaveModeChange(MutableLiveData<Character> waveModeChange) {
        this.waveModeChange = waveModeChange;
    }
}
