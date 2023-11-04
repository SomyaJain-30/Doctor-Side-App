package com.example.doctorside;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context){
        this.context = context;
    }

    int imageArray[] = {
            R.drawable.viewpage3,R.drawable.viewpage1, R.drawable.viewpage4};
    int headingArray[] = {R.string.viewpage1,R.string.viewpage2,R.string.viewpage3};
    @Override
    public int getCount() {
        return imageArray.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.viewpager_layout,container,false);

        ImageView imageView = view.findViewById(R.id.viewpager_img);
        TextView textView = view.findViewById(R.id.viewpager_heading);

        imageView.setImageResource(imageArray[position]);
        textView.setText(headingArray[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
