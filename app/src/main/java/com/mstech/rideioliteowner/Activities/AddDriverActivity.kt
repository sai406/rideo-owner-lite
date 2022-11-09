package com.mstech.rideioliteowner.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.mstech.rideiodriver.Utils.RetrofitFactory
import com.mstech.rideiodriverlite.Model.SharedKey
import com.mstech.rideiodriverlite.Utils.MyUtils
import com.mstech.rideioliteowner.Adapter.DriverListAdapter
import com.mstech.rideioliteowner.Model.DriverDetails
import com.mstech.rideioliteowner.Model.DriverListResponse
import com.mstech.rideioliteowner.R
import kotlinx.android.synthetic.main.activity_add_driver.*
import kotlinx.android.synthetic.main.activity_driver_list.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AddDriverActivity : AppCompatActivity() {
    var driverid =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_driver)
        try {
            driverid = intent.getIntExtra("driverid",0).toString()
            ToastUtils.showShort(driverid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(!driverid.equals("0")){
            getDriverInfo(driverid);
        }
        submit.setOnClickListener(View.OnClickListener {
            if (!NetworkUtils.isConnected()){
                ToastUtils.showShort("No Internet Connection")
            }else if (firstname.text.length==0){
                ToastUtils.showShort("Enter First Name")
            }else if (lastname.text.length==0){
                ToastUtils.showShort("Enter Last Name")
            }else if(mobile.text.length==0){
                ToastUtils.showShort("Enter Mobile Number")
            }else if (pin.text.length==0){
                ToastUtils.showShort("Enter Pin")
            }else if (address.text.length==0){
                ToastUtils.showShort("Enter Address")
            }else{
                addDriver();
            }
        })
    }

    private fun getDriverInfo(driverid: String) {

        RetrofitFactory.client
            .getDriverInfo(
                driverid
            )
            ?.enqueue(object : Callback<DriverDetails> {
                override fun onResponse(
                    call: Call<DriverDetails>,
                    response: Response<DriverDetails>
                ) {
                    MyUtils.showProgress(this@AddDriverActivity, false)
                    if (response.isSuccessful
                    ) {
                        firstname.setText(response.body()?.firstName)
                        lastname.setText(response.body()?.lastName)
                        mobile.setText(response.body()?.mobile)
                        address.setText(response.body()?.address)
                        emailid.setText(response.body()?.email)
                    }
                }

                override fun onFailure(
                    call: Call<DriverDetails>,
                    t: Throwable
                ) {
                    ToastUtils.showShort(t.message)
                    MyUtils.showProgress(this@AddDriverActivity, false)

                }
            })



    }

    private fun addDriver() {
        MyUtils.showProgress(this, true)
        var obj = JSONObject()
        obj.put("DriverId", driverid)
        obj.put("OwnerId", SPStaticUtils.getString(SharedKey.OWNER_ID,""))
        obj.put("FirstName", firstname.text)
        obj.put("LastName",lastname.text)
        obj.put("Email",emailid.text)
        obj.put("Mobile",mobile.text)
        obj.put("Password",pin.text)
        obj.put("Gender","m")
        obj.put("DOB","")
        obj.put("Address",address.text)
        obj.put("Location","")
        obj.put("CountryId","1")
        obj.put("IsActive","1")
        LogUtils.e(obj.toString())
        var body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            ((obj)).toString()
        )
        RetrofitFactory.client
            .addUpdateDriver(
                body
            )
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    MyUtils.showProgress(this@AddDriverActivity, false)
                    if (response.isSuccessful
                    ) {
                        try {
                            if (response.equals("null")){
                                ToastUtils.showShort("Invalid Login")
                            }else {
                                var array = JSONObject(response.body()?.string())
                                SPStaticUtils.put(SharedKey.ISLOGIN, "true")
                                SPStaticUtils.put(
                                    SharedKey.FIRSTNAME,
                                    array.getString("firstName")
                                )
                                SPStaticUtils.put(
                                    SharedKey.LASTNAME,
                                    array.getString("lastName")
                                )
                                SPStaticUtils.put(
                                    SharedKey.MOBILE,
                                    array.getString("mobile")
                                )
                                SPStaticUtils.put(
                                    SharedKey.EMAIL,
                                    array.getString("email")
                                )
                                SPStaticUtils.put(
                                    SharedKey.ADDRESS,
                                    array.getString("address")
                                )
                                SPStaticUtils.put(
                                    SharedKey.OWNER_ID,
                                    array.getString("ownerId")
                                )
                                ToastUtils.showShort("Data Updated Succesfully")
                                onBackPressed()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {
                    ToastUtils.showShort(t.message)
                    MyUtils.showProgress(this@AddDriverActivity, false)

                }
            })

    }
}