package io.arcs.grademanager.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;
import io.arcs.grademanager.R;
import io.arcs.grademanager.models.DataWarehouse;
import io.arcs.grademanager.models.Grade;
import io.arcs.grademanager.models.GradeList;
import io.arcs.grademanager.models.Subject;
import io.arcs.grademanager.ui.activities.MainActivity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ImportExport {

    public static int REQUEST_CODE_SAVE = 200;
    public static int REQUEST_CODE_LOAD = 201;

    private MainActivity mMain;

    public ImportExport(MainActivity mMain) {
        this.mMain = mMain;
    }

    public static void loadFromPath(Uri path, Activity mMain) {
        try {
            InputStream mFis = mMain.getContentResolver().openInputStream(path);
            GZIPInputStream mGzis = new GZIPInputStream(mFis);
            ObjectInputStream mIn = new ObjectInputStream(mGzis);

            @SuppressWarnings("unchecked")
            HashMap<String, ArrayList<Grade>> mResult = (HashMap<String, ArrayList<Grade>>) mIn.readObject();
            @SuppressWarnings("unchecked")
            ArrayList<Subject> mResult2 = (ArrayList<Subject>) mIn.readObject();
            mIn.close();

            DataWarehouse.getInstance(mMain).getGradeList().loadObject(mResult, mResult2);

            Intent mIntent = mMain.getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(mMain.getBaseContext().getPackageName());

            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            mMain.startActivity(mIntent);

            DataWarehouse.getInstance(mMain).getGradeList().save();

            Toast.makeText(mMain, mMain.getString(R.string.done), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(mMain, mMain.getString(R.string.corrupt), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            Toast.makeText(mMain, mMain.getString(R.string.corrupt), Toast.LENGTH_LONG).show();
        } catch (ClassCastException e) {
            Toast.makeText(mMain, mMain.getString(R.string.corrupt), Toast.LENGTH_LONG).show();
        }
    }

    public static void saveToPath(Uri path, Activity mMain) {
        System.out.println(path);
        BufferedOutputStream bos = null;
        try {

            OutputStream stream = mMain.getContentResolver().openOutputStream(path);


            GZIPOutputStream mGzos = new GZIPOutputStream(stream);
            ObjectOutputStream mOut = new ObjectOutputStream(mGzos);

            mOut.writeObject(DataWarehouse.getInstance(mMain).getGradeList().getGradeList());
            mOut.writeObject(DataWarehouse.getInstance(mMain).getGradeList().getSubjectList());

            mOut.close();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mMain, mMain.getString(R.string.error), Toast.LENGTH_LONG).show();
            return;

        } finally {
            try {
                if (bos != null) bos.close();
            } catch (IOException e) {
            }
        }

        Toast.makeText(mMain, mMain.getString(R.string.saved_at), Toast.LENGTH_LONG).show();

    }

    public void saveGradeList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/octet-stream");
            intent.putExtra(Intent.EXTRA_TITLE, "GradeList");
            mMain.startActivityForResult(intent, REQUEST_CODE_SAVE);
        } else {
            saveToPath(Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/GradeManager.aio"), mMain);
        }
    }

    public void loadGradeList() {

        new AlertDialog.Builder(mMain)
                .setTitle(mMain.getString(R.string.sure))

                .setMessage(mMain.getString(R.string.sure_comment))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            //KitKat only code here
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("application/*");
                            mMain.startActivityForResult(intent, REQUEST_CODE_LOAD);

                        } else {
                            loadFromPath(Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/GradeManager.aio"), mMain);
                        }

                    }
                }).show();
    }


}
