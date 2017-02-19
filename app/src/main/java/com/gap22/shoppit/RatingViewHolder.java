package com.gap22.shoppit;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by gap22 on 1/4/2017.
 */

public class RatingViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = RatingViewHolder.class.getSimpleName();
    public TextView averageRating;
    public RatingViewHolder(View itemView) {
        super(itemView);
        averageRating = (TextView)itemView.findViewById(R.id.tvProductID);
    }

}
