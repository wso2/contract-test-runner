package com.wso2.choreo.integrationtests.contractrunner.domain.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomEnv {

    @SerializedName("jsonPath")
    @Expose
    private String path;
    @SerializedName("key")
    @Expose
    private String key;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
