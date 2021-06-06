package com.wso2.choreo.integrationtests.contractrunner.usecase.factory;

import com.wso2.choreo.integrationtests.contractrunner.domain.entity.PathCheck;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertStructure extends AdvancedAssertion implements Assertion {
    private static final Logger logger = LogManager.getLogger(AssertStructure.class);

    @Override
    public void execute(PathCheck check, Response response, JsonPath contractJsonPath, boolean isHeaderCheck) {
        String testDescription = getTestDescription(check, contractJsonPath, isHeaderCheck);
        logger.info("Test is being initiated: ".concat(testDescription));
        contractJsonPath.setRootPath("response.body");
        Map<String, String> expectedMap;
        try {
            expectedMap = contractJsonPath
                    .getJsonObject(check.getPath());
        } catch (ClassCastException ignored) {
            Assert.fail(testDescription
                    .concat(" Error: Not a JSON object. "
                            .concat("Can not compare the structures of the non Json objects")));
            return;
        }
        contractJsonPath.setRootPath("");
        Set<String> expectedMapKeys = expectedMap.keySet();
        Map<String, String> actualMap = response.jsonPath().getJsonObject(check.getPath());
        Set<String> actualMapKeys = actualMap.keySet();
        assertThat(actualMapKeys).as(testDescription).containsAll(expectedMapKeys);
    }
}
