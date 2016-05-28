package com.mystudy.myzoomimageview;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mystudy.myzoomimageview.view.MyZommImageView;

public class MainActivity extends AppCompatActivity {

    private ViewPager vp;
    private int[] images = new int[]{R.drawable.image1, R.drawable.image2, R.drawable.image3};
    private ImageView[] imageViews = new ImageView[images.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp = (ViewPager) findViewById(R.id.vp);
        vp.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                MyZommImageView myZommImageView = new MyZommImageView(MainActivity.this);
                myZommImageView.setCanScale(false);
                myZommImageView.setImageResource(images[position]);
                container.addView(myZommImageView);
                imageViews[position] = myZommImageView;
                return myZommImageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(imageViews[position]);
            }

            @Override
            public int getCount() {
                return images.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }
}
