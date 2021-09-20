package com.techjays.chatlibrary.api

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.techjays.chatlibrary.Util.AppDialogs
import com.techjays.chatlibrary.Util.Helper
import com.techjays.chatlibrary.Util.Utility
import com.techjays.chatlibrary.api.AppServices.API.API_URL
import com.techjays.chatlibrary.constants.ProjectApplication
import okhttp3.*
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class AppServices {

    object API {

        fun constructUrl(urlKey: String): String {
            return String.format("%s%s", API_URL, urlKey)
        }

        // API Case's
        // User
        const val API_URL = ""
        const val sign_in = "users/signin/"
        const val social_login = "users/social-login/"
        const val employee_sign_up = "users/employer-signup/"
        const val job_seeker_sign_up = "users/jobseeker-signup/"
        const val upload_impression_videos = "vidhire-app/impression-videos/"

        const val forgot_password = "users/forgot-password/"
        const val logout = "users/logout/"
        const val update_token = "users/notification-id/update/"
        const val resend_otp = "users/resend-otp/"
        const val verify_otp = "users/verify-otp/"
        const val update_resend_otp = "users/update-mobile-number/resend-otp/"
        const val update_verify_otp = "users/update-mobile-number/verify-otp/"
        const val update_mobile_number = "users/update-mobile-number/"
        const val forgot_password_send_otp = "users/password-reset/send-otp/"
        const val forgot_password_verify_otp = "users/password-reset/verify-otp/"
        const val reset_password_submit = "users/password-reset/"
        const val change_password_submit = "users/change-password/"
        const val log_out = "users/logout/"
        const val get_profile = "users/myprofile/"
        const val update_profile = "users/myprofile/"
        const val update_profile_pic = "users/upload_profile_pic/"


        // Impression Video
        const val get_videos = "vidhire-app/impression-videos/"
        const val delete_videos = "vidhire-app/impression-videos/delete/"
        const val get_shortlisted_videos = "vidhire-app/shortlisted-videos/"
        const val get_jobSeeker_employee_details = "vidhire-app/jobseeker-employer-details/"
        const val shortlisting = "shortlisting"
        const val get_onboarding_video = "vidhire-app/onboarding-video/"

        const val count = "users/user-stats-count/"

        // Common
        const val menus = "common/menus/"
        const val industry_types = "common/industry-types/"
        const val invite_to_fayvit = "common/invite-to-fayvit/"
        const val give_feedback = "common/give-feedback/"
        const val privacy_policy = "vidhire-app/privacy-policy/"
        const val terms = "vidhire-app/terms-of-use/"
        const val SUBMIT_FEEDBACK = "SUBMIT_FEEDBACK"
        const val app_update = "common/check-app-update/"


        // Notification
        const val notifications = "notifications/"
        const val NotificationCall = "users/call/"
        const val chat_notification = "users/chat-notification/"

    }

    private interface ApiInterface {

        //        ----------------- POST Request ---------------
        @POST
        fun POST(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>,
            @Body body: JsonObject
        ): Call<ResponseBody>

        @POST
        fun POST(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

        @Multipart
        @POST
        fun MULTIPART(
            @Url url: String,
            @PartMap file: HashMap<String, RequestBody>,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

        @Multipart
        @POST
        fun MULTIPART(
            @Url url: String,
            @PartMap file: HashMap<String, RequestBody>
        ): Call<ResponseBody>

//      ----------------- GET Request ---------------

        @GET
        fun GET(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>,
            @QueryMap param: Map<String, String>
        ): Call<ResponseBody>

        @GET
        fun GET(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

        @GET
        fun GET(
            @Url url: String
        ): Call<ResponseBody>

//      ----------------- PUT Request ---------------

        @PUT
        fun PUT(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>,
            @Body body: JsonObject
        ): Call<ResponseBody>

        @Multipart
        @PUT
        fun PUT(
            @Url url: String,
            @PartMap file: HashMap<String, RequestBody>,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

//      ----------------- DELETE Request ---------------

        @DELETE
        fun DELETE(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

        @HTTP(method = "DELETE", path = API.notifications, hasBody = true)
        fun deleteNotification(
            @Body body: JsonObject,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>
    }

    /**
     * Retrofit Initialization
     */
    companion object {

        private var retrofit: Retrofit? = null
        private var okHttpClient: OkHttpClient? = null

        private fun getClient(): Retrofit {

            if (okHttpClient == null) {
                okHttpClient = OkHttpClient.Builder()
                    .cookieJar(CookieJar.NO_COOKIES)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
            }

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(okHttpClient!!)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit as Retrofit
        }


// ################################################################################################

        /**
         * Create RequestBody - text/plain
         */

        private fun requestBody(string: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), string)
        }

        /**
         * Get Error Msg
         * return - Response
         */

        private fun getErrorMsg(t: Throwable, hash: Int): com.techjays.chatlibrary.api.Response? {
            val r = Response()
            r.responseStatus = false
            r.responseMessage = t.message!!
            r.requestType = hash

            Log.d("failure", t.message!!)

            return r
        }

        /**
         * Initiating the api call
         */
        private fun initService(
            c: Context,
            call: Call<ResponseBody>,
            mSerializable: Type,
            mHashCode: String,
            listener: ResponseListener
        ) {
            Log.d("URL --> ", call.request().url().toString())
            Log.d("METHOD --> ", call.request().method())
            call.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
                ) {
                    listener.onResponse(getResponse(c, response, mSerializable, mHashCode))
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.onResponse(getErrorMsg(t, mHashCode.hashCode()))
                }
            })
        }

        /**
         * Get Success and Failure Msg
         * @return - Response
         */

        private fun getResponse(
            context: Context,
            mResponse: retrofit2.Response<ResponseBody>,
            mSerializable: Type,
            mHashCode: String
        ): com.techjays.chatlibrary.api.Response? {
            val response: com.techjays.chatlibrary.api.Response?

            if (!Utility.isInternetAvailable(context)) {
                okHttpClient?.dispatcher()?.cancelAll()
                return null
            }

            if (mResponse.isSuccessful) {
                val body = mResponse.body()?.string()!!
                Log.d("success", body)
                response = Gson().fromJson(body, mSerializable)
            } else {
                try {
                    if (mResponse.code() == 401) { // Unauthorized User / Invalid Token
                        Log.e("unauthorized", mResponse.errorBody()!!.string())
                        Log.e("unauthorized url", mResponse.raw().request().url().toString())
                        okHttpClient?.dispatcher()?.cancelAll()
                        Helper.logout(context as Activity)
                        return null
                    } else {
                        val errorBody = mResponse.errorBody()?.string()!!
                        Log.e("fail", errorBody)
                        response = Gson().fromJson(errorBody, mSerializable)
                        response?.responseStatus = false
                    }
                } catch (e: Exception) {
                    Utility
                    AppDialogs.customOkAction(context, e.message!!)
                    return null
                }
            }
            response?.requestType = mHashCode.hashCode()
            return response
        }

        /**
         * Get Common Header
         * @return - HashMap
         */

        private fun getHeader(): HashMap<String, String> {
            val mHeader = HashMap<String, String>()
            mHeader["Content-Type"] = "application/json"
            mHeader["device"] = ProjectApplication.instance().deviceId
            mHeader["platform"] = ProjectApplication.instance().deviceType

            Log.d("Header --> ", mHeader.toString())

            return mHeader
        }


        private fun getlogoutHeader(): HashMap<String, String> {
            val mHeader = HashMap<String, String>()

            mHeader["device"] = ProjectApplication.instance().deviceId
            mHeader["platform"] = ProjectApplication.instance().deviceType

            Log.d("Header --> ", mHeader.toString())

            return mHeader
        }

        private fun getHeaderContent(): HashMap<String, String> {
            val mHeader = HashMap<String, String>()
            mHeader["Content-Type"] = "application/json"
            Log.d("Header --> ", mHeader.toString())

            return mHeader
        }

        /**
         * Get Auth Header
         * return - HashMap<String, String>
         */

        private fun getAuthHeader(c: Context): HashMap<String, String> {
            val mHeader = HashMap<String, String>()
            mHeader["Content-Type"] = "application/json"
            mHeader["Authorization"] = getAuthToken(c)

            Log.d("Auth Header --> ", mHeader.toString())

            return mHeader
        }

        /**
         * Get Auth Header
         * return - HashMap<String, String>
         */

        private fun getCustomAuthHeader(c: Context, token: String): HashMap<String, String> {
            val mHeader = HashMap<String, String>()
            mHeader["Content-Type"] = "application/json"
            mHeader["Authorization"] = "Token $token"

            Log.d("Auth Header --> ", mHeader.toString())

            return mHeader
        }

        /**
         * Get Auth Header for Multipart
         * return - HashMap<String, String>
         */

        private fun getAuthHeaderPart(c: Context): HashMap<String, String> {
            val mHeader = HashMap<String, String>()
            mHeader["Authorization"] = getAuthToken(c)
            Log.d("Auth Header --> ", mHeader.toString())

            return mHeader
        }

        /**
         * Get AuthToken
         * return - String
         */

        private fun getAuthToken(c: Context): String {
            return "Token "
        }
    }
}

