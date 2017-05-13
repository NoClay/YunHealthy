package wang.fly.com.yunhealth.DataBasePackage;

//import cn.bmob.v3.BmobObject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by 82661 on 2016/11/25.
 */

public class SignUserData extends BmobObject {
    private String userName;//昵称
    private String passWord;//用于修改密码
    private String phoneNumber;//用于储存手机号
    private String userImage;//用户头像
    private String idNumber;
    private Boolean isMan;//true为  man    false 为woman
    private Integer height;
    private Float weight;
    private BmobDate birthday;

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Boolean getMan() {
        return isMan;
    }

    public void setMan(Boolean man) {
        isMan = man;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public BmobDate getBirthday() {
        return birthday;
    }

    public void setBirthday(BmobDate birthday) {
        this.birthday = birthday;
    }
}
