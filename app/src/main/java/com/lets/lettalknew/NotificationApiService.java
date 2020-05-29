package com.lets.lettalknew;

import com.squareup.okhttp.Response;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationApiService {

    @Headers ( {
            "Content-Type:application/json",
            "Authorization:key=AAAAMoDpXaA:APA91bHYuKgMhAsijkfqEmRbLDABL_52RI6U4M1-h2_mI5iVXs_mMFb4Plh1AfaD_1nAwmCebw2PR4hLJFOa4TM_jD856KPhBcd26PNBi2hzyG1LM_YlGKPPDZrXC_LO3BFSjYRtYthe"

    } )

    @POST("fcm/send")
    Call<NotificationResponse> sendNotification(@Body NotificationSender body);

}
