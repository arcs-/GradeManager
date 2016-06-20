package io.arcs.grademanager.ui.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import io.arcs.grademanager.R;
import io.arcs.grademanager.models.DataWarehouse;
import io.arcs.grademanager.models.GradeList;
import io.arcs.grademanager.ui.activities.MainActivity;
import io.arcs.grademanager.utils.ImportExport;

import java.text.DateFormatSymbols;

public class FragmentSetting extends PreferenceFragment {

    private static MainActivity sMain;

    private SharedPreferences mPrefs;
    private AlertDialog mAlertDialog;

    public FragmentSetting() {
        sMain = MainActivity.getInstance();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(sMain);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDatePicker();
        setupExportImport();

        switchStateLimit(mPrefs.getString("clac_method", "0"));
        setupDefaultWeight();

        setupLimitPicker();

    }

    private void setupLimitPicker() {
        Preference limit = findPreference("limit");
        limit.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(sMain);
                LayoutInflater mInflater = getActivity().getLayoutInflater();
                mBuilder.setView(mInflater.inflate(R.layout.dialog_number_picker, null));

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int id) {

                        NumberPicker mLowest = (NumberPicker) mAlertDialog.findViewById(R.id.lowest);
                        NumberPicker mHighest = (NumberPicker) mAlertDialog.findViewById(R.id.highest);

                        SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(sMain).edit();
                        mEditor.putInt("lowestGrade", mLowest.getValue());
                        mEditor.putInt("highestGrade", mHighest.getValue());
                        mEditor.apply();

                    }

                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                mBuilder.setTitle(R.string.limit_sum);
                mAlertDialog = mBuilder.create();
                mAlertDialog.show();


                NumberPicker mLowest = (NumberPicker) mAlertDialog.findViewById(R.id.lowest);
                mLowest.setMinValue(0);
                mLowest.setMaxValue(3);
                mLowest.setValue(mPrefs.getInt("lowestGrade", 1));


                NumberPicker mHighest = (NumberPicker) mAlertDialog.findViewById(R.id.highest);
                mHighest.setMinValue(4);
                mHighest.setMaxValue(100);
                mHighest.setValue(mPrefs.getInt("highestGrade", 6));

                DataWarehouse.getInstance(sMain).getGradeList().updateLimit(mPrefs.getInt("highestGrade", 6));


                return true;
            }
        });

    }

    private void setupDefaultWeight() {
        Preference mDweight = findPreference("default_weight");
        mDweight.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(sMain);
                LayoutInflater mInflater = getActivity().getLayoutInflater();
                mBuilder.setView(mInflater.inflate(R.layout.dialog_default_weight, null));

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int id) {

                        NumberPicker mWeight = (NumberPicker) mAlertDialog.findViewById(R.id.default_wei);

                        SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(sMain).edit();
                        mEditor.putInt("default_weight", mWeight.getValue());
                        mEditor.apply();

                        System.out.println(mWeight);

                    }

                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                mBuilder.setTitle(getString(R.string.def_weight_sum));
                mAlertDialog = mBuilder.create();
                mAlertDialog.show();


                NumberPicker mWeight = (NumberPicker) mAlertDialog.findViewById(R.id.default_wei);
                mWeight.setMinValue(0);
                mWeight.setMaxValue(4);
                mWeight.setValue(mPrefs.getInt("default_weight", 0));


                return true;
            }
        });

    }

    public void setupExportImport() {
        Preference mPref = findPreference("import_text");
        mPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                new ImportExport(sMain).loadGradeList();
                return true;
            }
        });

        Preference mPref2 = findPreference("export_text");
        mPref2.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                ImportExport ie = new ImportExport(sMain);
                ie.saveGradeList();

                return true;
            }
        });

        Preference mPref3 = findPreference("clac_method");
        mPref3.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference mView, Object mValue) {
                switchStateLimit(mValue);
                return true;
            }

        });

    }

    private void switchStateLimit(Object mValue) {
        if (mValue.equals("0")) {
            (findPreference("limit")).setEnabled(true);
        } else {
            (findPreference("limit")).setEnabled(false);
        }
    }

    public void setupDatePicker() {
        addPreferencesFromResource(R.layout.fragment_setting);

        final int mStart_sem_month_1 = mPrefs.getInt("start_sem_month_1", 8);
        final int mStart_sem_day_1 = mPrefs.getInt("start_sem_day_1", 1);

        final int mStart_sem_month_2 = mPrefs.getInt("start_sem_month_2", 2);
        final int mStart_sem_day_2 = mPrefs.getInt("start_sem_day_2", 12);

        Preference mPref = findPreference("semsester_eins");
        mPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                createDialogWithoutDateField(1, mStart_sem_day_1, mStart_sem_month_1).show();
                return true;
            }
        });
        mPref.setSummary(mStart_sem_day_1 + " " + getMonth(mStart_sem_month_1));

        Preference mPref2 = findPreference("semester_zwei");
        mPref2.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                createDialogWithoutDateField(2, mStart_sem_day_2, mStart_sem_month_2).show();
                return true;
            }
        });
        mPref2.setSummary(mStart_sem_day_2 + " " + getMonth(mStart_sem_month_2));
    }

    private DatePickerDialog createDialogWithoutDateField(final int mSelector, int mStart_sem_day, int mStart_sem_month) {

        final DatePickerDialog mDpd = new DatePickerDialog(sMain, /*new mDateSetListener(this, sMain, mSelector)*/null, 2014, mStart_sem_month, mStart_sem_day);
        mDpd.setTitle(getString(R.string.date_title));
        try {
            // "Hack" into datePicker class
            java.lang.reflect.Field[] datePickerDialogFields = mDpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(mDpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        if ("mYearSpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        final Fragment fragment = this;
        mDpd.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.date_set), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(sMain).edit();
                mEditor.putInt("start_sem_month_" + mSelector, mDpd.getDatePicker().getMonth());
                mEditor.putInt("start_sem_day_" + mSelector, mDpd.getDatePicker().getDayOfMonth());
                mEditor.apply();

                fragment.onCreate(null);

                dialog.dismiss();
            }
        });
        mDpd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDpd.getDatePicker().setCalendarViewShown(false);

        return mDpd;

    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

}