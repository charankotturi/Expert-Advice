package com.sales.dashboard

import com.sales.dashboard.Constants.Companion.CONTENT_TYPE
import com.sales.dashboard.Constants.Companion.SERVER_KEY
import com.sales.dashboard.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE;charset=UTF-8")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}