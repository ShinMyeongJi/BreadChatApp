package com.techtown.breadchatapp.fragment

import com.techtown.breadchatapp.notification.MyResponse
import com.techtown.breadchatapp.notification.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers(
            "Content-Type:application/json",
            "Authorization:key=AAAAA20J2wI:APA91bHGG8Nx-nnO6cpwMTuf06XXRpMU8V7IBZT6bqKftM5xNSmiX81cXW6r1EwBMplK4FrnJG2ifhmARXllkTS_JTC9L5qUt0m8lM0VPvzBg410h5_hTU_0kc7o987jBXRrJHzlKqh_"
    )

    @POST("fcm/send")
   fun sendNotification(@Body body : Sender) : Call<MyResponse>
}