package com.depromeet.hanriver.hanrivermeetup.fragment.meeting.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.depromeet.hanriver.hanrivermeetup.service.MapService;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

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
    Bitmap bitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mapView.setCenterPoint(126.930632, 37.529930);
        mapView.setCompassMode(false);
        mapView.setIconVisibility(true);
        mapView.setZoomLevel(15);
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);  //일반지도
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        mapView.setTrackingMode(false);
        mapView.setSightVisible(false);

        map.addView(mapView);

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
        tabname[0].setText("휴지통");
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
                bind();

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

    private void unBind() {
        mCompositeDisposable.clear();
    }

    private void setMarker(@NonNull final List<MapMarker> markers) {
        mapView.removeAllMarkerItem();
//        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.app_logo);
        TMapMarkerItem[] markerItem = new TMapMarkerItem[markers.size()];
        TMapPoint mapPoint;
        for (int i = 0; i < markers.size(); i++) {
            markerItem[i] = new TMapMarkerItem();
            mapPoint = new TMapPoint(Double.parseDouble(markers.get(i).getLat()), Double.parseDouble(markers.get(i).getLng()));
            markerItem[i].setTMapPoint(mapPoint);
            markerItem[i].setVisible(TMapMarkerItem.VISIBLE);
            markerItem[i].setID(markers.get(i).getMap_seq());
            mapView.addMarkerItem(markerItem[i].getID(), markerItem[i]);

        }
    }

    private void initTablayoutWeight(TabLayout tablayout) {
        LinearLayout linearLayout = (LinearLayout) tablayout.getChildAt(0);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int diff = display.getWidth() - (int) (tabname[0].getTextSize() * 9); //최대 가로 크기 - 3개 탭의 크기
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
