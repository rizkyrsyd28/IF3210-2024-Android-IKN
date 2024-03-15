package com.example.ikn.model.response

import com.example.ikn.model.response.item.Items
import com.google.gson.annotations.SerializedName

data class BillResponse (
    @SerializedName("items")
    val items: Items
)