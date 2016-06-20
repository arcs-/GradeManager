package io.arcs.grademanager.models.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.buzzingandroid.ui.HSVColorPickerDialog;
import io.arcs.grademanager.R;

/**
 * Created by Patrick on 13.01.2015.
 */
public class SubjectDialog {

    private Context mCtx;

    private AlertDialog mAlertDialog;
    private String mPositiveButton;

    public void build(Context mCtx, SubjectDialogCallBack mGradeDialogCallBack) {
        this.mCtx = mCtx;
        this.mPositiveButton = mCtx.getString(R.string.add);

        init(-1, "", -9581568, mGradeDialogCallBack);
    }

    public void build(Context mCtx, int mId, String mSubjectName, int mSubjectColor, SubjectDialogCallBack mGradeDialogCallBack) {
        this.mCtx = mCtx;
        mPositiveButton = mCtx.getString(R.string.update);

        init(mId, mSubjectName, mSubjectColor, mGradeDialogCallBack);
    }

    private void init(int mId, String mSubjectName, int mSubjectColor, SubjectDialogCallBack mGradeDialogCallBack) {
        mAlertDialog = getAlertDialog();
        mAlertDialog.show();

        setUpNameField(mSubjectName);
        setUpColorPicker(mSubjectColor);

        setUpOnClick(mId, mSubjectName, mGradeDialogCallBack);

    }


    private AlertDialog getAlertDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mCtx);
        LayoutInflater mInflater = (LayoutInflater) mCtx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        mBuilder.setView(mInflater.inflate(R.layout.dialog_subject, null));

        mBuilder.setPositiveButton(mPositiveButton, null).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return mBuilder.create();
    }

    private void setUpNameField(String mName) {
        ((EditText) mAlertDialog.findViewById(R.id.name)).setText(mName);
    }

    private void setUpColorPicker(final int mColor) {
        mAlertDialog.findViewById(R.id.colorButton).setBackgroundColor(mColor);

        final Button mButton = (Button) mAlertDialog.findViewById(R.id.colorButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                HSVColorPickerDialog cpd = new HSVColorPickerDialog(mCtx, mColor, new HSVColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void colorSelected(Integer color) {
                        mButton.setBackgroundColor(color);
                    }
                });
                cpd.setTitle(mCtx.getString(R.string.pick_color));
                cpd.show();

            }
        });
    }


    private void setUpOnClick(final int mId, final String mOrigSubject, final SubjectDialogCallBack mSubjectDialogCallBack) {
        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mSubjectName = ((EditText) mAlertDialog.findViewById(R.id.name)).getText().toString();
                Button mSubjectColor = ((Button) mAlertDialog.findViewById(R.id.colorButton));
                ColorDrawable mButtonColor = (ColorDrawable) mSubjectColor.getBackground();
                int colorId = mButtonColor.getColor();

                mAlertDialog.cancel();
                if(mSubjectDialogCallBack.positivePressed(mId, mOrigSubject, mSubjectName, colorId)) {
                    mAlertDialog.cancel();
                }
            }
        });
    }

}