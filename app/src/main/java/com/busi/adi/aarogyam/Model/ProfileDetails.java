package com.busi.adi.aarogyam.Model;

public class ProfileDetails {
    private String username;
    private String full_name;
    private String dob;
    private String gender;
    private String blood_group;
    private String phone;
    private String email;

    public ProfileDetails(){};
    public ProfileDetails(String username, String full_name, String dob, String gender, String blood_group, String phone, String email){
        this.username = username;
        this.full_name = full_name;
        this.dob = dob;
        this. gender = gender;
        this.blood_group = blood_group;
        this.phone = phone;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
