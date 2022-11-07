package com.example.pjs4_app;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.firebase.storage.StorageReference;

import java.util.List;

import androidx.annotation.RequiresApi;

public class CustomAdapter extends BaseAdapter {
    private List<StorageReference> listStorage;
    private Context context;

    /**
     * Required constructor
     * @param context
     * @param s
     */
    public CustomAdapter(Context context, List<StorageReference> s) {
        this.context = context;
        listStorage = s;
    }

    /**
     * Gets the number of image in the gridView
     * @return size
     */
    @Override
    public int getCount() {
        return listStorage.size();
    }

    /**
     * Return the Image's position
     * @param position
     * @return position
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

     /**
     * Return the Image's position
     * @param position
     * @return position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    /**
     * Displays the image
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView img;
        img =new ImageView(context);
        img.setLayoutParams(new GridView.LayoutParams(400,400));
        img.setBackgroundColor(context.getResources().getColor(R.color.liquid_white));
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideApp.with(context).load(listStorage.get(position)).into(img);
        return img;
    }
}
