package com.uet.restaurant_staff.Retrofit;

import com.uet.restaurant_staff.Model.GetKeyModel;
import com.uet.restaurant_staff.Model.MaxOrderModel;
import com.uet.restaurant_staff.Model.OrderDetailModel;
import com.uet.restaurant_staff.Model.OrderModel;
import com.uet.restaurant_staff.Model.RestaurantOwnerModel;
import com.uet.restaurant_staff.Model.TokenModel;
import com.uet.restaurant_staff.Model.UpdateRestaurantOwnerModel;

import java.util.Map;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IRestaurantAPI {

    @GET("getkey")
    Observable<GetKeyModel> getKey(@Query("fbid") String fbid);

    @GET("restaurantowner")
    Observable<RestaurantOwnerModel> getRestaurantOwner(@HeaderMap Map<String, String> headers);

    @POST("restaurantowner")
    @FormUrlEncoded
    Observable<UpdateRestaurantOwnerModel> updateRestaurantOwner(@HeaderMap Map<String, String> headers,
                                                                 @Query("userPhone") String userPhone,
                                                                 @Query("name") String name,
                                                                 @Query("restaurantId") int id,
                                                                 @Query("status") int status);

    @GET("orderbyrestaurant")
    Observable<OrderModel> getOrder(@HeaderMap Map<String, String> headers,
                                    @Query("restaurantId") String restaurantId,
                                    @Query("from") int from,
                                    @Query("to") int to);
    @GET("maxorderbyrestaurant")
    Observable<MaxOrderModel> getMaxOrder(@HeaderMap Map<String, String> headers,
                                          @Query("restaurantId") String restaurantId);

    @GET("orderdetailbyrestaurant")
    Observable<OrderDetailModel> getOrderDetailModel(@HeaderMap Map<String, String> headers,
                                                     @Query("orderId") int orderId);

    @GET("token")
    Observable<TokenModel> getToken(@HeaderMap Map<String, String> headers,
                                    @Query("key") String key);

    @POST("token")
    @FormUrlEncoded
    Observable<TokenModel> updateTokenToServer(@HeaderMap Map<String, String> headers,
                                               @Field("token") String token);
}
