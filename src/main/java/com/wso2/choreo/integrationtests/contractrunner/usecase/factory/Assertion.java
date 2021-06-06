package com.wso2.choreo.integrationtests.contractrunner.usecase.factory;

import com.wso2.choreo.integrationtests.contractrunner.domain.entity.PathCheck;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public interface Assertion {
    void execute(PathCheck check, Response response, JsonPath contractJsonPath, boolean isHeaderCheck);
}
