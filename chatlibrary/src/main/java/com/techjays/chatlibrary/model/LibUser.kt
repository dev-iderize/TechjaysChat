package com.techjays.chatlibrary.model

import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response
/**
 * Created by Mathan on 23/07/21.
 **/
class LibUser : Response() {

    @SerializedName("data")
    var mLibUser: LibUser? = null

    @SerializedName("user_id")
    var mUserId = -1

    @SerializedName("token")
    var mUserToken = ""

    @SerializedName("username")
    var mUserName = ""

    @SerializedName("first_name")
    var mFirstName = ""

    @SerializedName("last_name")
    var mLastName = ""

    @SerializedName("linkedin_profile")
    var mLinkedinProfile = ""

    @SerializedName("company_name")
    var mCompanyName = ""

    @SerializedName("mobile_number")
    var mMobileNumber = ""

    @SerializedName("company_logo")
    var mCompanyLogo = ""

    @SerializedName("email")
    var mEmail = ""

    @SerializedName("website")
    var mWebsite = ""

    @SerializedName("zip_code")
    var mZipCode = ""

    @SerializedName("country_code")
    var mCountryCode = ""

    @SerializedName("source")
    var mSource = "app"

    @SerializedName("user_type")
    var mUserType = ""

    @SerializedName("resume")
    var mResume = ""

    @SerializedName("video")
    var mVideo = ""

    @SerializedName("profile_pic")
    var mProfilePic = ""

    @SerializedName("is_impression_already_exist")
    var isVideoAdded = false

    var mSeenIntroVideo = false

    @SerializedName("is_login_first_time")
    var mLoginFirstTime = false

    var mOTPCode = ""
    var mPassword = ""
    var mCurrentPassword = ""
    var mSelectedIndustry = ""
    var mNewPassword =""
    var mConfirmPassword =""
}
