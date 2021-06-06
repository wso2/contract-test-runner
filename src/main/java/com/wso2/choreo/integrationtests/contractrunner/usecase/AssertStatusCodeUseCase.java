package com.wso2.choreo.integrationtests.contractrunner.usecase;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertStatusCodeUseCase implements UseCase {
    private static final Logger logger = LogManager.getLogger(AssertStatusCodeUseCase.class);

    private final Response response;
    private final JsonPath contractJsonPath;

    public AssertStatusCodeUseCase(Response response, JsonPath contractJsonPath) {
        this.response = response;
        this.contractJsonPath = contractJsonPath;
    }

    @Override
    public void execute() {
        String testDescription = "Contract: ".concat(contractJsonPath.getString("name"))
                .concat(" Check: StatusCodeCheck");
        logger.info("Test is being initiated: ".concat(testDescription));
        if (contractJsonPath.get("response.statusCode") == null) {
            Assert.fail(testDescription.concat(" Error: Response in the contract does not contain a status code"));
            return;
        }
        if (contractJsonPath.get("assertions.statusCodeCheck") == null
                || contractJsonPath.getBoolean("assertions.statusCodeCheck"))
            assertThat(response.statusCode()).as(testDescription).isEqualTo(contractJsonPath.getInt("response.statusCode"));
    }
}
