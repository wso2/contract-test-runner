package com.wso2.choreo.integrationtests.contractrunner.usecase.factory;

import com.wso2.choreo.integrationtests.contractrunner.domain.entity.PathCheck;
import com.wso2.choreo.integrationtests.contractrunner.domain.entity.PrimitiveAssertParams;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertIsEmpty extends PrimitiveAssertion implements Assertion {
    private static final Logger logger = LogManager.getLogger(AssertIsEmpty.class);

    @Override
    public void execute(PathCheck check, Response response, JsonPath contractJsonPath, boolean isHeaderCheck) {
        PrimitiveAssertParams params = getParams(check, response, contractJsonPath, isHeaderCheck);
        logger.info("Test is being initiated: ".concat(params.getTestDescription()));
        assertThat(params.getActualValue()).as(params.getTestDescription()).isEmpty();
    }
}
