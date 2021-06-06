package com.wso2.choreo.integrationtests.contractrunner;

public interface ContractRunnable {
    void initTestNG(String configDirectory);

    void initSuite(String configName);

    void initTest(String envFile);

    void runTest(String contractName, boolean skipTests, boolean skipPostConditions);

    void endSuite();
}
