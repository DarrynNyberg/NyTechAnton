package nybergtechnology.com.webuildapps.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 1/25/2016.
 */
public class CommnUtility {
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

    public static boolean checkEmail(String email){

        boolean validEmail;
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        validEmail = matcher.matches();

        return validEmail;
    }

}
