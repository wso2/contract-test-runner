package com.wso2.choreo.integrationtests.contractrunner.usecase.factory;

import com.wso2.choreo.integrationtests.contractrunner.domain.entity.PathCheck;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class AssertIsObject extends AdvancedAssertion implements Assertion {
    private static final Logger logger = LogManager.getLogger(AssertIsObject.class);

    @Override
    public void execute(PathCheck check, Response response, JsonPath contractJsonPath, boolean isHeaderCheck) {
        String testDescription = getTestDescription(check, contractJsonPath, isHeaderCheck);
        logger.info("Test is being initiated: ".concat(testDescription));
        if (!isJsonObject(check.getPath(), contractJsonPath))
            Assert.fail(testDescription.concat(" Error: Not a JSON object"));
    }
}
