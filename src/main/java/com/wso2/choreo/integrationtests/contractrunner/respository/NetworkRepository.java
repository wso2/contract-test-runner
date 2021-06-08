package com.wso2.choreo.integrationtests.contractrunner.respository;

import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public interface NetworkRepository {
    Response getResponse(JsonPath contractJsonPath, String url, RestAssuredConfig config);

    void setAuthenticationAttributes(String authType);
}
