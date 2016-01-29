package nybergtechnology.com.webuildapps.Utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 1/25/2016.
 */
public class UserUtility {
    private static Context mContext;

    public static void setContext(Context context){
        mContext = context;
    }

    public static Context getContext(){
        return mContext;
    }

    public static boolean checkNewUser(String sId, int uId){
        if (getCurrentSessionId().length() == 0)
            return true;
        if (getCurrentUserId() == -1)
            return true;
        if (!getCurrentSessionId().equals(sId))
            return true;
        if (getCurrentUserId() != uId)
            return true;

        return false;
    }

    public static String getCurrentSessionId(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("APP_CURRENT_USER_SESSION", Context.MODE_PRIVATE);
        return sharedPreferences.getString("var1", "");
    }

    public static void setCurrentSessionId(String sessionId){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("APP_CURRENT_USER_SESSION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("var1", sessionId);
        editor.commit();
    }

    public static int getCurrentUserId(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("APP_CURRENT_USER_ID", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("var1", -1);
    }

    public static void setCurrentUserId(int userID){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("APP_CURRENT_USER_ID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("var1", userID);
        editor.commit();
    }

}
