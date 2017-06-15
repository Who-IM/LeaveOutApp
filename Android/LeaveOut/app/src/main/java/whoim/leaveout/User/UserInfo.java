package whoim.leaveout.User;

import android.graphics.Bitmap;

/**
 * 유저 정보
 */
public class UserInfo {

    private static UserInfo mInstance;     // 싱글톤
    private int userNum;      // 아이디
    private String facebookId;      // 페이스북 토큰 값
    private String email;           // 이메일
    private String name;            // 이름
    private Bitmap profile;         // 프로필 사진

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getProfile() {
        return profile;
    }

    public void setProfile(Bitmap profile) {
/*        if(this.profile != null) {
            this.profile.recycle();
            this.profile = null;
        }*/
        this.profile = profile;
    }

    public void clear() {
        mInstance = null;
    }
}
