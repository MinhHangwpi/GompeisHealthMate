package com.example.cs528finalproject.utils

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class NutritionXRequest(
    method: Int,
    url: String,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener,
    appId: String,
    apiKey: String
) : StringRequest(method, url, listener, errorListener) {

    private val headers = HashMap<String, String>()

    init {
        headers["x-app-id"] = appId
        headers["x-app-key"] = apiKey
    }

    override fun getHeaders(): MutableMap<String, String> { return headers }
}