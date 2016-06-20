package io.arcs.grademanager.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.melnykov.fab.FloatingActionButton;
import io.arcs.grademanager.R;
import io.arcs.grademanager.adapters.AdapterSubject;
import io.arcs.grademanager.exeptions.DuplicateKeyEntry;
import io.arcs.grademanager.models.DataWarehouse;
import io.arcs.grademanager.models.Dialog.SubjectDialog;
import io.arcs.grademanager.models.Dialog.SubjectDialogCallBack;
import io.arcs.grademanager.models.GradeList;
import io.arcs.grademanager.models.Timestamp;
import io.arcs.grademanager.ui.activities.MainActivity;
import io.arcs.grademanager.utils.Calendar;

public class FragmentMain extends Fragment implements SubjectDialogCallBack {

    private GradeList mGradeList;
    private MainActivity mMain;
    private Context mCtx;
    private AdapterSubject mAdapter;

    public FragmentMain() {
        this.mMain = MainActivity.getInstance();
        this.mGradeList = DataWarehouse.getInstance(this.mMain).getGradeList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_main, container, false);
        mCtx = mView.getContext();
        setHasOptionsMenu(true);

        mAdapter = new AdapterSubject(mCtx, mGradeList);

        ListView mList = (ListView) mView.findViewById(R.id.listView_Noten);
        mList.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        final FragmentMain fragmentMain = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SubjectDialog().build(mCtx, fragmentMain);
            }
        });
        fab.attachToListView(mList);

        mList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int mPosition, long mId) {
                Fragment mFragment;
                Bundle mArgs = new Bundle();
                
/* ToDo: fix this somehow
                mFragment = new FragmentSubject();
                mArgs.putString(FragmentMain.ITEM_NAME, mMain.dataList.get(mPosition).getItemName());
                mArgs.putInt(FragmentMain.IMAGE_RESOURCE_ID, mMain.dataList.get(mPosition).getImgResID());

                mFragment.setArguments(mArgs);
                FragmentManager frgManager = getFragmentManager();
                frgManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();

                mMain.drawerList.setItemChecked(mPosition, true);
                mMain.setTitle(mMain.dataList.get(mPosition).getItemName());
                mMain.drawerLayout.closeDrawer(mMain.drawerList);
*/

            }
        });

        Timestamp now = Calendar.getCurrentTimestamp(mCtx);
        String d = mGradeList.getAverage(now.getYear(), now.getSemester());

        ((TextView) mView.findViewById(R.id.average)).setText(getString(R.string.durchschnitt_name));
        ((TextView) mView.findViewById(R.id.durchschnitt)).setText(d);

        registerForContextMenu(mList);

        return mView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu mMenu, View mV, ContextMenuInfo mMenuInfo) {
        super.onCreateContextMenu(mMenu, mV, mMenuInfo);

        mMenu.add(1, 1, 1, getString(R.string.edit));
        mMenu.add(1, 2, 2, getString(R.string.delete));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int mItemId = item.getItemId();
        AdapterContextMenuInfo mInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        int mIndex = mInfo.position;

        if (mItemId == 1) {
            new SubjectDialog().build(mCtx, mIndex, mGradeList.getSubjectList().get(mIndex).getName(), mGradeList.getSubjectList().get(mIndex).getColor(), this);
        } else {
            Toast.makeText(mCtx, getString(R.string.deleted), Toast.LENGTH_SHORT).show();

            mGradeList.removeSubject(mGradeList.getSubjectList().get(mIndex).getName(), mIndex);
            mGradeList.save();

            mAdapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public boolean positivePressed(int mId, String mOrigSubject, String mSubjectName, int mSubjectColor) {
        if(mSubjectName.trim().isEmpty()) {
            Toast.makeText(mCtx, mCtx.getString(R.string.error_subject_name_empty), Toast.LENGTH_SHORT).show();
            return false;
        }



        if (mId != -1) {
            mGradeList.updateSubject(mOrigSubject, mId, mSubjectName, mSubjectColor);
        } else {
            try {
                mGradeList.addSubject(mSubjectName, mSubjectColor);
            } catch (DuplicateKeyEntry e) {
                Toast.makeText(mMain, R.string.error_subject_name_unique, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        mGradeList.save();

        mAdapter.notifyDataSetChanged();

        return true;

    }

}