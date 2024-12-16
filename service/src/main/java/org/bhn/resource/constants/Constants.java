package org.bhn.resource.constants;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class Constants {
    public static final String WIDGET_SIGN_IN = "WIDGET_SIGN_IN";
    public static final String OTP_SIGN_IN = "OTP_SIGN_IN";

    public static final String INTERNAL_SERVER_ERROR ="INTERNAL_SERVER_ERROR";

    public static final String EMAIL_REGEX =  "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";

    public static final String OTP_EMAIL = "OTP_EMAIL";
    public static final String OTP_RETRY = "OTP_RETRY";
    public static final String OTP = "OTP";
    public static final String FLOW = "FLOW";

//    public static final String EMAIL_REGEX =  "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final String REGISTER_ACTION_TOKEN = "REGISTER_ACTION_TOKEN";


}
