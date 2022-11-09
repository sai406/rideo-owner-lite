package com.mstech.rideiodriver.Utils

import com.mstech.rideiodriverlite.Model.AlertsResponse
import com.mstech.rideioliteowner.Model.DriverDetails
import com.mstech.rideioliteowner.Model.DriverListResponse
import com.mstech.rideioliteowner.Model.GeofenceListResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("services/trackservices.asmx/DriverLogin")
    fun loginMethod(
        @Query("mobile") mobile: String?, @Query(
            "pin"
        ) pin: String?, @Query("mid") mid: String?
    ): Call<ResponseBody>?

    @GET("driver/getdriverlimitations")
    fun getDriverLimits(@Query("driverid") driverId: String?): Call<ResponseBody>?
    @GET("driver/getdriverinfo")
    fun getDriverInfo(@Query("driverid") driverId: String?): Call<DriverDetails>?
//
//    @GET("api/track/GetDriverCompanions")
//    fun getCompanionList(@Query ("OwnerId") ownerId: String? , @Query("DriverId") driverId :String?):Call<List<CompanionListResponse>>
//http://www.launchapps.com.au/api/trips/GetLiveTrack
//
@POST("trips/GetLiveTrack")
fun allLiveVehicles(@Body postdata : RequestBody): Call<ResponseBody>

    //    @Headers("Content-Type: application/json")
//    @POST("api/track/StartEndTrip")
//    fun postStartEndTrip(@Body  postdata:String):Call<StartEndResponse>
//
    @Headers("Content-Type: application/json")
    @POST("owner/GetDriversGeofences")
    fun getgeofenceList(@Body postdata: RequestBody): Call<List<GeofenceListResponse>>

    @Headers("Content-Type: application/json")
    @POST("trips/getalertsdata")
    fun getAlertsData(@Body postdata : RequestBody):Call<MutableList<AlertsResponse>>


  @Headers("Content-Type: application/json")
    @POST("owner/GetDrivers")
    fun getdriverlist(@Body postdata : RequestBody):Call<MutableList<DriverListResponse>>

    @Headers("Content-Type: application/json")
    @POST("owner/SetDriverGeoFence")
    fun setGeofence(@Body postdata: RequestBody): Call<ResponseBody>
  @Headers("Content-Type: application/json")
    @POST("owner/SetDriverSpeedLimit")
    fun setSpeedLimit(@Body postdata: RequestBody): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("driver/AddDriver")
    fun addUpdateDriver(@Body postdata: RequestBody): Call<ResponseBody>

//@Headers("Content-Type: application/json")
//    @POST("api/track/GetTripHistory")
//    fun getTripHistory(@Body postdata : RequestBody):Call<PlaybackAlerts>
//
//

}