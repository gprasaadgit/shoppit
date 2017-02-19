package com.gap22.shoppit.data;

import android.content.Intent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.gap22.shoppit.R;


/**
 * Created by gap22 on 12/27/2016.
 */

public class ProductRating {
    //public String itemCode;
    //public String userID;
    public boolean itemQuality;
    public boolean itemValueForMoney;
    public float myRating;
    public String itemName;

    public ProductRating(){

    }

    public ProductRating(String code, boolean quality, boolean valueForMoney){
        //this.itemCode = code;
        this.itemQuality = quality;
        this.itemValueForMoney = valueForMoney;
    }

    public ProductRating(Intent intent, View v){
        //this.itemCode = intent.getStringExtra(barCodeContent);
        RatingBar rBar = (RatingBar) v.findViewById(R.id.ratingBar);
        myRating = rBar.getRating();
        RatingBar rBarAvg = (RatingBar) v.findViewById(R.id.ratingBar3);
        RadioGroup rgQuality = (RadioGroup) v.findViewById(R.id.rgQuality);
        RadioButton rb = (RadioButton) v.findViewById(rgQuality.getCheckedRadioButtonId());
        this.itemQuality = rb.getText().toString().equalsIgnoreCase("good");
        RadioGroup rgValueForMoney = (RadioGroup) v.findViewById(R.id.rgValueForMoney);
        RadioButton rb1 = (RadioButton) v.findViewById(rgValueForMoney.getCheckedRadioButtonId());
        this.itemValueForMoney = rb1.getText().toString().equalsIgnoreCase("good");
        //this.userID = intent.getStringExtra("userID");
        this.itemName = intent.getStringExtra("productName");
    }
}
