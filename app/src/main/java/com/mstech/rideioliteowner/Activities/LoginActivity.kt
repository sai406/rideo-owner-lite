package com.mstech.rideioliteowner.Activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.mstech.rideiodriverlite.Model.SharedKey
import com.mstech.rideiodriverlite.Utils.AppConstants
import com.mstech.rideiodriverlite.Utils.MyUtils
import com.mstech.rideiodriverlite.Utils.MyUtils.showProgress
import com.mstech.rideioliteowner.R
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class LoginActivity : AppCompatActivity() {
    val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.title = "Owner Login"
        login_btn.setOnClickListener(View.OnClickListener { v ->
            loginMethod()
        })
    }

    private fun loginMethod() {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort("No Internet Connection")
        } else if (TextUtils.isEmpty(mobile.text?.trim())) {
            ToastUtils.showShort("Enter Mobile number")
        } else if (TextUtils.isEmpty(pin.text?.trim())) {
            ToastUtils.showShort("Enter Pin")
        } else {
            showProgress(this,true)
            var params = JSONObject()
            params.put("username", mobile.text.toString())
            params.put("password", pin.text.toString())
            Log.d("", "params " + params.toString())
            var mRequestBody = params.toString()
            val requestQueue = Volley.newRequestQueue(this)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                AppConstants.LOGIN,
                com.android.volley.Response.Listener { response ->
                    Log.d("response", response)
                    MyUtils.showProgress(this, false)
                    if (response.equals("null")){
                        ToastUtils.showShort("Invalid Login")
                    }else{
                        var array = JSONObject(response)
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
                       startActivity(
                           Intent(this,
                               HomeActivity::class.java)
                       )
                    }

                },
                com.android.volley.Response.ErrorListener { error ->
                    Log.e("LOG_VOLLEY", error.toString())
                    MyUtils.showProgress(this, false)

                }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray? {
                    return try {
                        mRequestBody.toByteArray(charset("utf-8"))
                    } catch (uee: UnsupportedEncodingException) {
                        VolleyLog.wtf(
                            "Unsupported Encoding while trying to get the bytes of %s using %s",
                            mRequestBody,
                            "utf-8"
                        )
                        null
                    }
                }
            }

            requestQueue.add(stringRequest)

        }
    }
}
