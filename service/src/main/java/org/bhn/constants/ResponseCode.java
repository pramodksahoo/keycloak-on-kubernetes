package org.bhn.constants;

import java.util.HashMap;
import java.util.Map;

public class ResponseCode {
    public static final String AUTH_1001 = "AUTH_1001";
    public static final String AUTH_1002 = "AUTH_1002";
    public static final String AUTH_1003 = "AUTH_1003";
    public static final String AUTH_1004 = "AUTH_1004";
    public static final String AUTH_1005 = "AUTH_1005";
    public static final String AUTH_1006 = "AUTH_1006";
    public static final String AUTH_1007 = "AUTH_1007";
    public static final String AUTH_1008 = "AUTH_1008";
    public static final String AUTH_1009 = "AUTH_1009";
    public static final String AUTH_1010 = "AUTH_1010";
    public static final String AUTH_1011 = "AUTH_1011";
    public static final String AUTH_1012 = "AUTH_1012";


    public static final String AUTH_ERR_1000 ="AUTH_ERR_1000";
    public static final String AUTH_ERR_1001 = "AUTH_ERR_1001";
    public static final String AUTH_ERR_1002 = "AUTH_ERR_1002";
    public static final String AUTH_ERR_1003 = "AUTH_ERR_1003";
    public static final String AUTH_ERR_1004 = "AUTH_ERR_1004";
    public static final String AUTH_ERR_1005 = "AUTH_ERR_1005";
    public static final String AUTH_ERR_1006 = "AUTH_ERR_1006";
    public static final String AUTH_ERR_1007 = "AUTH_ERR_1007";
    public static final String AUTH_ERR_1008 = "AUTH_ERR_1008";
    public static final String AUTH_ERR_1009 = "AUTH_ERR_1009";
    public static final String AUTH_ERR_1010 = "AUTH_ERR_1010";

    public static final String AUTH_ERR_1011 ="AUTH_ERR_1011";
    public static final String AUTH_ERR_1012 ="AUTH_ERR_1012";
    public static final String AUTH_ERR_1013 ="AUTH_ERR_1013";
    public static final String AUTH_ERR_1014 ="AUTH_ERR_1014";
    public static final String AUTH_ERR_1015 ="AUTH_ERR_1015";
    public static final String AUTH_ERR_1016 ="AUTH_ERR_1016";
    public static final String AUTH_ERR_1017 ="AUTH_ERR_1017";


    public static final Map<String, String> codeMessages = new HashMap<>();

    static {

        codeMessages.put(AUTH_1001, "Login Success");
        codeMessages.put(AUTH_1002, "User Created");
        codeMessages.put(AUTH_1003, "Password Update");
        codeMessages.put(AUTH_1004, "Login OTP Success");
        codeMessages.put(AUTH_1005, "Reset Password OTP Success");
        codeMessages.put(AUTH_1006, "Email Verification OTP Success");
        codeMessages.put(AUTH_1007, "Email verified");
        codeMessages.put(AUTH_1008, "Logout Success");
        codeMessages.put(AUTH_1009, "User Profile Updated");
        codeMessages.put(AUTH_1010, "OTP resend");
        codeMessages.put(AUTH_1011, "ActionToken created/Sent");
        codeMessages.put(AUTH_1012, "User Found");


        codeMessages.put(AUTH_ERR_1000, "Invalid Input");
        codeMessages.put(AUTH_ERR_1001, "User already exists");
        codeMessages.put(AUTH_ERR_1002, "Invalid Session");
        codeMessages.put(AUTH_ERR_1003, "Invalid Email");
        codeMessages.put(AUTH_ERR_1004, "Invalid Otp");
        codeMessages.put(AUTH_ERR_1005, "User Not Configured");
        codeMessages.put(AUTH_ERR_1006, "Person Service Down");
        codeMessages.put(AUTH_ERR_1007, "External Service Down");
        codeMessages.put(AUTH_ERR_1008, "Invalid Token");
        codeMessages.put(AUTH_ERR_1009, "User not found");
        codeMessages.put(AUTH_ERR_1010, "Invalid Password");
        codeMessages.put(AUTH_ERR_1011, "Internal Server Error");
        codeMessages.put(AUTH_ERR_1012, "Invalid Credential");
        codeMessages.put(AUTH_ERR_1013, "Invalid SSO code");
        codeMessages.put(AUTH_ERR_1014, "OTP limit exceed");
        codeMessages.put(AUTH_ERR_1015, "Invalid client");
        codeMessages.put(AUTH_ERR_1016, "Invalid Redirect uri");
        codeMessages.put(AUTH_ERR_1017, "User not exists");
    }
}
