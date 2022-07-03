package com.app.zuluwaterways;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class CustomAdapter extends ArrayAdapter {


    private final String[] spinnerTitles;
    private final Drawable[] spinnerImages;
    private final Context mContext;


    public CustomAdapter(@NonNull Context context, String[] titles, Drawable[] images) {
        super(context, R.layout.custom_spinner_row);
        this.spinnerTitles = titles;
        this.spinnerImages = images;
        this.mContext = context;
    }
    @Override
    public int getCount() {
        return spinnerTitles.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert mInflater != null;
            convertView = mInflater.inflate(R.layout.custom_spinner_row, parent, false);
            mViewHolder.mFlag =  convertView.findViewById(R.id.ivMarkerType);
            mViewHolder.mName = convertView.findViewById(R.id.tvMarkerType);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mFlag.setImageDrawable(spinnerImages[position]);
        mViewHolder.mName.setText(spinnerTitles[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        ImageView mFlag;
        TextView mName;

    }
}
