package nybergtechnology.com.webuildapps.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import nybergtechnology.com.webuildapps.NavigationDrawerFragment;
import nybergtechnology.com.webuildapps.R;
import nybergtechnology.com.webuildapps.Utility.CircleTransformClass;
import nybergtechnology.com.webuildapps.Utility.CommnUtility;
import nybergtechnology.com.webuildapps.Utility.Constant;
import nybergtechnology.com.webuildapps.Utility.MultipartUtility;
import nybergtechnology.com.webuildapps.classes.SessionObject;

/**
 * Created by Administrator on 1/25/2016.
 */
public class AccountFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    Context m_Context;
    public View m_myContainer;

    ImageView menuButton, profileImageButton;
    EditText emailText, passText, phoneText;
    TextView saveButton;

    Bitmap profileBitmap;
    String uploadFileFullPath = null;

    int REQUEST_CAMERA = 1;
    int SELECT_FILE = 2;


    public static AccountFragment newInstance(int sectionNumber) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_fragment, container, false);

        m_myContainer = rootView;
        m_Context = getActivity();

        InitView(rootView);

        fetchUser();

        return rootView;
    }

    private void fetchUser(){
        String url = String.format("%s/%s", Constant.SERVER_URL, Constant.USER_VIEW_API);
        new UserDownloadTask().execute(url);
    }

    private void InitView(View view){
        menuButton = getMenuButton(view);

        profileImageButton = getProfileImageButton(view);
        profileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_placeholder);
        getProfileImageButton(view).setImageBitmap(new CircleTransformClass(profileBitmap, Constant.PROFILE_USER_PHOTO_WIDTH).transform());

        emailText = getEmailText(view);
        passText = getPassText(view);
        phoneText = getPhoneText(view);

        saveButton = getSaveButton(view);

    }

    private ImageView getProfileImageButton(View view){
        if (profileImageButton == null){
            profileImageButton = (ImageView)view.findViewById(R.id.profileImage);
            profileImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickProfileImage();
                }
            });
        }
        return profileImageButton;
    }

    private void onClickProfileImage(){
        final CharSequence[] items = { "Camera", "Gallery", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Select Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {

                    acnFromCamera();

                } else if (items[item].equals("Gallery")) {

                    acnFromGallery();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }
    private void acnFromCamera(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void acnFromGallery(){
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                profileBitmap = (Bitmap) data.getExtras().get("data");;
                saveImageLocally((Bitmap) data.getExtras().get("data"));

                getProfileImageButton(m_myContainer).setImageBitmap(new CircleTransformClass(thumbnail, Constant.PROFILE_USER_PHOTO_WIDTH).transformWithCircleBorder());
                getProfileImageButton(m_myContainer).setScaleType(ImageView.ScaleType.CENTER_CROP);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(m_Context, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                profileBitmap = BitmapFactory.decodeFile(selectedImagePath, options);

                saveImageLocally(BitmapFactory.decodeFile(selectedImagePath, options));

                getProfileImageButton(m_myContainer).setImageBitmap(new CircleTransformClass(bm, Constant.PROFILE_USER_PHOTO_WIDTH).transformWithCircleBorder());
                getProfileImageButton(m_myContainer).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
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

    private EditText getPhoneText(View view){
        if (phoneText == null){
            phoneText = (EditText)view.findViewById(R.id.editTextPhone);
        }
        return phoneText;
    }
    private TextView getSaveButton(View view){
        if (saveButton == null){
            saveButton = (TextView)view.findViewById(R.id.saveButton);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickSaveButton();
                }
            });
        }
        return saveButton;
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

    private void onClickSaveButton(){
        if (checkEmptyfileds())
            return;

//        userSaveAction();
    }
    private String makeUrlParam(){
        String email = getEmailText(m_myContainer).getText().toString().trim();
        String pass = getPassText(m_myContainer).getText().toString().trim();
        String phone = getPhoneText(m_myContainer).getText().toString().trim();

        String urlParameters = "";
        try {
            if (phone.length() == 0) {
                urlParameters =
                        String.format("%s=", Constant.LOG_IN_API_POST_DATA_USER) + URLEncoder.encode(email, "UTF-8") +
                                String.format("&%s=", Constant.LOG_IN_API_POST_DATA_PASSWORD) + URLEncoder.encode(pass, "UTF-8");
            }else {
                urlParameters =
                        String.format("%s=", Constant.LOG_IN_API_POST_DATA_USER) + URLEncoder.encode(email, "UTF-8") +
                                String.format("&%s=", Constant.LOG_IN_API_POST_DATA_PASSWORD) + URLEncoder.encode(pass, "UTF-8") +
                                String.format("&%s=", Constant.LOG_IN_API_POST_DATA_PHONE) + URLEncoder.encode(phone, "UTF-8");                ;
            }

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        return urlParameters;
    }

    private void userSaveAction() {
        String url = String.format("%s/%s", Constant.SERVER_URL, Constant.USER_SAVE_API);
        new UserSaveTask().execute(url, makeUrlParam());
    }

    private boolean uploadfile(String serverUrl, String uploadFileFullPath){

        String charset = "UTF-8";
        File uploadFile = new File(uploadFileFullPath);
        try {
            MultipartUtility multipart = new MultipartUtility(serverUrl, charset);

            multipart.addHeaderField("User-Agent", "CodeJava");
            multipart.addHeaderField("Test-Header", "Header-Value");

            multipart.addFormField("description", "Cool Pictures");
            multipart.addFormField("keywords", "Java,upload,Spring");

            multipart.addFilePart("fileUpload", uploadFile);

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            System.err.println(ex);
            return false;
        }
        return true;
    }

    private boolean checkExistImageFile(String imagePath){
        if (imagePath == null)
            return false;

        File file = new File(imagePath);
        if (!file.exists())
            return false;

        return true;
    }

    private  class  uploadImageTask extends AsyncTask<String, Void,  String> {

        String serverUrl;
        String filePath;
        ProgressDialog dlg;

        protected void onPreExecute() {
            // TODO Auto-generated method stub
            dlg = new ProgressDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT);
            dlg.setCancelable(false);
            dlg.setCanceledOnTouchOutside(false);
            dlg.setMessage("Uploading images ...");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!dlg.isShowing()) {
                        dlg.show();
                    }
                }
            });
        }

        @Override
        protected String  doInBackground(String... params) {
            // TODO Auto-generated method stub
            serverUrl = params[0];
            filePath = params[1];

            if (!uploadfile(serverUrl, filePath))
                return null;
            return "success";
        }

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


            if (result != null){
                Log.d("upload!", "uploading done successfully!");
            }
            else {
                Log.d("upload!", "uploading done failed!");
            }
        }
    }

    private class UserSaveTask extends AsyncTask<String, Void, String> {
        ProgressDialog dlg;

        @Override
        protected void onPreExecute(){
            dlg = new ProgressDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT);
            dlg.setCancelable(false);
            dlg.setCanceledOnTouchOutside(false);
            dlg.setMessage("User saving ....");

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
                Log.d("User save String", result);
                checkUserSaveResult(result);

            }else {
                CommnUtility.showAlert(getActivity(), "Alert", "Can not get session Information. \n Please check internet state");
            }
        }
    }

    private void checkUserSaveResult(String jsonResult){
        JSONObject jObject = null;

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


        //upload image file
        if (uploadFileFullPath != null)
            new uploadImageTask().execute(Constant.SERVER_URL, uploadFileFullPath);
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
    private void saveImageLocally(Bitmap newImage){

        if (newImage != null) {
            String baseDirectoryPath = getActivity().getFilesDir().getAbsolutePath();
            String imageName = String.format("image-%d.jpg",System.currentTimeMillis());
//            String AlbumName = "testPost";
//            String imagePath = String.format("%s%s%s%s", baseDirectoryPath, File.separator, AlbumName, File.separator);
            String imagePath = String.format("%s%s", baseDirectoryPath, File.separator);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            newImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
            File destination = new File(imagePath, imageName);
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();

                uploadFileFullPath = destination.getAbsolutePath();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("saveImageLocally", "failed create image file");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("saveImageLocally", "failed write image");
            }
        }
        else {
        }
    }

    private void fillUI(String jsonResult){
        JSONObject jObject;
        String email = null;
        String sID = null;
        int uID = -1;

        try{
            jObject = new JSONObject(jsonResult);
            email = jObject.get("email").toString();
            sID =  jObject.get("sid").toString();
            uID = Integer.parseInt(jObject.get("uid").toString());
        }catch(Exception e){
            e.printStackTrace();
            return;
        }

        getEmailText(m_myContainer).setText(email);
    }

    private class UserDownloadTask extends AsyncTask<String, Void, String> {
        ProgressDialog dlg;

        @Override
        protected void onPreExecute(){
            dlg = new ProgressDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT);
            dlg.setCancelable(false);
            dlg.setCanceledOnTouchOutside(false);
            dlg.setMessage("Downloading session...");

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
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("user download", e.toString());
            }
            return data;
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

            if (result.length() > 0){
                Log.d("session", result);
                fillUI(result);

            }else {
                CommnUtility.showAlert(getActivity(), "Alert", "Can not get session Information. \n Please check internet state");
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
            Log.d("get session url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
