package com.wso2.choreo.integrationtests.contractrunner.usecase;

import io.restassured.path.json.JsonPath;
import org.testng.Assert;

public class AssertMetaDataUseCase implements UseCase {
    private final JsonPath contractJsonPath;
    private final String contractName;

    public AssertMetaDataUseCase(String contractName, JsonPath contractJsonPath) {
        this.contractName = contractName;
        this.contractJsonPath = contractJsonPath;
    }

    @Override
    public void execute() {
        if (contractJsonPath.get("name") == null)
            Assert.fail("No test name found in the contract: ".concat(contractName));
        if (contractJsonPath.get("request") == null)
            Assert.fail("Request section is missing in the contract: "
                    .concat(contractJsonPath.getString("name")));
        if (contractJsonPath.get("request.method") == null || contractJsonPath.get("request.url") == null)
            Assert.fail("Request method or url is missing in the contract: "
                    .concat(contractJsonPath.getString("name")));
    }
}
