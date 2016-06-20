package io.arcs.grademanager.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import com.melnykov.fab.FloatingActionButton;
import io.arcs.grademanager.R;
import io.arcs.grademanager.adapters.AdapterGrade;
import io.arcs.grademanager.models.DataWarehouse;
import io.arcs.grademanager.models.Grade;
import io.arcs.grademanager.models.Dialog.GradeDialog;
import io.arcs.grademanager.models.Dialog.GradeDialogCallBack;
import io.arcs.grademanager.models.GradeList;

public class FragmentSubject extends Fragment implements GradeDialogCallBack {

    public static final String ITEM_NAME = "itemName";

    private View mView;
    private Context mCtx;
    private GradeList mNl;

    private ListView mList;
    private AdapterGrade mAdapter;

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mContainer, Bundle mSavedInstanceState) {
        mView = mInflater.inflate(R.layout.fragment_fach, mContainer, false);
        mCtx = mView.getContext();
        setHasOptionsMenu(true);

        mNl = DataWarehouse.getInstance(mCtx).getGradeList();
        mList = (ListView) mView.findViewById(R.id.listView_Noten);

        setupFAB();

        registerForContextMenu(mList);

        return mView;
    }

    private void setupFAB() {

        final FragmentSubject fragmentSubject = this;

        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GradeDialog().build(mCtx, fragmentSubject);
            }
        });
        fab.attachToListView(mList);


        mAdapter = new AdapterGrade(mCtx, mNl, getArguments().getString(ITEM_NAME));
        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (isHeader(v)) {

                    int jahr = Integer.parseInt(((TextView) v.findViewById(R.id.header_title)).getText().toString().substring(0, 4));
                    int sem = Integer.parseInt(((TextView) v.findViewById(R.id.header_title_semester)).getText().toString().substring(0, 1));

                    String d = mNl.getAverageForSubject(getArguments().getString(ITEM_NAME), jahr, sem);

                    ((TextView) mView.findViewById(R.id.average)).setText(getString(R.string.durchschnitt_name));
                    ((TextView) mView.findViewById(R.id.durchschnitt)).setText(d);

                }

            }
        });
    }

    public void onCreateContextMenu(ContextMenu mMenu, View mView, ContextMenuInfo mMenuInfo) {
        super.onCreateContextMenu(mMenu, mView, mMenuInfo);

        int mPosition = ((AdapterContextMenuInfo) mMenuInfo).position;
        View mItem = getViewByPosition(mPosition);

        // If grade element
        if (!isHeader(mItem)) {
            mMenu.add(1, 1, 1, getString(R.string.edit));
            mMenu.add(1, 2, 2, getString(R.string.delete));
        } else {
            mMenu.close();
        }
    }

    private boolean isHeader(View v) {
        return v.findViewById(R.id.item_header_block) != null;
    }

    private View getViewByPosition(int position) {

        // From http://stackoverflow.com/a/14619482
        int mFirstPosition = mList.getFirstVisiblePosition() - mList.getHeaderViewsCount();
        int mWantedChild = position - mFirstPosition;

        if (mWantedChild < 0 || mWantedChild >= mList.getChildCount()) {
            System.out.println("Unable to get view for desired position, because it's not being displayed on screen.");
            return null;
        }

        return mList.getChildAt(mWantedChild);
    }

    @Override
    public boolean onContextItemSelected(MenuItem mItem) {
        int mItemId = mItem.getItemId();
        int mPosition = ((AdapterContextMenuInfo) mItem.getMenuInfo()).position;
        int mId = getViewByPosition(mPosition).getId() - 15;

        if (mItemId == 1) {
            Grade mGrade = mNl.getGrades(getArguments().getString(ITEM_NAME)).get(mId);
            new GradeDialog().build(mCtx, mId, mGrade.getJahr(), mGrade.getSemester(), mGrade.getNote(), mGrade.getGewichtung(), mGrade.getBemerkung(), this);
        } else {
            Toast.makeText(mCtx, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
            mNl.getGrades(getArguments().getString(ITEM_NAME)).remove(mId);
            mNl.save();
            mAdapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public boolean positivePressed(int mId, int mYear, int mSemester, String mGrade, double mWeight, String mComment) {
        try {
            if (mId > -1) mNl.updateGrade(getArguments().getString(ITEM_NAME), mId, mGrade, mYear, mSemester, mWeight, mComment);
            else mNl.addGrade(getArguments().getString(ITEM_NAME), new Grade(mGrade, mYear, mSemester, mWeight, mComment));
            mNl.save();
        } catch (NumberFormatException e) {
            Toast.makeText(mCtx, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        mAdapter.notifyDataSetChanged();

        /* Reset Average */
        ((TextView) mView.findViewById(R.id.durchschnitt)).setText(getString(R.string.minus));
        ((TextView) mView.findViewById(R.id.average)).setText(R.string.durchschnitt_pre);

        return true;
    }

}