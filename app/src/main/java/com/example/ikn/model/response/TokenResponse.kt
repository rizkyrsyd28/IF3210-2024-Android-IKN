package com.example.ikn.model.response

import com.google.gson.annotations.SerializedName

data class TokenResponse (
    @SerializedName("nim")
    val nim : String,
    @SerializedName("iat")
    val iat : Int,
    @SerializedName("exp")
    val exp : Int
)