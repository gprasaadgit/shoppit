package com.gap22.shoppit.data;

/**
 * Created by gap22 on 1/15/2017.
 */

public class ProductAverageRating {
    public float averageRating;
    public int numRatings;
    public String itemName;

    public ProductAverageRating(){

    }

    public ProductAverageRating(int numRatings, float avgRating, float myRating, String itemName){
        averageRating = ((numRatings * avgRating) + myRating)/(numRatings + 1);
        this.numRatings = numRatings + 1;
        this.itemName = itemName;
    }
}
