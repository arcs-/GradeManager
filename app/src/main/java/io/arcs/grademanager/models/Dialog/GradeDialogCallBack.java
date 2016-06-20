package io.arcs.grademanager.models.Dialog;

/**
 * Created by Patrick on 22.01.2015.
 */
public interface GradeDialogCallBack {
    boolean positivePressed(int mId, int mYear, int mSemester, String mGrade, double mWeight, String mComment);
}
