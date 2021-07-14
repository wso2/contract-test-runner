package com.wso2.choreo.integrationtests.contractrunner.domain.entity;

public class PrimitiveAssertParams {
    private final String actualValue;
    private final String expectedValue;
    private final String testDescription;

    public PrimitiveAssertParams(String actualValue, String expectedValue, String testDescription) {
        this.actualValue = actualValue;
        this.expectedValue = expectedValue;
        this.testDescription = testDescription;
    }

    public String getActualValue() {
        return actualValue;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public String getTestDescription() {
        return testDescription;
    }
}
