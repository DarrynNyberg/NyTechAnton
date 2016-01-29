package nybergtechnology.com.webuildapps.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import nybergtechnology.com.webuildapps.HomeActivity;
import nybergtechnology.com.webuildapps.NavigationDrawerFragment;
import nybergtechnology.com.webuildapps.R;
import nybergtechnology.com.webuildapps.Utility.CommnUtility;
import nybergtechnology.com.webuildapps.Utility.Constant;
import nybergtechnology.com.webuildapps.Utility.UserUtility;

/**
 * Created by Administrator on 1/26/2016.
 */
public class RegisterFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    Context m_Context;
    public View m_myContainer;

    ImageView menuButton;
    EditText emailText, passText;
    TextView registerButton;

    public static RegisterFragment newInstance(int sectionNumber) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public RegisterFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_fragment, container, false);

        m_myContainer = rootView;
        m_Context = getActivity();

        InitView(rootView);

        return rootView;
    }

    private void InitView(View view){
        menuButton = getMenuButton(view);

        emailText = getEmailText(view);
        passText = getPassText(view);

        registerButton = getRegisterButton(view);
    }

    private ImageView getMenuButton(View view){
        if (menuButton == null){
            menuButton = (ImageView)view.findViewById(R.id.menuButton);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickMenu();
                }
            });
        }
        return menuButton;
    }

    private TextView getRegisterButton(View view){
        if (registerButton == null){
            registerButton = (TextView)view.findViewById(R.id.registerButton);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickRegisterButton();
                }
            });
        }
        return registerButton;
    }

    private void onClickRegisterButton(){
        if (checkEmptyfileds())
            return;

        registerAction();
    }

    private void registerAction() {
        String url = String.format("%s/%s", Constant.SERVER_URL, Constant.REGISTER_API);
        new RegisterTask().execute(url, makeUrlParam());
    }

    private String makeUrlParam(){
        String email = getEmailText(m_myContainer).getText().toString().trim();
        String pass = getPassText(m_myContainer).getText().toString().trim();

        String urlParameters = "";
        try{
            urlParameters =
                    String.format("%s=", Constant.LOG_IN_API_POST_DATA_USER) + URLEncoder.encode(email, "UTF-8") +
                            String.format("&%s=", Constant.LOG_IN_API_POST_DATA_PASSWORD) + URLEncoder.encode(pass, "UTF-8");

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        return urlParameters;
    }

    private boolean checkEmptyfileds(){
        String emailString = getEmailText(m_myContainer).getText().toString();
        String passString = getPassText(m_myContainer).getText().toString();

        if (emailString.length() == 0){
            CommnUtility.showAlert(getActivity(), "Alert", "Please enter email");
            getEmailText(m_myContainer).requestFocus();
            return true;
        }

        if (!CommnUtility.checkEmail(emailString)){
            CommnUtility.showAlert(getActivity(), "Alert", "Invalid email address type");
            getEmailText(m_myContainer).requestFocus();
            return true;
        }

        if (passString.length() == 0){
            CommnUtility.showAlert(getActivity(), "Alert", "Please enter password");
            getPassText(m_myContainer).requestFocus();
            return true;
        }

        return false;
    }


    private void gotoLogin(){
        Fragment fragment = RegisterFragment.newInstance(100);
        HomeActivity.replaceNewFragment(fragment);
    }

    private EditText getPassText(View view){
        if (passText == null){
            passText = (EditText)view.findViewById(R.id.editTextPass);
        }
        return passText;
    }

    private EditText getEmailText(View view){
        if (emailText == null){
            emailText = (EditText)view.findViewById(R.id.editTextEmail);
        }
        return emailText;
    }

    private void onClickMenu(){
        NavigationDrawerFragment.sharedInstance.OpenDrawer();
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        ProgressDialog dlg;

        @Override
        protected void onPreExecute(){
            dlg = new ProgressDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT);
            dlg.setCancelable(false);
            dlg.setCanceledOnTouchOutside(false);
            dlg.setMessage("Registering ....");

            getActivity().runOnUiThread(new Runnable() {
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
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String response = null;
            String url = params[0];
            String param = params[1];
            response = excutePost(url, param);
            if (response != null) {

            }

            return response;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dlg.isShowing())
                        dlg.dismiss();
                }
            });

            if (result!=null){
                Log.d("Register String", result);
                checkRegisterResult(result);

            }else {
                CommnUtility.showAlert(getActivity(), "Alert", "Can not get session Information. \n Please check internet state");
            }
        }
    }


    private void checkRegisterResult(String jsonResult){
        JSONObject jObject = null;
Log.d("Register respond", jsonResult);
        try{
            jObject = new JSONObject(jsonResult);
            if (jObject.get("message").toString().length() > 0){
                Log.d("register", jObject.get("message").toString());
                CommnUtility.showAlert(getActivity(), "Alert", jObject.get("message").toString());
            }

        }catch(Exception e){
            e.printStackTrace();
            try {
                CommnUtility.showAlert(getActivity(), "Alert", "Registering successful!");
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }
    }

    public static String excutePost(String targetURL, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
