package com.depromeet.hanriver.hanrivermeetup.network;

import com.depromeet.hanriver.hanrivermeetup.model.meeting.MeetingDetail;
import com.depromeet.hanriver.hanrivermeetup.model.timeline.TimeLineVO;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TimelineAPIService {
    @POST("posts")
    Observable<List<TimeLineVO>> getPosts(@Body HashMap<String, Object> body);

    @GET("posts")
    Observable<List<TimeLineVO>> getPosts();

    @POST("post")
    Observable<Response<TimeLineVO>> createPost(@Body TimeLineVO post);
}
