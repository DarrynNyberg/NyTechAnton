package nybergtechnology.com.webuildapps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import nybergtechnology.com.webuildapps.Fragment.AccountFragment;
import nybergtechnology.com.webuildapps.Fragment.LoginFragment;
import nybergtechnology.com.webuildapps.Utility.CommnUtility;
import nybergtechnology.com.webuildapps.Utility.Constant;
import nybergtechnology.com.webuildapps.Utility.UserUtility;
import nybergtechnology.com.webuildapps.classes.SessionObject;

/**
 * Created by Administrator on 1/25/2016.
 */
public class HomeActivity extends ActionBarActivity  implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static HomeActivity sharedInstance;

    SessionObject sessionObject;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        sharedInstance = this;
        UserUtility.setContext(this);
        getSession();
    }

    private void getSession(){
        String url = String.format("%s/%s", Constant.SERVER_URL, Constant.GET_SESSION_API);
        new SessionDownloadTask().execute(url);
    }

    private void checkUser(String sessionJsonStr){
        JSONObject jObject;

        try{
            jObject = new JSONObject(sessionJsonStr);
            sessionObject = new SessionObject(jObject.get("token").toString(),
                    jObject.get("session_name").toString(),
                    jObject.get("sid").toString(),
                    jObject.get("email").toString(),
                    Integer.parseInt(jObject.get("uid").toString()));

        }catch(Exception e){
            e.printStackTrace();
        }


        setContentView(R.layout.home_activity);

        getSupportActionBar().hide();

        fragmentManager = getSupportFragmentManager();

        //slide bar menu
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = LoginFragment.newInstance(position + 1);
                break;
            case 1:
                fragment = AccountFragment.newInstance(position + 1);
                break;
            default:
                break;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public static void replaceNewFragment(Fragment fragment){
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


    private class SessionDownloadTask extends AsyncTask<String, Void, String> {
        ProgressDialog dlg;

        @Override
        protected void onPreExecute(){
            dlg = new ProgressDialog(HomeActivity.sharedInstance, android.app.AlertDialog.THEME_HOLO_LIGHT);
            dlg.setCancelable(false);
            dlg.setCanceledOnTouchOutside(false);
            dlg.setMessage("Downloading session...");

            HomeActivity.sharedInstance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!dlg.isShowing()) {
                        dlg.show();
                    }
                }
            });

        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            HomeActivity.sharedInstance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dlg.isShowing())
                        dlg.dismiss();
                }
            });

            if (result.length() > 0){
                Log.d("downl", result);
                checkUser(result);

            }else {
                CommnUtility.showAlert(HomeActivity.sharedInstance, "Alert", "Can not get session Information. \n Please check internet state");
            }
        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}
