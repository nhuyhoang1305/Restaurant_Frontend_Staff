package com.uet.restaurant_staff.Retrofit;

import com.uet.restaurant_staff.Model.FCMResponse;
import com.uet.restaurant_staff.Model.FCMSendData;


import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAApKqmIM0:APA91bGQbaqArGcelVDhdhJUyxU13f8Uex7P3GyxCEHKPTcB8PRYplkss3tQIe_UxZYP-WQk60c6TKRQpwVlKMqUs2btzVpHUthqYZZt4WaxBlbeaKnoP_jKPxc1aAF2g2exxcx-uL_h"
    })

    @POST("fcm/send")
    Observable<FCMResponse> sendNotificiaton(@Body FCMSendData body);
}
