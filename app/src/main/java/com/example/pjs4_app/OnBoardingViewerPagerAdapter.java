package com.example.pjs4_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class OnBoardingViewerPagerAdapter extends PagerAdapter {

    Context c;
    List<ScreenItem> items;

    /**
     * Constructor of the different views for the OnBoardingSlideShow
     * @param c
     * @param items
     */
    public OnBoardingViewerPagerAdapter(Context c, List<ScreenItem> items) {
        this.c = c;
        this.items = items;
    }

    /**
     * Instantiates the view where the slide will be displayed
     * @param container
     * @param position
     * @return the displayed slide
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen, null);

        TextView title = layoutScreen.findViewById(R.id.screenTitle);
        TextView description = layoutScreen.findViewById(R.id.screenContent);

        title.setText(items.get(position).getTitle());
        description.setText(items.get(position).getDescription());
        layoutScreen.setBackground(items.get(position).getScreenimg());

        container.addView(layoutScreen);

        return layoutScreen;
    }

    /**
     * Counts the number of slides
     * @return the number of slides
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * Verifies if a view equals another object
     * @param view
     * @param o
     * @return true or false
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    /**
     * Removes a slide from the onBoarding slideshow
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);
    }
}
