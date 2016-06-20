package io.arcs.grademanager.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import io.arcs.grademanager.R;
import io.arcs.grademanager.exeptions.DuplicateKeyEntry;
import io.arcs.grademanager.models.DataWarehouse;
import io.arcs.grademanager.models.GradeList;
import io.arcs.grademanager.ui.fragments.FragmentMain;
import io.arcs.grademanager.ui.fragments.FragmentSetting;
import io.arcs.grademanager.utils.ImportExport;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity {

    private static MainActivity sMainActivity;
    private CharSequence mTitle;
    private AlertDialog mAlertDialog;
    private SharedPreferences.Editor mEditor;

    static public MainActivity getInstance() {
        return sMainActivity;
    }

    @Override
    protected void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);

        sMainActivity = this;
        mEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        setContentView(R.layout.activity_main);

        // Set default
        GradeList mNL = DataWarehouse.getInstance(this).getGradeList();
        if (mNL.getSubjectList().size() < 1) {
            try {
                mNL.addSubject(getString(R.string.default_1), -3388416);
                mNL.addSubject(getString(R.string.default_2), -9581568);
                mNL.addSubject(getString(R.string.default_3), -13983796);
            } catch (DuplicateKeyEntry e) {
                // Can't happen, because this is only called, if there are no entry's
            }
        }

        // First Start
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (mPrefs.getBoolean("firstLaunch", true)) {
            buildFirstDialog();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu mMenu) {
        getMenuInflater().inflate(R.menu.action_bar, mMenu);
        return super.onCreateOptionsMenu(mMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mItem) {
        switch (mItem.getItemId()) {
            case R.id.action_settings:
                FragmentManager frgManager = getFragmentManager();
                frgManager.beginTransaction().replace(R.id.content_frame, new FragmentSetting(), null).commit();

                return true;

            default:
                return super.onOptionsItemSelected(mItem);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        FragmentMain fragMain = (FragmentMain) getFragmentManager().findFragmentByTag("home");

        if (fragMain == null || !fragMain.isVisible()) {

            FragmentManager frgManager = getFragmentManager();
            frgManager.beginTransaction().replace(R.id.content_frame, new FragmentMain(), "home").commit();

            setTitle(getString(R.string.app_name));

        } else {
            finish();
        }
    }

    private void buildFirstDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        LayoutInflater mInflater = this.getLayoutInflater();
        mBuilder.setView(mInflater.inflate(R.layout.dialog_defaults, null));
        mBuilder.setCancelable(false);

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog2, int id) {
            } // Dummy
        });

        mAlertDialog = mBuilder.create();
        mAlertDialog.show();

        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                HashMap<String, String> hitParameters = new HashMap<String, String>();

                RadioGroup mRg = (RadioGroup) mAlertDialog.findViewById(R.id.selectSys);
                switch (mRg.getCheckedRadioButtonId()) {
                    case R.id.num:
                        if (mAlertDialog.findViewById(R.id.first_select).getVisibility() == View.VISIBLE) { /* if first page is on screen*/
                            mAlertDialog.findViewById(R.id.first_select).setVisibility(View.GONE);
                            mAlertDialog.findViewById(R.id.first_limit).setVisibility(View.VISIBLE);

                            NumberPicker mLowest = (NumberPicker) mAlertDialog.findViewById(R.id.lowest);
                            mLowest.setMinValue(0);
                            mLowest.setMaxValue(3);
                            mLowest.setValue(1);

                            NumberPicker mHighest = (NumberPicker) mAlertDialog.findViewById(R.id.highest);
                            mHighest.setMinValue(4);
                            mHighest.setMaxValue(100);
                            mHighest.setValue(6);

                            ImageButton mIb = (ImageButton) mAlertDialog.findViewById(R.id.first_back);
                            mIb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mAlertDialog.findViewById(R.id.first_select).setVisibility(View.VISIBLE);
                                    mAlertDialog.findViewById(R.id.first_limit).setVisibility(View.GONE);
                                }
                            });
                            return;
                        } else {
                            NumberPicker mLowest = (NumberPicker) mAlertDialog.findViewById(R.id.lowest);
                            NumberPicker mHighest = (NumberPicker) mAlertDialog.findViewById(R.id.highest);

                            mEditor.putInt("lowestGrade", mLowest.getValue());
                            mEditor.putInt("highestGrade", mHighest.getValue());
                            mEditor.putString("clac_method", "0");
                            mEditor.putBoolean("firstLaunch", false);

                            hitParameters.put("Calculation lowest limit", mLowest.getValue()+"");
                            hitParameters.put("Calculation highest limit", mHighest.getValue()+"");
                            hitParameters.put("Calculation Method", "Numbers");
                            EasyTracker.getInstance(sMainActivity).send(hitParameters);

                        }
                        break;
                    case R.id.let:
                        mEditor.putString("clac_method", "1");
                        mEditor.putBoolean("firstLaunch", false);

                        hitParameters.put("Calculation Method", "Letters");
                        EasyTracker.getInstance(sMainActivity).send(hitParameters);

                        break;
                    case R.id.pro:
                        mEditor.putString("clac_method", "2");
                        mEditor.putBoolean("firstLaunch", false);

                        hitParameters.put("Calculation Method", "Percent");
                        EasyTracker.getInstance(sMainActivity).send(hitParameters);

                        break;
                }
                hitParameters.clear();

                mEditor.apply();
                mAlertDialog.cancel();
            }
        });
    }

    /**
     * This is for the Import Export.. it just has to be in an Activity
     *
     * @param requestCode The request code
     * @param resultCode  The result code
     * @param data        The Intend data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ImportExport.REQUEST_CODE_LOAD) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ImportExport.loadFromPath(data.getData(), this);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, this.getString(R.string.canceled), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == ImportExport.REQUEST_CODE_SAVE) {
            if (resultCode == Activity.RESULT_OK) {
                ImportExport.saveToPath(data.getData(), this);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, this.getString(R.string.canceled), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setStatusBarColor(View statusBar, DrawerLayout content) {
        // Below KitKat no support, above is default
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            statusBar.getLayoutParams().height = getStatusBarHeight();

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) content.getLayoutParams();
            params.setMargins(0, getStatusBarHeight() + getActionBarHeight(), 0, 0);
            content.setLayoutParams(params);
        }
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

}