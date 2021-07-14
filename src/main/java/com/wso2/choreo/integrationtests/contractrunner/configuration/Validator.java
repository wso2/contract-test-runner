package com.wso2.choreo.integrationtests.contractrunner.configuration;

import io.restassured.path.json.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.SkipException;

public class Validator {
    private static final Logger logger = LogManager.getLogger(Validator.class);

    private Validator(){}

    public static boolean isConfigFileValid(String configFileName, JsonPath configJsonPath) {
        var errorMessage = "{} has not been set in {}";
        if (configJsonPath.get(Constant.BEFORE_SUITE_CONFIGS) == null) {
            logger.error(errorMessage, Constant.BEFORE_SUITE_CONFIGS, configFileName);
            throw new SkipException("");
        }
        if (configJsonPath.get(Constant.AFTER_SUITE_CONFIGS) == null) {
            logger.error(errorMessage, Constant.AFTER_SUITE_CONFIGS, configFileName);
            return false;
        }
        if (configJsonPath.get(Constant.TEST_LIST) == null) {
            logger.error(errorMessage, Constant.TEST_LIST, configFileName);
            return false;
        }
        return true;
    }
}
