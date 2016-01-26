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
import nybergtechnology.com.webuildapps.MainActivity;
import nybergtechnology.com.webuildapps.NavigationDrawerFragment;
import nybergtechnology.com.webuildapps.R;
import nybergtechnology.com.webuildapps.Utility.CommnUtility;
import nybergtechnology.com.webuildapps.Utility.Constant;
import nybergtechnology.com.webuildapps.classes.SessionObject;

/**
 * Created by Administrator on 1/25/2016.
 */
public class LoginFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    Context m_Context;
    public View m_myContainer;

    ImageView menuButton;
    EditText emailText, passText;
    TextView loginButton, registerButton;

    public static LoginFragment newInstance(int sectionNumber) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);

        m_myContainer = rootView;
        m_Context = getActivity();

        InitView(rootView);

        return rootView;
    }

    private void InitView(View view){
        menuButton = getMenuButton(view);

        emailText = getEmailText(view);
        passText = getPassText(view);

        loginButton = getLoginButton(view);
        registerButton = getRegisterButton(view);
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
        Fragment fragment = RegisterFragment.newInstance(100);
        HomeActivity.replaceNewFragment(fragment);
    }

    private TextView getLoginButton(View view){
        if (loginButton == null){
            loginButton = (TextView)view.findViewById(R.id.loginButton);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickLoginButton();
                }
            });
        }
        return loginButton;
    }

    private void onClickLoginButton(){
        if (checkEmptyfileds())
            return;

        loginAction();
    }

    private void loginAction(){
        String url = String.format("%s/%s", Constant.SERVER_URL, Constant.LOG_IN_API);
        new LoginTask().execute(url, makeUrlParam());
    }

    private String makeUrlParam(){
        String email = getEmailText(m_myContainer).getText().toString().trim();
        String pass = getPassText(m_myContainer).getText().toString().trim();

        String urlParameters = "";
        try{
            urlParameters =
                    String.format("%s=", Constant.LOG_IN_API_POST_DATA_USER) + URLEncoder.encode(email, "UTF-8") +
                            String.format("&%s=", Constant.LOG_IN_API_POST_DATA_USER) + URLEncoder.encode(pass, "UTF-8");

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

    private void onClickMenu(){
        NavigationDrawerFragment.sharedInstance.OpenDrawer();
    }

    private class LoginTask extends AsyncTask<String, Void, List<String>> {
        ProgressDialog dlg;

        @Override
        protected void onPreExecute(){
            dlg = new ProgressDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT);
            dlg.setCancelable(false);
            dlg.setCanceledOnTouchOutside(false);
            dlg.setMessage("Log in ....");

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
        protected List<String> doInBackground(String... params) {
            // TODO Auto-generated method stub
            List<String> result = new ArrayList<>();
            String url = params[0];
            String param = params[1];
            String response = excutePost(url, param);
            if (response != null)
                result.add(response);

            return result;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dlg.isShowing())
                        dlg.dismiss();
                }
            });

            if (result!=null){
                Log.d("Login", result.get(0).toString());
                checkLoginResult(result.get(0).toString());

            }else {
                CommnUtility.showAlert(HomeActivity.sharedInstance, "Alert", "Can not get session Information. \n Please check internet state");
            }
        }
    }


    private void checkLoginResult(String jsonResult){
        JSONObject jObject;

        try{
            jObject = new JSONObject(jsonResult);
            if (jObject.get("message").toString().length() > 0){
                CommnUtility.showAlert(getActivity(), "Alert", jObject.get("message").toString());
            }

        }catch(Exception e){
            e.printStackTrace();
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
