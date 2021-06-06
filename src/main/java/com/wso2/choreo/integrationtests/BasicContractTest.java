package com.wso2.choreo.integrationtests;

import com.wso2.choreo.integrationtests.contractrunner.ContractRunnable;
import com.wso2.choreo.integrationtests.contractrunner.application.ContractRunner;
import org.testng.annotations.*;

public class BasicContractTest {
    private final ContractRunnable runner = new ContractRunner();

    @BeforeSuite
    @Parameters("configName")
    public void beforeSuite(@Optional String configName) {
        runner.initSuite(configName);
    }

    @BeforeTest
    @Parameters("envFile")
    public void beforeTest(@Optional String envFile) {
        runner.initTest(envFile);
    }

    @Test
    @Parameters({"contractNameOrDirectory", "skipTests", "skipPostConditions"})
    public void basicContractTest(String contractNameOrDirectory, @Optional("false") String skipTests, @Optional("false") String skipPostConditions) {
        runner.runTest(contractNameOrDirectory, Boolean.parseBoolean(skipTests), Boolean.parseBoolean(skipPostConditions));
    }

    @AfterSuite
    @Parameters("configName")
    public void afterSuite(@Optional String configName) {
        if (configName != null)
            runner.endSuite();
    }
}
