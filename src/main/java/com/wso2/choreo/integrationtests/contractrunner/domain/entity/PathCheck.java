package com.wso2.choreo.integrationtests.contractrunner.domain.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PathCheck {

    @SerializedName("jsonPath")
    @Expose
    private String path;
    @SerializedName("typeValue")
    @Expose
    private String typeValue;
    @SerializedName("type")
    @Expose
    private String type;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
