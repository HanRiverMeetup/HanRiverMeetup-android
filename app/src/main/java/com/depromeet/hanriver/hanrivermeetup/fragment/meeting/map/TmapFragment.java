package com.depromeet.hanriver.hanrivermeetup.fragment.meeting.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.depromeet.hanriver.hanrivermeetup.BuildConfig;
import com.depromeet.hanriver.hanrivermeetup.R;
import com.depromeet.hanriver.hanrivermeetup.model.map.MapMarker;
import com.depromeet.hanriver.hanrivermeetup.model.map.TourEventInfo;
import com.depromeet.hanriver.hanrivermeetup.service.EventService;
import com.depromeet.hanriver.hanrivermeetup.service.MapService;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TmapFragment extends Fragment {

    private RelativeLayout map;
    private TMapView mapView;
    private TMapGpsManager gpsManager;
    private TabLayout tabLayout;
    private int current_position = 0;
    private View tabs[] = new View[3];
    private TextView tabname[] = new TextView[3];
    private CompositeDisposable mCompositeDisposable;
    private Bitmap bitmap;
    private ViewPager mViewPager;
    TMapMarkerItem[] cf_markerItem;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mCompositeDisposable = new CompositeDisposable();
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        setupViews(view);

        map = view.findViewById(R.id.mapView);
        gpsManager = new TMapGpsManager(getActivity());
        mapView = new TMapView(getActivity());
        mapView.setSKTMapApiKey(BuildConfig.TMapApiKey);
//        mapView.setCenterPoint(126.930632, 37.529930);
        mapView.setCenterPoint(127.0029794866, 37.5811724288);
        mapView.setCompassMode(false);
        mapView.setIconVisibility(true);
        mapView.setZoomLevel(15);
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);  //일반지도
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        mapView.setTrackingMode(false);
        mapView.setSightVisible(false);

        map.addView(mapView);

        mViewPager = view.findViewById(R.id.map_viewPager);

//        mCardAdapter = new CardPagerAdapter(getContext());
//        mCardAdapter.addCardItem(new CardItem("test", "hello"));
//        mCardAdapter.addCardItem(new CardItem("test2", "hello2"));
//        mCardAdapter.addCardItem(new CardItem("test3", "hello3"));
//        mCardAdapter.addCardItem(new CardItem("test4", "hello4"));

