package com.example.ikn.model.response.item

import com.google.gson.annotations.SerializedName

data class Items(
    @SerializedName("items")
    val items: ArrayList<Item>
)