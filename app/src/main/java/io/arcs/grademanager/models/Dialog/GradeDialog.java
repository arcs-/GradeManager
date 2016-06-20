package io.arcs.grademanager.models.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import io.arcs.grademanager.R;
import io.arcs.grademanager.models.DataWarehouse;
import io.arcs.grademanager.models.FrequentWords;
import io.arcs.grademanager.models.Timestamp;
import io.arcs.grademanager.utils.Calendar;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Patrick on 13.01.2015.
 */
public class GradeDialog {

    private Context mCtx;
    private SharedPreferences mPrefs;

    private AlertDialog mAlertDialog;
    private String mPositiveButton;

    public void build(Context mCtx, GradeDialogCallBack mGradeDialogCallBack) {
        this.mCtx = mCtx;
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(mCtx);

        mPositiveButton = mCtx.getString(R.string.add);

        Timestamp timestamp = Calendar.getCurrentTimestamp(mCtx);
        int default_weight = mPrefs.getInt("default_weight", 1);


        init(-1, timestamp.getYear(), timestamp.getSemester(), "", default_weight, "", mGradeDialogCallBack);
    }

    public void build(Context mCtx, int mId, int mYear,  int mSemester, String mGrade, double mWeight, String mComment, GradeDialogCallBack mGradeDialogCallBack) {
        this.mCtx = mCtx;
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(mCtx);

        mPositiveButton = mCtx.getString(R.string.update);

        init(mId,mYear,mSemester,mGrade,mWeight,mComment,mGradeDialogCallBack);
    }

    private void init(int mId, int mYear,  int mSemester, String mGrade, double mWeight, String mComment, GradeDialogCallBack mGradeDialogCallBack) {
        mAlertDialog = getAlertDialog();
        mAlertDialog.show();

        setUpYearSpinner(mYear);
        setUpSemesterSpinner(mSemester);
        setUpGradeField(mGrade);
        setUpWeightField(mWeight);
        setUpSuggestions(mComment);

        setUpOnClick(mId, mGradeDialogCallBack);

    }


