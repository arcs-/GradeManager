package io.arcs.grademanager.models;

import android.content.Context;
import android.preference.PreferenceManager;
import io.arcs.grademanager.R;
import io.arcs.grademanager.ui.activities.MainActivity;

import java.io.Serializable;
import java.util.Locale;

public class Grade implements Serializable {

    private static final long serialVersionUID = 3816715445711989721L;

    private double mGrade;
    private int mLimit = -20;
    private int mYear;
    private int mSemester;
    private double mWeight;
    private String mComment;

    public Grade(String mGrade, int mYear, int mSemester, double mWeight, String mComment) throws NumberFormatException {
        this.mGrade = getGradeByString(mGrade);
        this.mYear = mYear;
        this.mSemester = mSemester;
        this.mWeight = mWeight;
        this.mComment = mComment;
    }

    static public double getNumberByPercent(double mGrade) {
        Context mCtx = MainActivity.getInstance();
        int limit = PreferenceManager.getDefaultSharedPreferences(mCtx).getInt("highestGrade", 6);
        return limit * mGrade / 100;

    }

    static public String getLetterByPercent(double mGrade) {
        if (mGrade > 97) return "A+";
        else if (mGrade > 93) return "A";
        else if (mGrade > 90) return "A-";
        else if (mGrade > 87) return "B+";
        else if (mGrade > 83) return "B";
        else if (mGrade > 80) return "B-";
        else if (mGrade > 77) return "C+";
        else if (mGrade > 73) return "C";
        else if (mGrade > 70) return "C-";
        else if (mGrade > 67) return "D+";
        else if (mGrade > 63) return "D";
        else if (mGrade > 60) return "D-";
        else return "F";

    }

    static public int getPercentByLetter(String mGrade) {
        mGrade = mGrade.toLowerCase(Locale.getDefault());
        if (mGrade.equals("a+")) return 100;
        else if (mGrade.equals("a")) return 96;
        else if (mGrade.equals("a-")) return 92;
        else if (mGrade.equals("b+")) return 89;
        else if (mGrade.equals("b")) return 86;
        else if (mGrade.equals("b-")) return 82;
        else if (mGrade.equals("c+")) return 79;
        else if (mGrade.equals("c")) return 76;
        else if (mGrade.equals("c-")) return 72;
        else if (mGrade.equals("d+")) return 69;
        else if (mGrade.equals("d")) return 66;
        else if (mGrade.equals("d-")) return 62;
        else return 59;

    }

    public void update(String mNote, int mYear, int mSemester, double mWeight, String mComment) throws NumberFormatException {
        this.mGrade = getGradeByString(mNote);
        this.mYear = mYear;
        this.mSemester = mSemester;
        this.mWeight = mWeight;
        this.mComment = mComment;
    }

    public void newLimit(int mNewLimit) {
        if (mLimit != -20) {
            double gradeNumber = this.mLimit * this.mGrade / 100;
            this.mGrade = gradeNumber * 100 / mNewLimit;
            this.mLimit = mNewLimit;
        }
    }

    public double getGradeByString(String mNote) throws NumberFormatException {
        Context mCtx = MainActivity.getInstance();
        int method = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mCtx).getString("clac_method", "0"));

        switch (method) {
            case 0:
                int lowest = PreferenceManager.getDefaultSharedPreferences(mCtx).getInt("lowestGrade", 1);
                mLimit = PreferenceManager.getDefaultSharedPreferences(mCtx).getInt("highestGrade", 6);
                double grade = 0;
                try {
                    grade = Double.parseDouble(mNote);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException(MainActivity.getInstance().getString(R.string.error_grade_between_nNum));
                }
                String errorMessage = MainActivity.getInstance().getString(R.string.error_grade_between_num).replace("**lowest", lowest + "").replace("**highest", mLimit + "");
                if (grade > mLimit || grade < lowest) throw new NumberFormatException(errorMessage);
                return Double.parseDouble(mNote) * 100 / mLimit;

            case 1:
                mNote = mNote.toLowerCase(Locale.getDefault());
                if (!mNote.matches("([abcdef][+-]?)$"))
                    throw new NumberFormatException(MainActivity.getInstance().getString(R.string.error_grade_between_let));

                return getPercentByLetter(mNote);
            case 2:
                mNote.replace("%", "");
                double doubleGrade = 0;
                try {
                    doubleGrade = Double.parseDouble(mNote);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException(MainActivity.getInstance().getString(R.string.error_grade_between_nNum));
                }
                if (doubleGrade > 100 || doubleGrade < 0)
                    throw new NumberFormatException(MainActivity.getInstance().getString(R.string.error_grade_between_pro));
                return doubleGrade;

        }

        throw new NumberFormatException("Internal Error");
    }

    public int getJahr() {
        return mYear;
    }

    public int getSemester() {
        return mSemester;
    }

    public double getGewichtung() {
        return mWeight;
    }

    public String getBemerkung() {
        return mComment;
    }

    public double getPercent() {
        return mGrade;
    }

    public String getNote() {
        Context mCtx = MainActivity.getInstance();
        int method = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mCtx).getString("clac_method", "0"));

        switch (method) {
            case 0:
                return (Math.round(getNumberByPercent(mGrade) * 100d) / 100d) + "";
            case 1:
                return getLetterByPercent(mGrade);
            case 2:
                return (Math.round(mGrade * 100d) / 100d) + "";
        }

        return "";
    }
}
