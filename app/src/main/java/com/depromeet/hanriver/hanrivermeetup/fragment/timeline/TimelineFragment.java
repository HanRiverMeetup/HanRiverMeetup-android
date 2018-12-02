package com.depromeet.hanriver.hanrivermeetup.fragment.timeline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.depromeet.hanriver.hanrivermeetup.R;
import com.depromeet.hanriver.hanrivermeetup.common.PreferencesManager;
import com.depromeet.hanriver.hanrivermeetup.fragment.timeline.Adapter.TimeLineAdapter;
import com.depromeet.hanriver.hanrivermeetup.model.meeting.Weather;
import com.depromeet.hanriver.hanrivermeetup.model.timeline.EventVO;
import com.depromeet.hanriver.hanrivermeetup.model.timeline.TimeLineVO;
import com.depromeet.hanriver.hanrivermeetup.service.EventService;
import com.depromeet.hanriver.hanrivermeetup.service.TimelineService;
import com.depromeet.hanriver.hanrivermeetup.service.WeatherService;
import com.github.clans.fab.FloatingActionButton;

import org.reactivestreams.Publisher;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class TimelineFragment extends Fragment {
    TimelineFragment fragment;
    @BindView(R.id.add_post_fab) FloatingActionButton title;
    @BindView(R.id.timeline_scrollview) NestedScrollView mNestedScrollView;
    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.timeline_weather_img) ImageView weather_img;
    @BindView(R.id.timeline_temperature_text) TextView weather_temp;
    @BindView(R.id.timeline_weather_status_text) TextView weather_temp_sub;
    @BindView(R.id.event_pager) ViewPager mEventViewPager;
    @BindView(R.id.page_text) TextView pageTextView;
    @BindView(R.id.timeline_main_text) TextView nickNameText;

    private EventBannerAdapter eventBannerAdapter;

    @OnClick(R.id.add_post_fab)
    public void createPost() {
        CreatePostFragment dialog = CreatePostFragment.newInstance(fragment);
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "modify_meeting");
    }

    @NonNull
    private CompositeDisposable mCompositeDisposable;
    private PublishProcessor<Integer> paginator = PublishProcessor.create();
    private LinearLayoutManager mLayoutManager;
    private TimeLineAdapter mTimeLineAdapter;

    private boolean loading = false;
    private int pageNumber = 0;
    private final int VISIBLE_THRESHOLD = 1;
    private int lastVisibleItem, totalItemCount;

    private final String[] skyState = {"","맑음","구름조금","구름많음","흐림"};
    private final int[] skyState_img = {0,R.drawable.ic_weather_sunny,R.drawable.ic_weather_alittlecloudy,R.drawable.ic_weather_muchcloudy,R.drawable.ic_weather_fog};
    private final int[] rainState_img = {0,R.drawable.ic_weather_rain,R.drawable.ic_weather_snowrain,R.drawable.ic_weather_snow};

    public TimelineFragment() {
        this.fragment = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceSatate) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTimeLineAdapter = new TimeLineAdapter(getActivity());
        mRecyclerView.setAdapter(mTimeLineAdapter);
        nickNameText.setText(PreferencesManager.getNickname()+" 님\n한강소식을 알볼까요?");

        return view;
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
                .delay(1, TimeUnit.SECONDS)
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
                .doOnNext(res->{
                    if (res.code() == HttpsURLConnection.HTTP_OK) {
                        setWeather(res.body());
                    } else {
                        Toast.makeText(getContext(), "날씨정보 호출 과정에서 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribe());

        mCompositeDisposable.add(EventService.getInstance().getEvents()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setEvents));
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

    private void setEvents(@NonNull final Response<List<EventVO>> events){
        eventBannerAdapter = new EventBannerAdapter(this.getActivity(), events.body());

        mEventViewPager.setAdapter(eventBannerAdapter);
        mEventViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                pageTextView.setText((position + 1) + "/" + events.body().size() + " +");
            }
        });

        pageTextView.setText(1 + "/" + events.body().size() + " +");
    }
}
