package com.depromeet.hanriver.hanrivermeetup.service;

import com.depromeet.hanriver.hanrivermeetup.model.meeting.Comment;
import com.depromeet.hanriver.hanrivermeetup.model.meeting.MatchingDetail;
import com.depromeet.hanriver.hanrivermeetup.model.meeting.MeetingDetail;
import com.depromeet.hanriver.hanrivermeetup.model.timeline.TimeLineVO;
import com.depromeet.hanriver.hanrivermeetup.network.APIUtiles;
import com.depromeet.hanriver.hanrivermeetup.network.TimelineAPIService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class TimelineService {
    private static final TimelineService ourInstance = new TimelineService();
    private TimelineAPIService mService;

    public static TimelineService getInstance() {
        return ourInstance;
    }

    private TimelineService() { }

    public void setService(String token, String id) {
        mService = APIUtiles.getTimelineService(token, id);
    }

    public Observable<List<TimeLineVO>> getPosts(Date date, int offset, int limit){
        HashMap<String, Object> jsonBody = new HashMap<String, Object>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        jsonBody.put("date", sdf.format(date));
        jsonBody.put("offset", offset);
        jsonBody.put("limit", limit);

        return mService.getPosts(jsonBody)
                .subscribeOn(Schedulers.io())
                .map(it -> it);
    }

    public Observable<Response<TimeLineVO>> createPost(TimeLineVO post) {
        return mService.createPost(post).subscribeOn(Schedulers.io())
                .doOnNext( res -> {
                    if(res.code() == HttpsURLConnection.HTTP_OK){
                        res.body();
                    }
                });
    }
}
