package com.lets.lettalknew;

public class ModalUser {

    String nickName,gender,age,lookingForGender,lookingForAgeRange,lookingForLanguage,uId;
    String userStatus,ProfilePic1;

    public ModalUser(){}

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public ModalUser(String nickName, String gender, String age, String lookingForGender, String lookingForAgeRange, String lookingForLanguage, String uId,
                     String userStatus, String ProfilePic1) {
        this.nickName = nickName;
        this.gender = gender;
        this.age = age;
        this.lookingForGender = lookingForGender;
        this.lookingForAgeRange = lookingForAgeRange;
        this.lookingForLanguage = lookingForLanguage;
        this.userStatus = userStatus;
        this.ProfilePic1 = ProfilePic1;
        this.uId = uId;



    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLookingForGender() {
        return lookingForGender;
    }

    public void setLookingForGender(String lookingForGender) {
        this.lookingForGender = lookingForGender;
    }

    public String getLookingForAgeRange() {
        return lookingForAgeRange;
    }

    public void setLookingForAgeRange(String lookingForAgeRange) {
        this.lookingForAgeRange = lookingForAgeRange;
    }

    public String getLookingForLanguage() {
        return lookingForLanguage;
    }

    public void setLookingForLanguage(String lookingForLanguage) {
        this.lookingForLanguage = lookingForLanguage;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }


    public String getProfilePic1() {
        return ProfilePic1;
    }

    public void setProfilePic1(String ProfilePic1) {
        this.ProfilePic1 = ProfilePic1;
    }


}
