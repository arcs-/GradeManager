package io.arcs.grademanager.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import io.arcs.grademanager.exeptions.DuplicateKeyEntry;
import io.arcs.grademanager.utils.ObjectSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class GradeList implements Serializable {
    private static final long serialVersionUID = -1240622674291786659L;
    private static final String PREFERENCE_STRING_GRADE = "notenliste";
    private static final String PREFERENCE_STRING_SUBJECT = "facherliste";

    private HashMap<String, ArrayList<Grade>> mGradeList;
    private ArrayList<Subject> mSubjectList;
    private SharedPreferences mPrefs;


    public GradeList(Context mCtx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        loadLists();
    }

    public void loadLists() {
        mGradeList = (HashMap<String, ArrayList<Grade>>) ObjectSerializer.getDeserializeDisregardException(mPrefs.getString(PREFERENCE_STRING_GRADE, null));
        if (null == mGradeList) mGradeList = new HashMap<String, ArrayList<Grade>>();

        mSubjectList = (ArrayList<Subject>) ObjectSerializer.getDeserializeDisregardException(mPrefs.getString(PREFERENCE_STRING_SUBJECT, null));
        if (null == mSubjectList) mSubjectList = new ArrayList<Subject>();

    }

    public void loadObject(HashMap<String, ArrayList<Grade>> gradeList, ArrayList<Subject> subjectList) {
        mGradeList = gradeList;
        mSubjectList = subjectList;

    }

    public void save() {
        mPrefs.edit().putString(PREFERENCE_STRING_GRADE, ObjectSerializer.getSerializeDisregardException(mGradeList)).apply();
        mPrefs.edit().putString(PREFERENCE_STRING_SUBJECT, ObjectSerializer.getSerializeDisregardException(mSubjectList)).apply();
    }

    public int getColorFromSubject(String mSubject) {
        for (Subject subject : mSubjectList)
            if (subject.getName().equalsIgnoreCase(mSubject))
                return subject.getColor();

        return 0;
    }

    public String getAverage(int mActualYear, int mCurrentSemester) {

        double complete = 0;
        int subjectCount = 0;
        for (Subject subject : mSubjectList) {

            double grade = calculatedAverageForSubject(subject.getName(), mActualYear, mCurrentSemester);
            if (grade > 0) {
                subjectCount += 1;
                complete += grade;
            }

        }

        return (complete == 0) ? "-" : reformatGrade(complete / subjectCount);

    }

    public String getAverageForSubject(String mSubject, int mActualYear, int mCurrentSemester) {

        double mDur = calculatedAverageForSubject(mSubject, mActualYear, mCurrentSemester);

        return (mDur == 0) ? "-" : reformatGrade(mDur);
    }

    private double calculatedAverageForSubject(String mSubject, int mActualYear, int mCurrentSemester) {
        double mGew = 0;
        double mDur = 0;
        for (Grade mGrade : mGradeList.get(mSubject.toLowerCase(Locale.getDefault()))) {
            if (mGrade.getJahr() == mActualYear && mGrade.getSemester() == mCurrentSemester) {
                mDur = mDur + mGrade.getPercent() * mGrade.getGewichtung();
                mGew = mGew + mGrade.getGewichtung();
            }
        }
        mDur = mDur / mGew;
        if ((mDur + "").equals("NaN")) mDur = 0;

        return mDur;
    }

    private String reformatGrade(double mGrade) {
        int method = Integer.parseInt(mPrefs.getString("clac_method", "0"));
        switch (method) {
            case 0:
                return ((mGrade == 0) ? "-" : Math.round(Grade.getNumberByPercent(mGrade) * 100d) / 100d) + "";
            case 1:
                return ((mGrade == 0) ? "-" : Grade.getLetterByPercent(Math.round(mGrade * 100d) / 100d));
            case 2:
                return ((mGrade == 0) ? "-" : Math.round(mGrade * 100d) / 100d) + "%";
        }

        return "-";
    }

    public void updateLimit(int newLimit) {
        for (String subject : mGradeList.keySet()) {
            for (Grade n : mGradeList.get(subject)) {
                n.newLimit(newLimit);
            }

        }
    }

    public void addSubject(String mKey, int mColor) throws DuplicateKeyEntry {
        for (Subject subject : mSubjectList)
            if (subject.getName().equalsIgnoreCase(mKey)) throw new DuplicateKeyEntry();

        mGradeList.put(mKey.toLowerCase(Locale.getDefault()), new ArrayList<Grade>());
        mSubjectList.add(new Subject(mKey, mColor));
    }

    public ArrayList<Subject> getSubjectList() {
        return mSubjectList;
    }

    public HashMap<String, ArrayList<Grade>> getGradeList() {
        return mGradeList;
    }

    public void updateSubject(String mKey, int mPosition, String mNewName, int mNewColor) {
        mSubjectList.get(mPosition).updateSubject(mNewName, mNewColor);
        mGradeList.put(mNewName.toLowerCase(Locale.getDefault()), mGradeList.remove(mKey.toLowerCase(Locale.getDefault())));
    }

    public void removeSubject(String mKey, int mPosition) {
        mSubjectList.remove(mPosition);
        mGradeList.remove(mKey.toLowerCase(Locale.getDefault()));
    }

    public void addGrade(String mKey, Grade mGrade) throws NumberFormatException {
        mGradeList.get(mKey.toLowerCase(Locale.getDefault())).add(mGrade);
    }

    public ArrayList<Grade> getGrades(String mKey) {
        return mGradeList.get(mKey.toLowerCase(Locale.getDefault()));
    }

    public void updateGrade(String mKey, int mId, String mNote, int mJahr, int mSemester, double mGewichtung, String mBemerkung) throws NumberFormatException {
        mGradeList.get(mKey.toLowerCase(Locale.getDefault())).get(mId).update(mNote, mJahr, mSemester, mGewichtung, mBemerkung);
    }

}