    private AlertDialog getAlertDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mCtx);
        LayoutInflater mInflater = (LayoutInflater) mCtx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        mBuilder.setView(mInflater.inflate(R.layout.dialog_grade, null));

        mBuilder.setPositiveButton(mPositiveButton, null).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return mBuilder.create();
    }

    private void setUpYearSpinner(int mCurrentYear) {
        String mYears[] = {Calendar.getDualYear(mCtx, 0),
                           Calendar.getDualYear(mCtx, 1),
                           Calendar.getDualYear(mCtx, 2)};

        Spinner mYear = (Spinner) mAlertDialog.findViewById(R.id.jahr);

        ArrayAdapter<String> mSpinnerYears = new ArrayAdapter<String>(mCtx, android.R.layout.simple_spinner_item, mYears);
        mSpinnerYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mYear.setAdapter(mSpinnerYears);

        int mSpinnerPosition = mSpinnerYears.getPosition(mCurrentYear + "/" + (mCurrentYear + 1 - 2000));
        mYear.setSelection(mSpinnerPosition);
    }

    private void setUpSemesterSpinner(int mCurrentSemester) {
        String mSemesterSpinnerContent[] = new String[]{mCtx.getString(R.string.Semester1), mCtx.getString(R.string.Semester2)};

        Spinner mSemester = (Spinner) mAlertDialog.findViewById(R.id.semester);
        ArrayAdapter<String> mSpinnerSemester = new ArrayAdapter<String>(mCtx, android.R.layout.simple_spinner_item, mSemesterSpinnerContent);
        mSpinnerSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSemester.setAdapter(mSpinnerSemester);

        mSemester.setSelection(mCurrentSemester-1);
    }

    private void setUpGradeField(String mCurrentGrade) {
        ((EditText) mAlertDialog.findViewById(R.id.note)).setText(mCurrentGrade);


        int mCalcMethod = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mCtx).getString("clac_method", "0"));

        if (mCalcMethod == 1)((EditText) mAlertDialog.findViewById(R.id.note)).setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        else ((EditText) mAlertDialog.findViewById(R.id.note)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    private void setUpWeightField(double mCurrentWeight) {
        ((EditText) mAlertDialog.findViewById(R.id.gewichtung)).setText(mCurrentWeight + "");
    }

    private void setUpSuggestions(String mCurrentComment) {
        // Field
        EditText textView = ((EditText) mAlertDialog.findViewById(R.id.item_header));
        textView.setText(mCurrentComment);

        // Suggestions
        Set<String> suggestionsSet = DataWarehouse.getInstance(mCtx).getFrequentWords().getTopWords();
        suggestions = new ArrayList<String>(suggestionsSet.size()+1);
        suggestions.add(0, Calendar.getCurrentDate(mCtx));

        for(String word : suggestionsSet) {
            suggestions.add(word);
        }

        vi = (LayoutInflater) mCtx.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        suggestionContainer = (LinearLayout) mAlertDialog.findViewById(R.id.suggestionContainer);

       for(int i = 0; i < 4; i++) addNextWord();

    }

    private ArrayList<String> suggestions;
    private int suggestionPosition = 0;
    private LinearLayout suggestionContainer;
    private LayoutInflater vi;

    private void addNextWord() {
        String word;
        try { word = suggestions.get(suggestionPosition);
        } catch (IndexOutOfBoundsException e) { return; }
        suggestionPosition += 1;

        // Create Button
        View v = vi.inflate(R.layout.button_suggestion, null);

        Button button = (Button) v.findViewById(R.id.button);
        button.setText(word);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView comment = (TextView) mAlertDialog.findViewById(R.id.item_header);
                boolean noSpace = comment.getText().length() == 0 || comment.getText().charAt(comment.length() - 1) == ' ';
                comment.append(((noSpace)?"":" ") + ((Button) v).getText());
                v.setVisibility(View.GONE);
                addNextWord();
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setVisibility(View.GONE);
                // can be null because the warehouse will be initialized at this point
                FrequentWords frequentWords = DataWarehouse.getInstance(null).getFrequentWords();
                frequentWords.removeWord(((Button) v).getText() + "");
                frequentWords.save();
                Toast.makeText(mCtx, mCtx.getString(R.string.deleted),Toast.LENGTH_SHORT).show();
                addNextWord();
                return true;
            }
        });

        suggestionContainer.addView(v);
    }

    private void setUpOnClick(final int mId, final GradeDialogCallBack mGradeDialogCallBack) {
        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mWeight = ((EditText) mAlertDialog.findViewById(R.id.gewichtung)).getText().toString();
                String mGrade = ((EditText) mAlertDialog.findViewById(R.id.note)).getText().toString();
                String mYear = ((Spinner) mAlertDialog.findViewById(R.id.jahr)).getSelectedItem().toString();
                Spinner mSemester = (Spinner) mAlertDialog.findViewById(R.id.semester);
                String mComment = ((EditText) mAlertDialog.findViewById(R.id.item_header)).getText().toString();

                double mWeightNumber;
                try {
                    mWeightNumber = Double.parseDouble(mWeight);
                } catch (NumberFormatException e) {
                    mWeightNumber = 1;
                }

                int mSemesterNumber = mSemester.getSelectedItemPosition()+1;

                int mYearNumber = Integer.parseInt(mYear.substring(0, 4));

                if(mGradeDialogCallBack.positivePressed(mId, mYearNumber, mSemesterNumber, mGrade, mWeightNumber, mComment)) {
                    mAlertDialog.cancel();
                    if(!mComment.trim().isEmpty()) {
                        FrequentWords frequentWords = DataWarehouse.getInstance(mCtx).getFrequentWords();
                        String[] words = mComment.split(" ");
                        for(String word : words) {
                            word = word.trim();
                            if(!word.matches("([0-9]{1,2}(/|.)?){2}(/|.)?[0-9]{2,4}")) {
                                frequentWords.useWord(word);
                            }
                        }
                        frequentWords.save();
                    }
                }

            }
        });
    }

}