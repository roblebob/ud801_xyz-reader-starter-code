package com.example.xyzreader.repository.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AppViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Application mApplication;

    public AppViewModelFactory(Application application) {
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create (@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AppViewModel(mApplication);
    }
}
