package com.wso2.choreo.integrationtests.contractrunner.usecase;

import com.wso2.choreo.integrationtests.contractrunner.domain.entity.PathCheck;
import com.wso2.choreo.integrationtests.contractrunner.usecase.factory.Assertion;
import com.wso2.choreo.integrationtests.contractrunner.usecase.factory.AssertionsFactory;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;

public class AssertHeadersUseCase implements UseCase {
    private final Response response;
    private final JsonPath contractJsonPath;

    public AssertHeadersUseCase(Response response, JsonPath contractJsonPath) {
        this.response = response;
        this.contractJsonPath = contractJsonPath;
    }

    @Override
    public void execute() {
        List<PathCheck> checks = contractJsonPath
                .getList("assertions.headersPathCheck", PathCheck.class);
        AssertionsFactory factory = new AssertionsFactory();
        if (checks.size() > 0) {
            for (PathCheck check : checks) {
                Assertion assertion = factory.get(check.getType());
                assertion.execute(check, response, contractJsonPath, true);
            }
        }
    }
}
