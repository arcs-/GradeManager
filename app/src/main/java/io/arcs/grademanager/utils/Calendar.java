package io.arcs.grademanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import io.arcs.grademanager.models.Timestamp;


import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Patrick on 27.10.2014.
 */
public class Calendar {

    /**
     * return current date in localized string
     */
    public static String getCurrentDate(Context ctx) {
        java.util.Date date = new Date();
        return DateFormat.getDateFormat(ctx).format(date);
    }

    /**
     * @param ctx The Context
     * @return Timestamp with current grade year and semester
     */
    public static Timestamp getCurrentTimestamp(Context ctx) {
        int mCurrentSemester = (inFirstSemester(ctx)) ? 1 : 2;
        int mActualYear = GregorianCalendar.getInstance().get(java.util.Calendar.YEAR);
        mActualYear = (datesAhead(ctx) != 0) ? mActualYear - 1 : mActualYear;

        return new Timestamp(mCurrentSemester, mActualYear);
    }

    /**
     * @param ctx   The context
     * @param minus Minus this value (for past years)
     * @return Year in format 2014/15
     */
    public static String getDualYear(Context ctx, int minus) {
        if (datesAhead(ctx) != 0) minus += 1;
        int startYear = GregorianCalendar.getInstance().get(java.util.Calendar.YEAR);
        int endYear = GregorianCalendar.getInstance().get(java.util.Calendar.YEAR) + 1 - 2000;

        return (startYear - minus) + "/" + (endYear - minus);
    }

    /**
     * @param ctx The Context
     * @return boolean for is in first semester
     */
    public static boolean inFirstSemester(Context ctx) {
        // 1 is 2. semester 0&2 are first
        return datesAhead(ctx) != 1;
    }

    /**
     * Returns the count of semester switches ahead
     *
     * @param ctx The context
     * @return dates ahead
     */
    private static int datesAhead(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        int currentDay = GregorianCalendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
        int currentMonth = GregorianCalendar.getInstance().get(java.util.Calendar.MONTH);
        //    int currentYear = GregorianCalendar.getInstance().get(java.util.Calendar.YEAR);

        int firstSemesterDay = mPrefs.getInt("start_sem_day_1", 1);
        int firstSemesterMonth = mPrefs.getInt("start_sem_month_1", 8);

        int secondSemesterDay = mPrefs.getInt("start_sem_day_2", 12);
        int secondSemesterMonth = mPrefs.getInt("start_sem_month_2", 2);

        int dateAhead = 0;
        if (currentMonth < firstSemesterMonth) dateAhead += 1;
        else if (currentMonth == firstSemesterMonth) if (currentDay < firstSemesterDay) dateAhead += 1;

        if (currentMonth < secondSemesterMonth) dateAhead += 1;
        else if (currentMonth == secondSemesterMonth) if (currentDay < secondSemesterDay) dateAhead += 1;

        return dateAhead;
/*
        int startFirst = currentYear, startSecond = currentYear + 1;
        if(firstSemesterMonth >= currentMonth || (firstSemesterMonth >= currentMonth && firstSemesterDay > currentDay)) {
            startFirst = currentYear - 1;
            startSecond = currentYear;
        }

        Date firstStart = new Date(startFirst,firstSemesterMonth,firstSemesterDay);
        Date secondStart = new Date(startSecond,secondSemesterMonth,secondSemesterDay);
        Date currentDate = new Date(currentYear,currentMonth,currentDay);

        return currentDate.after(firstStart) && currentDate.before(secondStart);
*/
    }
}
