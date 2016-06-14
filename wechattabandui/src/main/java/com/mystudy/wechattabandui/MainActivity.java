package com.mystudy.wechattabandui;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jauker.widget.BadgeView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager vp;
    private List<Fragment> fragmentList;
    private FragmentPagerAdapter pagerAdapter;
    private TextView mChatTextView;
    private TextView mFindTextView;
    private TextView mContactTextView;
    private BadgeView badgeView;
    private LinearLayout mChatLineLayout;
    private ImageView mImageLine;
    private int mScreen1_3;

    private int mCurrentPageInedx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initImageLine();
        initView();

    }

    private void initImageLine() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        mScreen1_3 = displayMetrics.widthPixels / 3;
        mImageLine = (ImageView) findViewById(R.id.id_iv_line);
        if (mImageLine != null) {
            ViewGroup.LayoutParams layoutParams = mImageLine.getLayoutParams();
            layoutParams.width = mScreen1_3;
            mImageLine.setLayoutParams(layoutParams);
        }
    }

    private void initView() {
        vp = (ViewPager) findViewById(R.id.vp);
        mChatTextView = (TextView) findViewById(R.id.id_tv_chat);
        mFindTextView = (TextView) findViewById(R.id.id_tv_find);
        mContactTextView = (TextView) findViewById(R.id.id_tv_contact);
        mChatLineLayout = (LinearLayout) findViewById(R.id.id_ll_chat);
        fragmentList = new ArrayList<>();
        ChatMainTabFragment tab1 = new ChatMainTabFragment();
        FindMainTabFragment tab2 = new FindMainTabFragment();
        ContactMainTabFragment tab3 = new ContactMainTabFragment();
        fragmentList.add(tab1);
        fragmentList.add(tab2);
        fragmentList.add(tab3);
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        vp.setAdapter(pagerAdapter);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // positionOffset 0.0 - 1.0
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mImageLine.getLayoutParams();

                if (mCurrentPageInedx == 0 && position == 0) { //0->1
                    lp.leftMargin = (int) (mCurrentPageInedx * mScreen1_3 + positionOffset * mScreen1_3);
                } else if (mCurrentPageInedx == 1 && position == 0) { //1-0
                    lp.leftMargin = (int) (mCurrentPageInedx * mScreen1_3 + (positionOffset - 1) * mScreen1_3);
                } else if (mCurrentPageInedx == 1 && position == 1) { //1-2
                    lp.leftMargin = (int) (mCurrentPageInedx * mScreen1_3 + positionOffset * mScreen1_3);
                } else if (mCurrentPageInedx == 2 && position == 1) {//2-1
                    lp.leftMargin = (int) (mCurrentPageInedx * mScreen1_3 + (positionOffset - 1) * mScreen1_3);
                }
                mImageLine.setLayoutParams(lp);
                Log.e("page","position::::"+position);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        if (badgeView != null) {
                            mChatLineLayout.removeView(badgeView);
                        }
                        badgeView = new BadgeView(MainActivity.this);
                        badgeView.setBadgeCount(7);
                        mChatLineLayout.addView(badgeView);
                        mChatTextView.setTextColor(Color.parseColor("#008800"));
                        break;
                    case 1:
                        mFindTextView.setTextColor(Color.parseColor("#008800"));
                        break;
                    case 2:
                        mContactTextView.setTextColor(Color.parseColor("#008800"));
                        break;
                }
                mCurrentPageInedx = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void resetTextView() {
        mChatTextView.setTextColor(Color.BLACK);
        mFindTextView.setTextColor(Color.BLACK);
        mContactTextView.setTextColor(Color.BLACK);
    }
}
