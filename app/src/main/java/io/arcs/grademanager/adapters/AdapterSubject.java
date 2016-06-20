package io.arcs.grademanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import io.arcs.grademanager.R;
import io.arcs.grademanager.models.GradeList;
import io.arcs.grademanager.models.Subject;
import io.arcs.grademanager.models.Timestamp;
import io.arcs.grademanager.utils.Calendar;

import java.util.ArrayList;

public class AdapterSubject extends BaseAdapter {

    private GradeList mNl;
    private ArrayList<Subject> mSubjectList;
    private Context mCtx;
    private LayoutInflater sInflater;

    public AdapterSubject(Context mCtx, GradeList mGradeList) {
        this.mNl = mGradeList;
        this.mSubjectList = mGradeList.getSubjectList();
        this.mCtx = mCtx;
        sInflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mSubjectList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int mPosition, View mView, ViewGroup mParent) {
        mView = sInflater.inflate(R.layout.liste_item_home, null);
        Subject mCurrentSubject = mSubjectList.get(mPosition);

        Timestamp currentTime = Calendar.getCurrentTimestamp(mCtx);
        String mAverage = mNl.getAverageForSubject(mCurrentSubject.getName(), currentTime.getYear(), currentTime.getSemester());

        ((TextView) mView.findViewById(R.id.average)).setText(mCurrentSubject.getName());
        ((TextView) mView.findViewById(R.id.durchschnitt)).setText(mAverage);
        mView.setBackgroundColor(mCurrentSubject.getColor());

        return mView;
    }
}
