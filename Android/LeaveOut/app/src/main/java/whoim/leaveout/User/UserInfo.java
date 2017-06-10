package whoim.leaveout.User;

/**
 * 유저 정보
 */
public class UserInfo {

    private static UserInfo mInstance;     // 싱글톤
    private int userNum;      // 아이디
    private String facebookId;

    private UserInfo() {}

    // 싱글톤
    public static UserInfo getInstance() {
        if(mInstance == null)
            mInstance = new UserInfo();

        return mInstance;
    }

    public int getUserNum() { return userNum; }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
