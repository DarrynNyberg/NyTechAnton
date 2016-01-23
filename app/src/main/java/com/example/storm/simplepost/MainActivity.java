package com.example.storm.simplepost;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText nameEdit, phoneEdit, emailEdit;
    TextView postButton;
    public static MainActivity sharedInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedInstance = this;
        InitView();
    }

    private void InitView(){
        nameEdit = getNameEdit();
        phoneEdit = getPhoneEdit();
        emailEdit = getEmailEdit();

        postButton = getPostButton();
    }

    private EditText getNameEdit(){
        if (nameEdit == null){
            nameEdit = (EditText)findViewById(R.id.nameText);
        }
        return nameEdit;
    }

    private EditText getPhoneEdit(){
        if (phoneEdit == null){
            phoneEdit = (EditText)findViewById(R.id.phoneText);
        }
        return phoneEdit;
    }

    private EditText getEmailEdit(){
        if (emailEdit == null){
            emailEdit = (EditText)findViewById(R.id.emailText);
        }
        return emailEdit;
    }

    private TextView getPostButton(){
        if (postButton == null){
            postButton = (TextView)findViewById(R.id.postButton);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doPost();
                }
            });
        }
        return postButton;
    }

    private boolean checkEmptyFields(){
        if (getNameEdit().getText().toString().length() == 0){
            Utility.showAlert(this, "Alert", "Name can not be empty!");
            getNameEdit().requestFocus();
            return false;
        }

        if (getPhoneEdit().getText().toString().length() == 0){
            Utility.showAlert(this, "Alert", "Phone number can not be empty!");
            getPhoneEdit().requestFocus();
            return false;
        }

        if (getEmailEdit().getText().toString().length() == 0){
            Utility.showAlert(this, "Alert", "Email can not be empty!");
            getPhoneEdit().requestFocus();
            return false;
        }

        if (!Utility.checkEmail(getEmailEdit().getText().toString())){
            Utility.showAlert(this, "Alert", "Invalid email type");
            getEmailEdit().requestFocus();
            return false;
        }
        return true;
    }

    private void doPost(){

        if (!checkEmptyFields())
            return;

        String name = getNameEdit().getText().toString().trim();
        String phone = getPhoneEdit().getText().toString().trim();
        String email = getEmailEdit().getText().toString().trim();

        String urlParameters = "";
        try{
             urlParameters =
                    "name=" + URLEncoder.encode(name, "UTF-8") +
                            "&phone=" + URLEncoder.encode(phone, "UTF-8") +
                            "&email=" + URLEncoder.encode(email, "UTF-8");

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        new postTask().execute(Constant.postUrl, urlParameters);
    }

    private  class  postTask extends AsyncTask<String, Void, List<String>> {

        protected void onPreExecute() {
            // TODO Auto-generated method stub
            Utility.show_LoadingIndicator("Uploading data ....", MainActivity.sharedInstance);
        }

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

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            Utility.hideLoading();

            if (result != null){
                if (result.size() >0)
                    Log.d("response", result.get(0));
            }
        }
    }

    public static String excutePost(String targetURL, String urlParameters)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }


}
