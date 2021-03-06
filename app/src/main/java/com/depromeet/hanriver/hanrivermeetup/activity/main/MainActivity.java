package com.depromeet.hanriver.hanrivermeetup.activity.main;

import android.Manifest;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.depromeet.hanriver.hanrivermeetup.HanRiverMeetupApplication;
import com.depromeet.hanriver.hanrivermeetup.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    @NonNull
    private static TabLayout tabLayout;

    @NonNull
    private static NonSwipeableViewPager viewPager;

    private int imgIcon[] = {
            R.drawable.ic_nolgang_icon,
            R.drawable.ic_timeline_icon,
            R.drawable.ic_tap_map,
            R.drawable.ic_mypage_icon
    };
    private int imgIcon_act[] = {
            R.drawable.ic_nolgang_icon_active,
            R.drawable.ic_timeline_icon_active,
            R.drawable.ic_tap_map_active,
            R.drawable.ic_mypage_icon_active
    };
    private String tabText[] = {
            "같이놀강",
            "지금한강",
            "어디로강",
            "마이한강",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);


        //Permission Check
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("모임 연결을 위해선 권한이 필요합니다.")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요!")
                .setPermissions(Manifest.permission.CALL_PHONE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        // Initializing the TabLayout
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setTabRippleColor(null);

        View tabItems[] = new View[4];
        for(int i=0; i<tabItems.length;i++) {
            tabItems[i] = getLayoutInflater().inflate(R.layout.main_tab_icon, null);
            ImageView tabItemImg = tabItems[i].findViewById(R.id.main_tab_img);
            TextView tabItemText = tabItems[i].findViewById(R.id.main_tab_text);

            if(i!=0) {
                tabItemImg.setImageResource(imgIcon[i]);
                tabItemText.setText(tabText[i]);
                tabItemText.setTextColor(Color.parseColor("#333333"));
            }

            tabLayout.addTab(tabLayout.newTab().setCustomView(tabItems[i]));
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager
        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Creating TabPagerAdapter adapter
        viewPager.setAdapter(new MainTabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int x = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());

                View view = tab.getCustomView();
                ImageView img = view.findViewById(R.id.main_tab_img);
                TextView text = view.findViewById(R.id.main_tab_text);

                img.setImageResource(imgIcon_act[tab.getPosition()]);
                text.setText(tabText[tab.getPosition()]);
                text.setTextColor(Color.parseColor("#2186f8"));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                View view = tab.getCustomView();
                ImageView img = view.findViewById(R.id.main_tab_img);
                TextView text = view.findViewById(R.id.main_tab_text);

                img.setImageResource(imgIcon[tab.getPosition()]);
                text.setText(tabText[tab.getPosition()]);
                text.setTextColor(Color.parseColor("#333333"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }



    public static void tabVisible(int visible){
        tabLayout.setVisibility(visible);
    }
}
