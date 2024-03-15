package com.example.ikn.model.response.item

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("name")
    val name: String,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("price")
    val price: Double
)
