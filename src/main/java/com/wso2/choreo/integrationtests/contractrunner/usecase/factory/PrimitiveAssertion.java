package com.wso2.choreo.integrationtests.contractrunner.usecase.factory;

import com.wso2.choreo.integrationtests.contractrunner.domain.entity.PathCheck;
import com.wso2.choreo.integrationtests.contractrunner.domain.entity.PrimitiveAssertParams;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public abstract class PrimitiveAssertion {
    protected PrimitiveAssertParams getParams(PathCheck check, Response response, JsonPath contractJsonPath,
                                              boolean isHeaderCheck) {
        String testDescription = "Contract: ".concat(contractJsonPath.getString("name"))
                .concat(" Check: ").concat((isHeaderCheck) ? "HeadersPathCheck" : "BodyPathCheck")
                .concat(" Type: ".concat(check.getType()))
                .concat(" JsonPath: ").concat(check.getPath());

        JsonPath parsedResponse = response.body().jsonPath();
        String actualValue;
        String expectedValue;
        if (isHeaderCheck) {
            actualValue = response.headers().getValue(check.getPath());
            expectedValue = contractJsonPath.setRootPath("response.headers")
                    .getString(check.getPath());
        } else {
            actualValue = parsedResponse.getString(check.getPath());
            expectedValue = contractJsonPath.setRootPath("response.body")
                    .getString(check.getPath());
        }
        contractJsonPath.setRootPath("");
        return new PrimitiveAssertParams(actualValue, expectedValue, testDescription);
    }
}
