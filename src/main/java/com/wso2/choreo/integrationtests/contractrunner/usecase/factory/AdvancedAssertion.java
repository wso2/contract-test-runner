package com.wso2.choreo.integrationtests.contractrunner.usecase.factory;

import com.wso2.choreo.integrationtests.contractrunner.domain.entity.PathCheck;
import io.restassured.path.json.JsonPath;

public abstract class AdvancedAssertion {
    protected boolean isJsonObject(String path, JsonPath contractJsonPath) {
        try {
            contractJsonPath.setRootPath("response.body");
            return (contractJsonPath.getMap(path, String.class, String.class) != null);
        } catch (Exception ignored) {
            return false;
        } finally {
            contractJsonPath.setRootPath("");
        }
    }

    protected String getTestDescription(PathCheck check, JsonPath contractJsonPath,
                                        boolean isHeaderCheck) {
        return "Contract: ".concat(contractJsonPath.getString("name"))
                .concat(" Check: ").concat((isHeaderCheck) ? "HeadersPathCheck" : "BodyPathCheck")
                .concat(" Type: ".concat(check.getType()))
                .concat(" JsonPath: ").concat(check.getPath());
    }
}
