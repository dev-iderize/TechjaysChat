package com.techjays.chatlibrary.api

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.constants.ProjectApplication
import com.techjays.chatlibrary.model.LibChatList
import com.techjays.chatlibrary.model.LibChatMessages
import com.techjays.chatlibrary.model.LibChatSocketMessages
import com.techjays.chatlibrary.util.AppDialogs
import com.techjays.chatlibrary.util.Helper
import com.techjays.chatlibrary.util.Utility
import com.techjays.chatlibrary.util.Utility.getMimeType
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class LibAppServices {

    object API {

        fun constructUrl(urlKey: String): String {
            return String.format("%s%s", ChatLibrary.instance.baseUrl, urlKey)
        }

        fun constructVidRivalUrl(urlKey: String): String {
            return String.format("%s%s", "https://dev-myvidrivals.myvidhire.com/api/v1/", urlKey)
        }

        // API Case's

        // Notification
        const val notifications = "notifications/"

        //Chat
        const val get_chat_token = "chat/token/"
        const val chat_list = "chat/chat-lists/"
        const val get_chat_message = "chat/chat-messages/"
        const val delete_chats = "chat/delete-chat-list/"
        const val delete_messages = "chat/delete-chat-messages/"
        const val upload_file = "chat/file-upload/"
        const val upload_image = "chat/file-upload/"
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
                    .baseUrl(ChatLibrary.instance.baseUrl)
                    .client(okHttpClient!!)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit as Retrofit
        }

        /**
         * Get Chat
         * Method - GET
         * Auth token needed
         */

        fun getChatList(c: Context, offset: Int, limit: Int, listener: ResponseListener) {
            try {
                val apiService = getClient().create(ApiInterface::class.java)
                val mHashCode = API.chat_list
                val mURL = API.constructUrl(mHashCode)

                val mParam = HashMap<String, String>()
                mParam["offset"] = offset.toString()
                mParam["limit"] = limit.toString()

                val call = apiService.GET(mURL, getAuthHeader(c), mParam)
                initService(c, call, LibChatList::class.java, mHashCode, listener)
                Log.d("mParam --> ", mParam.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getChatMessage(
            c: Context,
            offset: Int,
            limit: Int,
            userId: String,
            duelId:String,
            listener: ResponseListener
        ) {
            try {
                val apiService = getClient().create(ApiInterface::class.java)
                val mHashCode = API.get_chat_message
                val mURL = API.constructUrl(mHashCode)

                val mParam = HashMap<String, String>()
                mParam["offset"] = offset.toString()
                mParam["limit"] = limit.toString()
                mParam["to_user_id"] = userId
                mParam["duel_id"] = duelId

                val call = apiService.GET(mURL, getAuthHeader(c), mParam)
                initService(c, call, LibChatMessages::class.java, mHashCode, listener)
                Log.d("mParam --> ", mParam.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Delete Chat list - Multiple / Single
         * Method - POST
         */
        fun deleteChats(c: Context, ids: String, listener: ResponseListener) {
            try {
                val apiService = getClient().create(ApiInterface::class.java)
                val mHashCode = API.delete_chats
                val mURL = API.constructUrl(mHashCode)

                val mObject = JsonObject()
                mObject.addProperty("to_user_id", ids)

                val call = apiService.POST(mURL, getAuthHeader(c), mObject)
                initService(c, call, Response::class.java, mHashCode, listener)
                Log.d("Param --> ", mObject.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun deleteMessages(
            c: Context,
            user_id: Int,
            isforme: Boolean,
            ids: String,
            listener: ResponseListener
        ) {
            try {
                val apiService = getClient().create(ApiInterface::class.java)
                val mHashCode = API.delete_messages
                val mURL = API.constructUrl(mHashCode)
                val mObject = JsonObject()
                mObject.addProperty("to_user_id", user_id)
                mObject.addProperty("message_ids", ids)
                if (isforme)
                    mObject.addProperty("delete_message_type", "for_me")
                else
                    mObject.addProperty("delete_message_type", "everyone")
                val call = apiService.POST(mURL, getAuthHeader(c), mObject)
                initService(c, call, Response::class.java, mHashCode, listener)
                Log.d("Param --> ", mObject.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun fileUpload(c: Context, chatMessages: LibChatMessages, listener: ResponseListener) {
            try {
                val apiService = getClient().create(ApiInterface::class.java)
                val mHashCode = API.upload_file
                val mURL = API.constructUrl(mHashCode)
                val mParam = HashMap<String, RequestBody>()

                val file = File(chatMessages.mFile)
                Utility.log(file.toString())
                val requestBody =
                    RequestBody.create(
                        MediaType.parse(
                            getMimeType(chatMessages.mFile)
                        ), file
                    )
                mParam["file\"; filename=\"" + file.name] = requestBody
                mParam["file_type"] = requestBody("pdf")
                /*mParam["to_user_id"] = requestBody(chatMessages.mToUserId)*/

                val call = apiService.MULTIPART(mURL, mParam, getAuthHeaderPart(c))
                initService(c, call, LibChatSocketMessages::class.java, mHashCode, listener)
                Log.d("Param --> ", mParam.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun mImageVideoUpload(c: Context, path: String, type: String, listener: ResponseListener) {
            try {
                val apiService = getClient().create(ApiInterface::class.java)
                val mHashCode = API.upload_image
                val mURL = API.constructVidRivalUrl(mHashCode)
                val mParam = HashMap<String, RequestBody>()

                val file = File(path)
                Utility.log(file.toString())
                val requestBody =
                    RequestBody.create(
                        MediaType.parse(
                            getMimeType(path)
                        ), file
                    )
                mParam["file\"; filename=\"" + file.name] = requestBody
                mParam["file_type"] = requestBody(type)
                /*mParam["to_user_id"] = requestBody(chatMessages.mToUserId)*/

                val call = apiService.MULTIPART(mURL, mParam, getAuthHeaderPartVidrival(c))
                initService(c, call, LibChatSocketMessages::class.java, mHashCode, listener)
                Log.d("Param --> ", mParam.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

        private fun getErrorMsg(t: Throwable, hash: Int): Response? {
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
        ): Response? {
            val response: Response?

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

        private fun getAuthHeaderPartVidrival(c: Context): HashMap<String, String> {
            val mHeader = HashMap<String, String>()
            mHeader["Authorization"] = "token 1c40b92d06bc7ec7744b60bd04e86ad52332264d"
                Log.d("Auth Header --> ", mHeader.toString())

            return mHeader
        }

        /**
         * Get AuthToken
         * return - String
         */

        private fun getAuthToken(c: Context): String {
            return "Token ${ChatLibrary.instance.authToken}"
        }
    }
}