//        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
//
//        mViewPager.setAdapter(mCardAdapter);
//        mViewPager.setPageTransformer(false, mCardShadowTransformer);
//        mViewPager.setOffscreenPageLimit(3);

        return view;
    }

    private void setupViews(View v) {
        tabLayout = v.findViewById(R.id.map_tablayout);

        for (int i = 0; i < 3; i++) {
            tabs[i] = getLayoutInflater().inflate(R.layout.tab_meeting_list, null);
            tabname[i] = tabs[i].findViewById(R.id.meeting_list_tab_name);
            tabname[i].setTextColor(getResources().getColor(R.color.greyish));
            tabLayout.addTab(tabLayout.newTab().setCustomView(tabs[i]));
        }

        tabname[0].setText("행사정보");
        tabname[1].setText("편의점");
        tabname[2].setText("화장실");
        tabname[0].setTextColor(getResources().getColor(R.color.clear_blue));

        tabLayout.setOverScrollMode(View.OVER_SCROLL_NEVER);
        tabLayout.setTabRippleColor(null);
        initTablayoutWeight(tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                current_position = tab.getPosition();
                tabname[tab.getPosition()].setTextColor(getResources().getColor(R.color.clear_blue));

                if(current_position == 1) {
                    mapView.setCenterPoint(126.930632, 37.526930);
                    mapView.setZoomLevel(15);
                }
                else if(current_position == 2) {
                    mapView.setCenterPoint(126.930632, 37.526930);
                    mapView.setZoomLevel(15);
                }
                else {
//                    mapView.setCenterPoint(127.0029794866, 37.5811724288);
                    mapView.setCenterPoint(127.002979, 37.579172);
                    mapView.setZoomLevel(15);
                }

                bind();

                if(current_position==1||current_position==2){
                    mViewPager.setVisibility(View.GONE);
                }
                else{
                    mViewPager.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabname[tab.getPosition()].setTextColor(getResources().getColor(R.color.greyish));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
    }

    @Override
    public void onPause() {
        super.onPause();
        unBind();
    }

    private void bind() {
        mCompositeDisposable = new CompositeDisposable();
        if(current_position == 0) {
            mCompositeDisposable.add(EventService.getInstance().getRecentlyTourEvents()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(res -> {
                        if (res.code() == HttpsURLConnection.HTTP_OK) {
                            setTourEventMarker(res.body());
                        } else {
                            Toast.makeText(getContext(), "이벤트 정보 로딩 오류", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .subscribe());
        }
        else {
            mCompositeDisposable.add(MapService.getInstance().getMarkerList(current_position + 1)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(res -> {
                        if (res.code() == HttpsURLConnection.HTTP_OK) {
                            setMarker(res.body());
                        } else {
                            Toast.makeText(getContext(), "마커 정보 로딩 오류", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .subscribe());
        }
    }

    private void unBind() {
        mCompositeDisposable.clear();
    }

    private void setTourEventMarker(@NonNull final List<TourEventInfo> markers) {
        mapView.removeAllMarkerItem();
        cf_markerItem = new TMapMarkerItem[markers.size()];
        TMapPoint mapPoint;

        for (int i = 0; i < markers.size(); i++) {
            cf_markerItem[i] = new TMapMarkerItem();
            mapPoint = new TMapPoint(markers.get(i).getMapy(), markers.get(i).getMapx());
            cf_markerItem[i].setTMapPoint(mapPoint);
            cf_markerItem[i].setVisible(TMapMarkerItem.VISIBLE);
            // markerItem[i].setID(markers.get(i).getMap_seq());
            cf_markerItem[i].setID(String.valueOf(i));

            ///////////////////Marker Click logic///////////////////////

            //풍선뷰 이미지 bitmap으로 저장
            bitmap = BitmapFactory.decodeResource(getContext().getResources(), android.R.color.transparent);

            // 풍선뷰 안의 항목 세팅

            cf_markerItem[i].setCalloutTitle(markers.get(i).getTitle());
            cf_markerItem[i].setCanShowCallout(true);
            cf_markerItem[i].setAutoCalloutVisible(false);
            cf_markerItem[i].setCalloutRightButtonImage(bitmap);

            mapView.addMarkerItem(cf_markerItem[i].getID(), cf_markerItem[i]);

            //풍선뷰 클릭 이벤트
            mapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                @Override
                public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                    int position = Integer.valueOf(tMapMarkerItem.getID());
                    Toast.makeText(getContext(), markers.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                }
            });
        }

        setCardList(markers);
    }

    private void setMarker(@NonNull final List<MapMarker> markers) {
        mapView.removeAllMarkerItem();
        TMapMarkerItem[] markerItem = new TMapMarkerItem[markers.size()];
        TMapPoint mapPoint;

        for (int i = 0; i < markers.size(); i++) {
            markerItem[i] = new TMapMarkerItem();
            mapPoint = new TMapPoint(Double.parseDouble(markers.get(i).getLat()), Double.parseDouble(markers.get(i).getLng()));
            markerItem[i].setTMapPoint(mapPoint);
            markerItem[i].setVisible(TMapMarkerItem.VISIBLE);
            // markerItem[i].setID(markers.get(i).getMap_seq());
            markerItem[i].setID(String.valueOf(i));

            ///////////////////Marker Click logic///////////////////////


          //  bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher);


            // 풍선뷰 안의 항목 세팅
//            markerItem[i].setCalloutTitle(markers.get(i).getLat());
//            markerItem[i].setCalloutSubTitle("test");
//            markerItem[i].setCanShowCallout(true);
//            markerItem[i].setAutoCalloutVisible(false);
//            markerItem[i].setCalloutRightButtonImage(bitmap);



            mapView.addMarkerItem(markerItem[i].getID(), markerItem[i]);

            mapView.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {
                @Override
                public void onCalloutMarker2ClickEvent(String id, TMapMarkerItem2 tMapMarkerItem2) {
                    Toast.makeText(getContext(), "test " + id, Toast.LENGTH_SHORT).show();
                }
            });

            //풍선뷰 클릭 이벤트
            mapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                @Override
                public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                    int position = Integer.valueOf(tMapMarkerItem.getID());
                    Toast.makeText(getContext(), markers.get(position).getLat(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setCardList(List<TourEventInfo> markers){
        mCardAdapter = new CardPagerAdapter();
        for(int i=0;i<markers.size();i++) {
            mCardAdapter.addCardItem(new CardItem(markers.get(i).getTitle(), markers.get(i).getAddr(),markers.get(i).getHompage(),markers.get(i).getTel(),markers.get(i).getImage()));
        }

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        mapView.setCenterPoint(markers.get(0).getMapx(),markers.get(0).getMapy()-0.003);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mapView.setCenterPoint(markers.get(i).getMapx(),markers.get(i).getMapy()-0.003);
                mapView.setZoom(15);
                cf_markerItem[i].setAutoCalloutVisible(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void initTablayoutWeight(TabLayout tablayout) {
        LinearLayout linearLayout = (LinearLayout) tablayout.getChildAt(0);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int diff = display.getWidth() - (int) (tabname[0].getTextSize() * 10); //최대 가로 크기 - 3개 탭의 크기
        diff = diff / 6;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View vv = linearLayout.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) vv.getLayoutParams();
            params.weight = 0;
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.leftMargin = diff;
            params.rightMargin = diff;
            vv.setLayoutParams(params);
        }
    }

}