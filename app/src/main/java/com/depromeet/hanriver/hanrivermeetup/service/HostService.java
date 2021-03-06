package com.depromeet.hanriver.hanrivermeetup.service;

import com.depromeet.hanriver.hanrivermeetup.model.mypage.ApplicantVO;
import com.depromeet.hanriver.hanrivermeetup.model.mypage.Tab1VO;
import com.depromeet.hanriver.hanrivermeetup.model.meeting.CreateRoom;
import com.depromeet.hanriver.hanrivermeetup.model.meeting.MeetingDetail;
import com.depromeet.hanriver.hanrivermeetup.network.APIUtiles;
import com.depromeet.hanriver.hanrivermeetup.network.HostAPIService;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HostService {
    private static final HostService ourInstance = new HostService();
    private HostAPIService mService;

    public static HostService getInstance() {
        return ourInstance;
    }

    private HostService() { }

    public void setService(String token, String id) {
        mService = APIUtiles.getHostService(token, id);
    }

    public Observable<List<ApplicantVO>> getMeetingApplicants(int meeting_seq){
        return mService.getMeetingApplicants(meeting_seq)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MeetingDetail>> getTodayList(){
        return mService.getMeetingsOnToday()
                .subscribeOn(Schedulers.io());
    }

    public Observable<Response<MeetingDetail>> getMeetingDetail(int meeting_seq){
        return mService.getMeetingDetail(meeting_seq).subscribeOn(Schedulers.io())
                .doOnNext(res -> {
                    if(res.code() == HttpsURLConnection.HTTP_OK){
                        res.body();
                    }
                });
    }

    public Observable<Response<MeetingDetail>> createMeeting(MeetingDetail createRoom){
        return mService.createMeeting(createRoom).subscribeOn(Schedulers.io())
                .doOnNext( res -> {
                    if(res.code() == HttpsURLConnection.HTTP_OK){
                        res.body();
                    }
                });
    }

    public Observable<Response<MeetingDetail>> modifyMeeting(int meeting_seq ,MeetingDetail meetingDetail){
        return mService.modifyMeeting(meeting_seq, meetingDetail)
                .subscribeOn(Schedulers.io())
                .doOnNext(res -> {
                    if (res.code() == HttpsURLConnection.HTTP_OK) {
                        res.body();
                    }
                });
    }

    public Observable<Response<List<MeetingDetail>>> getWeekList(int activity_seq){
        return mService.getMeetingsOnWeek(activity_seq)
                .subscribeOn(Schedulers.io())
                .doOnNext(res -> {
                    if(res.code() == HttpsURLConnection.HTTP_OK){
                        res.body();
                    }
                });
    }
}