package com.yeslabapps.friendb.model;


public class User {
    private String username;
    private String email;
    private String password;
    private String userId;
    private String bio;
    private String avatar;
    private String country;
    private String status;
    private String gender;
    private String registerDate;
    private String mbti;
    private String birthDate;
    private int age;
    private String accountStatus;
    private String dmPrefer;
    private String lastSeen;
    private String seenPrefer;
    private String usernameLower;
    private String isUsernameChanged;


    public User() {
    }

    public User(String username, String email, String password, String userId, String bio, String avatar,
                String country, String status,String gender,String registerDate,String mbti
    ,String birthDate,int age,String accountStatus,String dmPrefer,String lastSeen,String seenPrefer
    , String usernameLower,String isUsernameChanged) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userId = userId;
        this.bio = bio;
        this.avatar = avatar;
        this.country = country;
        this.status = status;
        this.gender=gender;
        this.registerDate=registerDate;
        this.mbti=mbti;
        this.birthDate=birthDate;
        this.age=age;
        this.accountStatus= accountStatus;
        this.dmPrefer=dmPrefer;
        this.lastSeen = lastSeen;
        this.seenPrefer=seenPrefer;
        this.usernameLower = usernameLower;
        this.isUsernameChanged = isUsernameChanged;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMbti() {
        return mbti;
    }

    public void setMbti(String mbti) {
        this.mbti = mbti;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getDmPrefer() {
        return dmPrefer;
    }

    public void setDmPrefer(String dmPrefer) {
        this.dmPrefer = dmPrefer;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getSeenPrefer() {
        return seenPrefer;
    }

    public void setSeenPrefer(String seenPrefer) {
        this.seenPrefer = seenPrefer;
    }

    public String getUsernameLower() {
        return usernameLower;
    }

    public void setUsernameLower(String usernameLower) {
        this.usernameLower = usernameLower;
    }

    public String getIsUsernameChanged() {
        return isUsernameChanged;
    }

    public void setIsUsernameChanged(String isUsernameChanged) {
        this.isUsernameChanged = isUsernameChanged;
    }
}
