package com.depromeet.hanriver.hanrivermeetup.fragment.timeline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.depromeet.hanriver.hanrivermeetup.HanRiverMeetupApplication;
import com.depromeet.hanriver.hanrivermeetup.R;
import com.depromeet.hanriver.hanrivermeetup.fragment.timeline.Adapter.TimeLineAdapter;
import com.depromeet.hanriver.hanrivermeetup.model.meeting.Weather;
import com.depromeet.hanriver.hanrivermeetup.model.timeline.TimeLineVO;
import com.depromeet.hanriver.hanrivermeetup.service.TimelineService;
import com.depromeet.hanriver.hanrivermeetup.service.WeatherService;

import org.reactivestreams.Publisher;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

public class TimelineFragment extends Fragment {

    private PublishProcessor<Integer> paginator = PublishProcessor.create();

    @NonNull
    private CompositeDisposable mCompositeDisposable;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TimeLineAdapter mTimeLineAdapter;

    private boolean loading = false;
    private int pageNumber = 0;
    private final int VISIBLE_THRESHOLD = 1;
    private int lastVisibleItem, totalItemCount;
    private ImageView weather_img;
    private TextView weather_temp,weather_temp_sub;
    private final String[] skyState = {"","맑음","구름조금","구름많음","흐림"};
    private final int[] skyState_img = {0,R.drawable.ic_weather_sunny,R.drawable.ic_weather_alittlecloudy,R.drawable.ic_weather_muchcloudy,R.drawable.ic_weather_fog};
    private final int[] rainState_img = {0,R.drawable.ic_weather_rain,R.drawable.ic_weather_rain_snow,R.drawable.ic_weather_snow};

    public TimelineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    NestedScrollView mNestedScrollView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceSatate) {
        mNestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_timeline, container, false);
        mRecyclerView = mNestedScrollView.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTimeLineAdapter = new TimeLineAdapter(getActivity());
        mRecyclerView.setAdapter(mTimeLineAdapter);
        weather_img = mNestedScrollView.findViewById(R.id.timeline_weather_img);
        weather_temp = mNestedScrollView.findViewById(R.id.timeline_temperature_text);
        weather_temp_sub = mNestedScrollView.findViewById(R.id.timeline_weather_status_text);

        return mNestedScrollView;
    }

    private void setUpLoadMoreListener() {
        mNestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            totalItemCount = mLayoutManager.getItemCount();
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

            if (!loading
                    && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                pageNumber++;
                paginator.onNext(pageNumber);
                loading = true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
        setUpLoadMoreListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        unBind();
    }

    private Flowable<List<TimeLineVO>> dataFromNetwork(final int page) {
        return Flowable.just(true)
                .delay(2, TimeUnit.SECONDS)
                .map(value -> {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Date date = new Date();
                    try {
                        date = format.parse("2018-09-07");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return TimelineService.getInstance().getPosts(
                            date,
                            pageNumber * 2,
                            2).blockingSingle();
                });
    }

    private void bind() {
        pageNumber = 0;
        mTimeLineAdapter.clear();
        mTimeLineAdapter.notifyDataSetChanged();

        Disposable disposable = paginator
                .onBackpressureDrop()
                .concatMap((Function<Integer, Publisher<List<TimeLineVO>>>) page -> {
                    loading = true;
                    return dataFromNetwork(page);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    mTimeLineAdapter.addItems(items);
                    mTimeLineAdapter.notifyDataSetChanged();
                    loading = false;
                });

        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposable);
        paginator.onNext(pageNumber);

        mCompositeDisposable.add(WeatherService.getInstance().getWeather()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setWeather));
    }

    private void unBind() {
        mCompositeDisposable.clear();
    }

    private void setWeather(@NonNull final Weather weather){
        weather_temp.setText(weather.getT1h().toString());
        weather_temp_sub.setText(skyState[Integer.parseInt(weather.getSky())]+"\n" + weather.getTmn()+"℃ / " + weather.getTmx()+"℃");
        if (Integer.parseInt(weather.getPty()) != 0) {
            weather_img.setImageResource(rainState_img[Integer.parseInt(weather.getPty())]);
        }
        else{
            weather_img.setImageResource(skyState_img[Integer.parseInt(weather.getSky())]);
        }
    }
}
