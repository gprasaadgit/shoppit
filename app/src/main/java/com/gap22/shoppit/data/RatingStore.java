package com.gap22.shoppit.data;

import android.provider.BaseColumns;

/**
 * Created by gap22 on 12/23/2016.
 */

public class RatingStore {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private RatingStore() {}

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RatingEntry.TABLE_NAME + " (" +
                    RatingEntry._ID + " INTEGER PRIMARY KEY," +
                    RatingEntry.COLUMN_NAME_Quality + " Boolean," +
                    RatingEntry.COLUMN_NAME_ValueForMoney + " Boolean," +
                    RatingEntry.COLUMN_NAME_BuyAgain + " Boolean)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RatingEntry.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static class RatingEntry implements BaseColumns {
        public static final String TABLE_NAME = "Ratings";
        public static final String COLUMN_NAME_Quality = "Quality";
        public static final String COLUMN_NAME_ValueForMoney = "ValueForMoney";
        public static final String COLUMN_NAME_BuyAgain = "BuyAgain";
    }
}
