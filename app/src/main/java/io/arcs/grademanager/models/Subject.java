package io.arcs.grademanager.models;

import java.io.Serializable;

public class Subject implements Serializable {

    private static final long serialVersionUID = 7491482406220191257L;

    private String mName;
    private int mColor;

    public Subject(String mName, int mColor) {
        this.mName = mName;
        this.mColor = mColor;
    }

    public String getName() {
        return mName;
    }

    public int getColor() {
        return mColor;
    }

    public void updateSubject(String mName, int mColor) {
        this.mName = mName;
        this.mColor = mColor;
    }

}
