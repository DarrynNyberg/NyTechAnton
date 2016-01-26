package nybergtechnology.com.webuildapps.classes;

/**
 * Created by Administrator on 1/25/2016.
 */
public class SessionObject {
    public String   token;
    public String   session_name;
    public String   sid;
    public String   email;
    public int      uid;

    public SessionObject(String token, String session_name, String sid, String email, int uid){
        this.token = token;
        this.session_name = session_name;
        this.sid = sid;
        this.email = email;
        this.uid = uid;
    }
}
