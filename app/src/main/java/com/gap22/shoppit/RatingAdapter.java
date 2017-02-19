package com.gap22.shoppit;

import com.gap22.shoppit.data.ProductRating;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

/**
 * Created by gap22 on 1/4/2017.
 */

public class RatingAdapter extends FirebaseRecyclerAdapter<ProductRating, RatingViewHolder> {
    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public RatingAdapter(Class<ProductRating> modelClass, int modelLayout, Class<RatingViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(RatingViewHolder viewHolder, ProductRating model, int position) {
        String aver = String.valueOf(model.myRating);
        viewHolder.averageRating.setText(aver);
    }
}
