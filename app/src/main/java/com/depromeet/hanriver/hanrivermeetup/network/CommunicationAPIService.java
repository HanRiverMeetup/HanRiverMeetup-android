package com.depromeet.hanriver.hanrivermeetup.network;

import com.depromeet.hanriver.hanrivermeetup.model.meeting.Comment;
import com.depromeet.hanriver.hanrivermeetup.model.meeting.MatchingDetail;
import com.depromeet.hanriver.hanrivermeetup.model.meeting.MeetingDetail;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommunicationAPIService {
    @GET("comments/{meetingID}")
    Comment getComments(@Path("meetingID") int meetingID);

    @POST("comment/")
    MeetingDetail addComment(@Body Comment comment);

    @DELETE("comment/{commentID}")
    boolean deleteComment(@Path("commentID") int commentID);

    @POST("match/")
    MatchingDetail match(@Body MatchingDetail matchingDetail);

    @GET("match/{contactID}")
    MatchingDetail getMatchingDetail(@Path("contactID") int contactID);

    @POST("match/find")
    MatchingDetail getMatchingDetails(@Body MatchingDetail matchingDetail);

    @DELETE("match/{contactID}")
    boolean cancleMatch(@Path("contactID") int contactID);
}