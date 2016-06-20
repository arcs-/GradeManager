package io.arcs.grademanager.models;

/**
 * Created by Patrick on 27.10.2014.
 */
public class Timestamp {
    private int mSemester;
    private int mYear;

    public Timestamp(int mSemester, int mYear) {
        this.mSemester = mSemester;
        this.mYear = mYear;
    }

    public int getSemester() {
        return mSemester;
    }

    public void setSemester(int mSemester) {
        this.mSemester = mSemester;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int mYear) {
        this.mYear = mYear;
    }
}
