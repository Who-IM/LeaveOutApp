package whoim.leaveout.User;

/**
 * 유저 정보
 */
public class UserInfo {

    private static UserInfo mInstance;     // 싱글톤
    private String id;      // 아이디
    private String facebookId;

    private UserInfo() {}

    // 싱글톤
    public static UserInfo getInstance() {
        if(mInstance == null)
            mInstance = new UserInfo();

        return mInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
