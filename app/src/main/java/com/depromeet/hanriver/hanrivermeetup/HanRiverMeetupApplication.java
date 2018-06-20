package com.depromeet.hanriver.hanrivermeetup;

import android.app.Application;
import android.support.annotation.NonNull;

import com.depromeet.hanriver.hanrivermeetup.Activity.main.MainViewModel;
import com.depromeet.hanriver.hanrivermeetup.datamodel.meeting.ActivityDataModel;
import com.depromeet.hanriver.hanrivermeetup.datamodel.meeting.IActivityDataModel;
import com.depromeet.hanriver.hanrivermeetup.schedulers.ISchedulerProvider;
import com.depromeet.hanriver.hanrivermeetup.schedulers.SchedulerProvider;

public class HanRiverMeetupApplication extends Application {

    @NonNull
    private final IActivityDataModel mActivityDataModel;

    public HanRiverMeetupApplication() {
        mActivityDataModel = new ActivityDataModel();
    }

    @NonNull
    public IActivityDataModel getActivityDataModel() {
        return mActivityDataModel;
    }

    @NonNull
    public ISchedulerProvider getSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }

    @NonNull
    public MainViewModel getMainViewModel() {
        return new MainViewModel(getActivityDataModel(), getSchedulerProvider());
    }
}
