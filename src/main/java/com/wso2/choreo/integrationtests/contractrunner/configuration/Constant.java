package com.wso2.choreo.integrationtests.contractrunner.configuration;

public final class Constant {
    public static final String RESOURCES_PATH = "RESOURCES_PATH";
    public static final String RESPONSE_LOG =
            "\n===============\n RESPONSE:\nurl: {} {}\n body: {} \n===============\n";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String CONTENT_TYPE = "Content-type";

    public static final String TESTS_OUTPUT_PATH = "/test-outputs/logs.log";

    public static final String BEFORE_SUITE_CONFIGS = "beforeSuiteConfigs";
    public static final String AFTER_SUITE_CONFIGS = "afterSuiteConfigs";
    public static final String TEST_LIST = "tests";

    public static final String SUITE_DATA_PROVIDER = "SUITE_DATA_PROVIDER";
    public static final String BASE_URL = "BASE_URL";

    private Constant(){}
}
