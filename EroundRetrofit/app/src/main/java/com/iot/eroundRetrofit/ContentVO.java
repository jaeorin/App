package com.iot.eroundRetrofit;

import com.google.gson.annotations.SerializedName;

public class ContentVO {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private ContentVOData data;
}