package com.depromeet.hanriver.hanrivermeetup.datamodel.meeting.ListFragment;

import android.support.annotation.NonNull;

import com.depromeet.hanriver.hanrivermeetup.R;
import com.depromeet.hanriver.hanrivermeetup.datamodel.meeting.ListFragment.IMeetingListInnerDataModel;
import com.depromeet.hanriver.hanrivermeetup.model.meeting.MeetingDetail;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

public class MeetingListInnerDataModel implements IMeetingListInnerDataModel {
    @NonNull

    @Override
    public Observable<List<MeetingDetail>> getAvailableRooms() {
        return Observable.fromCallable(this::getRooms);
    }

    @NonNull
    private List<MeetingDetail> getRooms() {

        return Arrays.asList(new MeetingDetail(0,0,"123123123","여의도 한강공원에서 놀아요",3,"여의도","12:00",30000,"12:00","12:00","한강 공원 가자","01012341234",0,"nick"),
                new MeetingDetail(0,0,"123123123","여의도 한강공원에서 놀아요",3,"여의도","12:00",30000,"12:00","12:00","한강 공원 가자","01012341234",0,"nick"));
//                new MeetingDetail(R.drawable.ic_chicken_icon,"같이 놀아요!","여의도 한강공원","12:00",5,20000,"같이 재밌게 놀아요 ~~~","010-0000-0000"),
//                new MeetingDetail(R.drawable.ic_chicken_icon,"같이 놀아요!","여의도 한강공원","12:00",5,20000,"같이 재밌게 놀아요 ~~~","010-0000-0000"),
//                new MeetingDetail(R.drawable.ic_chicken_icon,"같이 놀아요!","여의도 한강공원","12:00",5,20000,"같이 재밌게 놀아요 ~~~","010-0000-0000"),
//                new MeetingDetail(R.drawable.ic_chicken_icon,"같이 놀아요!","여의도 한강공원","12:00",5,20000,"같이 재밌게 놀아요 ~~~","010-0000-0000"));
////                .asList(new Activity("자전거타기", "자전거를 같이 타요!"),
////                        new Activity("산책하기", "한강을 거닐어요"),
////                        new Activity("오리배타기", "오리배를 같이 타요!"));
//        return null;
    }
}
