package com.wso2.choreo.integrationtests.contractrunner.configuration;

import io.restassured.config.RestAssuredConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SuiteConfig {
    private static final Map<String, String> mainEnvs = new ConcurrentHashMap<>();
    private static RestAssuredConfig restAssuredConfig;
    private static Map<String, String> suiteEnvs;
    private static Map<String, String> testEnvs;
    private static String[] beforeSuiteContracts;
    private static String[] beforeSuitePreContracts;
    private static String[] afterSuiteContracts;

    public static RestAssuredConfig getRestAssuredConfig() {
        return restAssuredConfig;
    }

    public static void setRestAssuredConfig(RestAssuredConfig restAssuredConfig) {
        SuiteConfig.restAssuredConfig = restAssuredConfig;
    }

    public static Map<String, String> getEnvs() {
        return (testEnvs != null) ? testEnvs : ((suiteEnvs != null) ? suiteEnvs : mainEnvs);
    }

    public static void putEnv(String key, String value, EnvLevel level) {
        switch (level) {
            case GLOBAL:
                mainEnvs.put(key, value);
                break;
            case SUITE:
                suiteEnvs.put(key, value);
                break;
            case TEST:
            default:
                testEnvs.put(key, value);
                break;
        }
    }

    public static void putEnvs(Map<String, String> envs, EnvLevel level) {
        switch (level) {
            case GLOBAL:
                SuiteConfig.mainEnvs.putAll(envs);
                break;
            case SUITE:
                SuiteConfig.suiteEnvs.putAll(envs);
                break;
            case TEST:
            default:
                SuiteConfig.testEnvs.putAll(envs);
                break;
        }
    }

    public static String[] getBeforeSuiteContracts() {
        return beforeSuiteContracts;
    }

    public static void setBeforeSuiteContracts(String[] beforeSuiteContracts) {
        SuiteConfig.beforeSuiteContracts = beforeSuiteContracts;
    }

    public static String[] getBeforeSuitePreContracts() {
        return beforeSuitePreContracts;
    }

    public static void setBeforeSuitePreContracts(String[] beforeSuitePreContracts) {
        SuiteConfig.beforeSuitePreContracts = beforeSuitePreContracts;
    }

    public static String[] getAfterSuiteContracts() {
        return afterSuiteContracts;
    }

    public static void setAfterSuiteContracts(String[] afterSuiteContracts) {
        SuiteConfig.afterSuiteContracts = afterSuiteContracts;
    }

    public static void initializeSuiteEnvs() {
        SuiteConfig.suiteEnvs = new HashMap<>();
        SuiteConfig.suiteEnvs.putAll(mainEnvs);
    }

    public static void initializeTestEnvs() {
        SuiteConfig.testEnvs = new HashMap<>();
        SuiteConfig.testEnvs.putAll(mainEnvs);
        SuiteConfig.testEnvs.putAll(suiteEnvs);
    }
}
