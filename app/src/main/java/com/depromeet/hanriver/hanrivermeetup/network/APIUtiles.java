package com.depromeet.hanriver.hanrivermeetup.network;

import com.depromeet.hanriver.hanrivermeetup.BuildConfig;
import com.depromeet.hanriver.hanrivermeetup.fragment.mypage.MyPageTabAdapter;
import com.depromeet.hanriver.hanrivermeetup.model.map.MapMarker;

public class APIUtiles {
    private APIUtiles() { }

    private static final String BASE_URL = BuildConfig.APIServerBaseURL;
    public static final String HOST_API_URL = BASE_URL + "host/";
    public static final String GUEST_API_URL = BASE_URL + "guest/";
    public static final String COMM_API_URL = BASE_URL + "comm/";
    public static final String ACCESS_API_URL = BASE_URL + "access/";
    public static final String MYPAGE_API_URL = BASE_URL + "mypage/";
    public static final String TIMELINE_API_URL = BASE_URL + "timeLine/";
    public static final String WEATHER_API_URL = BASE_URL + "weather/";
    public static final String MAP_API_URL = BASE_URL + "mapMarker/";
    public static final String NOTIFICATION_API_URL = BASE_URL + "notificationLog/";
    public static final String EVENT_API_URL = BASE_URL + "event/";
    public static final String FACEBOOK_API_URL = "https://graph.facebook.com/";

    public static LoginAPIService getLoginService(){
        return RetrofitClient.getClient(ACCESS_API_URL).create(LoginAPIService.class);
    }

    public static FacebookAPIService getFacebookService(){
        return RetrofitClient.getClient(FACEBOOK_API_URL).create(FacebookAPIService.class);
    }


    public static HostAPIService getHostService(String accessToken, String id){
        return RetrofitClient.getClientWithToken(HOST_API_URL, accessToken, id).create(HostAPIService.class);
    }

    public static GuestAPIService getGuestService(String accessToken, String id){
        return RetrofitClient.getClientWithToken(GUEST_API_URL, accessToken, id).create(GuestAPIService.class);
    }

    public static CommunicationAPIService getCommunicationService(String accessToken, String id){
        return RetrofitClient.getClientWithToken(COMM_API_URL, accessToken, id).create(CommunicationAPIService.class);
    }

    public static MyPageAPIService getMyPageService(String accessToken, String id){
        return RetrofitClient.getClientWithToken(MYPAGE_API_URL, accessToken, id).create(MyPageAPIService.class);
    }

    public static TimelineAPIService getTimelineService(String accessToken, String id){
        return RetrofitClient.getClientWithToken(TIMELINE_API_URL, accessToken, id).create(TimelineAPIService.class);
    }

    public static WeatherAPIService getWeatherService(String accessToken, String id){
        return RetrofitClient.getClientWithToken(WEATHER_API_URL, accessToken, id).create(WeatherAPIService.class);
    }

    public static MapAPISerivce getMapService(String accessToken, String id){
        return RetrofitClient.getClientWithToken(MAP_API_URL,accessToken,id).create(MapAPISerivce.class);
    }

    public static NotificationAPIService getNotificationService(String accessToken, String id){
        return RetrofitClient.getClientWithToken(NOTIFICATION_API_URL, accessToken, id).create(NotificationAPIService.class);
    }

    public static EventAPIService getEventService(String accessToken, String id) {
        return RetrofitClient.getClientWithToken(EVENT_API_URL, accessToken, id).create(EventAPIService.class);
    }
}