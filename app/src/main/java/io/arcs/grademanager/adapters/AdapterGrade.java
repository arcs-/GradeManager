package io.arcs.grademanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import io.arcs.grademanager.R;
import io.arcs.grademanager.models.Grade;
import io.arcs.grademanager.models.GradeList;

import java.util.ArrayList;

public class AdapterGrade extends BaseAdapter {

    public static final String SEPARATOR = "�#!@";
    private static LayoutInflater sInflater;
    private static ArrayList<String> mNotenSortiert = new ArrayList<String>();
    private GradeList mNl;
    private Context mCtx;
    private String mFach;
    private int mFarbe;

    public AdapterGrade(Context mCtx, GradeList mNoten, String mFach) {
        this.mNl = mNoten;
        sInflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mCtx = mCtx;
        this.mFach = mFach;

        this.mFarbe = mNl.getColorFromSubject(mFach);

    }

    public void refresh() {
        mNotenSortiert.clear();
        int mGroessetsJahrSemester = 0;
        int mGroesstesJahr = 0;
        int mGroesstesSemester = 1;
        int mPosition = mNl.getGrades(mFach).size();

        // Create: 2013 + semester 1 = 20131
        for (Grade n : mNl.getGrades(mFach)) {
            int t = n.getJahr() * 10 + n.getSemester();
            if (t > mGroessetsJahrSemester) {
                mGroesstesJahr = n.getJahr();
                mGroesstesSemester = n.getSemester();
                mGroessetsJahrSemester = t;
            }
        }
        if (mGroesstesJahr == 0) {
            return;
        }

        int mAltesJahrSemester = 0;

        while (mPosition > 0) {
            for (int i = 0; i < mNl.getGrades(mFach).size(); i++) {
                Grade n = mNl.getGrades(mFach).get(i);

                if (n.getJahr() == mGroesstesJahr && n.getSemester() == mGroesstesSemester) {
                    mGroessetsJahrSemester = mGroesstesJahr * 10 + mGroesstesSemester;
                    if (mAltesJahrSemester != mGroessetsJahrSemester) {
                        mNotenSortiert.add("h" + mGroesstesJahr + SEPARATOR + mGroesstesSemester + mCtx.getString(R.string.semester_add));
                        mAltesJahrSemester = mGroessetsJahrSemester;
                    }
                    mNotenSortiert.add("n" + n.getNote() + SEPARATOR + n.getGewichtung() + SEPARATOR + n.getBemerkung() + " " + SEPARATOR
                            + i);
                    mPosition--;
                }
            }

            if (mGroesstesSemester == 1) {
                mGroesstesJahr--;
            }
            mGroesstesSemester = 3 - mGroesstesSemester;

        }

    }

    public int getCount() {
        refresh();
        return mNotenSortiert.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int mPposition, View mView, ViewGroup mParent) {
        String mCurrent = mNotenSortiert.get(mPposition);
        if (mCurrent.charAt(0) == 'h') {
            mView = sInflater.inflate(R.layout.list_item_header, mParent, false);
            if (mPposition != 0) mView.setPadding(0, 60, 0, 0);

            mCurrent = mCurrent.substring(1);
            String[] parts = mCurrent.split(SEPARATOR);

            ((TextView) mView.findViewById(R.id.header_title)).setText(parts[0] + "/" + (Integer.parseInt(parts[0].substring(2)) + 1));
            ((TextView) mView.findViewById(R.id.header_title_semester)).setText(parts[1]);
            mView.findViewById(R.id.item_header_block).setBackgroundColor(mFarbe);


            mView.setId(-1);
        } else {
            mView = sInflater.inflate(R.layout.list_item_note, mParent, false);

            mCurrent = mCurrent.substring(1);
            String[] parts = mCurrent.split(SEPARATOR);

            ((TextView) mView.findViewById(R.id.item_note)).setText(parts[0]);
            ((TextView) mView.findViewById(R.id.item_gewichtung)).setText(parts[1] + "x ");
            ((TextView) mView.findViewById(R.id.item_comment)).setText(parts[2]);

            int id = Integer.parseInt(parts[3]) + 15;
            mView.setId(id);

        }
        return mView;
    }
}