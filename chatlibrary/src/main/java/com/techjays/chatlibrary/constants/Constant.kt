package com.techjays.chatlibrary.constants

import android.Manifest

/**
 * Created by Mathan on 23/07/21.
 **/
object Constant {

    internal var PREF_FILE_NAME = "vidhire"

    const val REQUEST_CODE_PERMISSION = 2

    var MOBILE_NUMBER = ""
    var COUNTRY_CODE = ""
    var VERIFIED_OTP = ""

    var NO_UPDATE = 3
    var LIGHT_UPDATE = 2
    var FORCE_UPDATE = 1

    const val GET_PROFILE_QR_CODE = "profile_qr"
    const val GET_PROFILE_URL = "profile_url"

    const val EMPLOYER_USER = "Employer"
    const val JOB_SEEKER_USER = "Job Seeker"

    const val CHAT_TYPE_MESSAGE = "message"
    const val CHAT_TYPE_FILE = "file"

    const val CHAT_NOTIFICATION = "CHAT"
    const val VIDEO_NOTIFICATION = "VIDEO"
    const val TWILIO_NOTIFICATION = "TWILIO"

    // Date/Time Formats
    var DD_MM_YY = "dd-MM-yyyy" // 06-12-1993
    var YY_MM_DD = "yyyy-MM-dd" // 1993-12-06
    var YY_MM_DD_SLASH = "yyyy/MM/dd" // 1993/12/06
    var DD_MMM_YY = "dd MMM yyyy" // 06 Dec 1993
    var DD_MMM_YY_ZONE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" // 2019-03-07T16:00:00.000-05:00
    var HH_MM = "HH:mm" // 20:30
    var HH_MM_AA = "hh:mm aa" // 10:30 AM

    const val OTP_REQUEST = 1
    const val OTP_VERIFY = 2

    const val OTP_SIGN_UP = 1
    const val OTP_FORGOT_PASSWORD = 2

    var VIDEO_VIEW = "VIDEO_VIEW"
    var ADD_VIDEO_VIEW = "ADD_VIDEO_VIEW"

    const val ANALYTICS_LOGIN = "login"
    const val ANALYTICS_LOGOUT = "logout"
    const val ANALYTICS_SIGNUP = "signup"
    const val ANALYTICS_USER_FIREBASE_TOKEN = "user_firebase_token_update_success"


    var CAMERA = Manifest.permission.CAMERA
    var WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    var READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
}