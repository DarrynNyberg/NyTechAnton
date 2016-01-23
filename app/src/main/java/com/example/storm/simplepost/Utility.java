package com.example.storm.simplepost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by storm on 1/23/16.
 */
public class Utility {
    public static ProgressDialog progressDlg;

    public static boolean checkEmail(String email){

        boolean validEmail;
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        validEmail = matcher.matches();

        return validEmail;
    }

    public static void showAlert(Context context, String title, String msg){
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(context);
        myAlertDialog.setTitle(title);
        myAlertDialog.setMessage(msg);
        myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        myAlertDialog.show();
    }

    public static void show_LoadingIndicator(String loadingTitle, Activity context){
        progressDlg = new ProgressDialog(context);
        progressDlg.setTitle(loadingTitle);
        progressDlg.setMessage("Please wait.");
        progressDlg.setCancelable(false);
        progressDlg.setIndeterminate(true);

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!progressDlg.isShowing()) progressDlg.show();
            }
        });
    }

    public static void hideLoading(){
        if (progressDlg != null)
            progressDlg.dismiss();
    }
}
