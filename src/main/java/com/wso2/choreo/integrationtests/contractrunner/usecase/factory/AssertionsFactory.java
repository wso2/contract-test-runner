package com.wso2.choreo.integrationtests.contractrunner.usecase.factory;

import com.wso2.choreo.integrationtests.contractrunner.domain.entity.CheckType;

public class AssertionsFactory {
    public Assertion get(String assertionType) {
        switch (CheckType.valueOf(assertionType)) {
            case IS_EMPTY:
                return new AssertIsEmpty();
            case REGEX:
                return new AssertRegex();
            case STRUCTURE:
                return new AssertStructure();
            case IS_ARRAY:
                return new AssertIsArray();
            case IS_OBJECT:
                return new AssertIsObject();
            case IS_NOT_EMPTY:
                return new AssertIsNotEmpty();
            default:
            case EXACT:
                return new AssertExact();
        }
    }
}
