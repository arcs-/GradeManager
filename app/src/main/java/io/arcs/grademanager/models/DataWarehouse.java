package io.arcs.grademanager.models;

import android.content.Context;

/**
 * Created by Patrick on 31.01.2015.
 */
public class DataWarehouse {

    private static DataWarehouse dataWarehouse;

    public static DataWarehouse getInstance(Context mCtx) {
        if(dataWarehouse == null) dataWarehouse = new DataWarehouse(mCtx);
        return dataWarehouse;
    }

    // Content
    private GradeList gradeList;
    private FrequentWords frequentWords;

    private DataWarehouse(Context mCtx) {
        gradeList = new GradeList(mCtx);
        frequentWords = new FrequentWords(mCtx);


    }

    public GradeList getGradeList() {
        return gradeList;
    }

    public FrequentWords getFrequentWords() {
        return frequentWords;
    }
}
