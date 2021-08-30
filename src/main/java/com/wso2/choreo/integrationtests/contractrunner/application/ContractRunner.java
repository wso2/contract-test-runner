package com.wso2.choreo.integrationtests.contractrunner.application;

import com.google.gson.Gson;
import com.wso2.choreo.integrationtests.BasicContractTest;
import com.wso2.choreo.integrationtests.contractrunner.ContractRunnable;
import com.wso2.choreo.integrationtests.contractrunner.configuration.Constant;
import com.wso2.choreo.integrationtests.contractrunner.configuration.EnvLevel;
import com.wso2.choreo.integrationtests.contractrunner.configuration.SuiteConfig;
import com.wso2.choreo.integrationtests.contractrunner.controller.ContractController;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.JsonPath;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractRunner implements ContractRunnable {
    private static final Logger logger = LogManager.getLogger(ContractRunner.class);
    private final ContractController controller = new ContractController();

    public void initTestNG(String resourcesDirectory) {
        String configDirectory = resourcesDirectory.concat("/configs");
        SuiteConfig.putEnvs(getEnvsMap(configDirectory), EnvLevel.GLOBAL);
        if ((new File(resourcesDirectory.concat("/testng.xml"))).exists()) {
            List<String> suites = new ArrayList<>();
            suites.add(resourcesDirectory.concat("/testng.xml"));
            runTestng(suites);
            return;
        }
        String[] configNames = controller.getJsonFilesInDirectory(configDirectory);
        if (configNames != null) {
            List<XmlSuite> suites = new ArrayList<>();
            var parentSuite = new XmlSuite();
            parentSuite.setName("all suites");
            for (String configName : configNames) {
                var configJsonPath = controller.getJsonPathFromFile(configDirectory.concat("/"
                        .concat(configName)));
                String[] contractNamesList = configJsonPath
                        .getList("tests")
                        .toArray(new String[0]);
                XmlSuite suite = getSuite(contractNamesList, configName);
                suite.setParentSuite(parentSuite);
                parentSuite.getChildSuites().add(suite);
            }
            suites.add(parentSuite);
            runTestng(suites);
        } else {
            logger.error("There are no config files in the directory: {}", configDirectory);
        }
    }

    public void initSuite(String configName) {
        logger.debug("Initializing the suite...");
        controller.setSuiteConfigs(configName);
        if (configName != null) {
            runMultipleContracts(SuiteConfig.getBeforeSuitePreContracts(), true, false);
            runMultipleContracts(SuiteConfig.getBeforeSuiteContracts(), false, false);
        }
    }

    @Override
    public void initTest(String envFile) {
        SuiteConfig.initializeTestEnvs();
        if (envFile != null) {
            SuiteConfig.putEnvs(getEnvsMap(System
                    .getenv(Constant.RESOURCES_PATH).concat("/").concat(envFile)), EnvLevel.TEST);
        }
    }

    public void runTest(String contractNameOrDirectory, boolean skipTests, boolean skipPostConditions) {
        if (SuiteConfig.getEnvs().containsKey(Constant.SUITE_DATA_PROVIDER)) {
            var dataProviderPath = JsonPath.from(SuiteConfig.getEnvs().get(Constant.SUITE_DATA_PROVIDER));
            var arrayPath = JsonPath.from(SuiteConfig.getEnvs().get(dataProviderPath.getString("array")));
            List<String> dataList;
            if (dataProviderPath.get("filterPath") != null) {
                dataList = arrayPath.getList(dataProviderPath
                        .getString("filterPath"));
            } else {
                dataList = arrayPath.getList("");
            }
            dataList.forEach((Object data) -> {
                var jsonDataString = new Gson().toJson(data, Map.class);
                var dataPath = JsonPath.from(jsonDataString);
                SuiteConfig.putEnv(dataProviderPath.getString("mapper.key"),
                        dataPath.getString(dataProviderPath.getString("mapper.jsonPath")), EnvLevel.TEST);
                logger.info("SETTING ".concat(EnvLevel.TEST.toString()).concat(" ENV: ")
                        .concat(dataProviderPath.getString("mapper.key")).concat(" = ")
                        .concat(dataPath.getString(dataProviderPath.getString("mapper.jsonPath"))));
                runContracts(contractNameOrDirectory, skipTests, skipPostConditions);
            });
            return;
        }
        runContracts(contractNameOrDirectory, skipTests, skipPostConditions);
    }

    private void runContracts(String contractNameOrDirectory, boolean skipTests, boolean skipPostConditions) {
        String absolutePath = System.getenv(Constant.RESOURCES_PATH).concat("/contracts/")
                .concat(contractNameOrDirectory);
        if (!(new File(absolutePath)).isDirectory()) {
            controller.runContract(contractNameOrDirectory, skipTests, skipPostConditions);
        } else {
            String[] contractNames = controller.getJsonFilesInDirectory(absolutePath);
            if (contractNames != null && contractNames.length != 0) {
                runMultipleContracts(contractNames, skipTests, skipPostConditions, contractNameOrDirectory);
            } else {
                logger.error("There are no contract files in the directory: {}", absolutePath);
            }
        }
    }

    public void endSuite() {
        logger.debug("Ending the suite...");
        runMultipleContracts(SuiteConfig.getAfterSuiteContracts(), false, true);
    }

    private void runMultipleContracts(String[] contracts, boolean skipTests, boolean skipPostConditions,
                                      String... location) {
        for (String contractName : contracts) {
            controller.runContract((location != null && location.length > 0) ?
                    location[0].concat("/").concat(contractName) : contractName, skipTests, skipPostConditions);
        }
    }

    private <T> void runTestng(List<T> suites) {
        if (suites != null && !suites.isEmpty()) {
            var printStream = IoBuilder.forLogger(LogManager.getRootLogger())
                    .setLevel(Level.DEBUG).buildPrintStream();
            var restAssuredConfig = RestAssuredConfig.config()
                    .logConfig(new LogConfig().defaultStream(printStream).enablePrettyPrinting(true));
            if (SuiteConfig.getEnvs().containsKey("SSL_KEYSTORE_NAME")
                    && SuiteConfig.getEnvs().containsKey("SSL_KEYSTORE_PASSWORD")
                    && SuiteConfig.getEnvs().containsKey("SSL_TRUSTSTORE_NAME")
                    && SuiteConfig.getEnvs().containsKey("SSL_TRUSTSTORE_PASSWORD")) {
                RestAssured.keyStore(
                        System.getenv(Constant.RESOURCES_PATH).concat("/configs/")
                                .concat(SuiteConfig.getEnvs().get("SSL_KEYSTORE_NAME")),
                        SuiteConfig.getEnvs().get("SSL_KEYSTORE_PASSWORD"));
                RestAssured.trustStore(
                        System.getenv(Constant.RESOURCES_PATH).concat("/configs/")
                                .concat(SuiteConfig.getEnvs().get("SSL_TRUSTSTORE_NAME")),
                        SuiteConfig.getEnvs().get("SSL_TRUSTSTORE_PASSWORD"));
            }
            SuiteConfig.setRestAssuredConfig(restAssuredConfig);
            if (!Boolean.parseBoolean(SuiteConfig.getEnvs().get("SKIP_AUTH")))
                controller.setAuthenticationAttributes(SuiteConfig.getEnvs().get("AUTH_TYPE"));
            var testng = new TestNG();
            testng.setOutputDirectory(System.getenv(Constant.RESOURCES_PATH).concat("/test-outputs"));
            if (suites.get(0) instanceof String)
                testng.setTestSuites((List<String>) suites);
            if (suites.get(0) instanceof XmlSuite)
                testng.setXmlSuites((List<XmlSuite>) suites);
            testng.run();
            System.exit(testng.getStatus());
        }
    }

    private XmlSuite getSuite(String[] contractNamesList, String configName) {
        var suite = new XmlSuite();
        suite.setName(configName);
        suite.setVerbose(2);
        suite.setParallel(XmlSuite.ParallelMode.TESTS);
        suite.setThreadCount(3);
        Map<String, String> suiteParams = new HashMap<>();
        suiteParams.put("configName", configName);
        suite.setParameters(suiteParams);
        for (String contractName : contractNamesList) {
            addTestToSuite(suite, contractName);
        }
        return suite;
    }

    private void addTestToSuite(XmlSuite suite, String contractName) {
        var test = new XmlTest(suite);
        test.setName(contractName);
        Map<String, String> testngParams = new HashMap<>();
        testngParams.put("contractNameOrDirectory", contractName);
        test.setParameters(testngParams);
        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass(BasicContractTest.class));
        test.setXmlClasses(classes);
    }

    private Map<String, String> getEnvsMap(String configDirectory) {
        var dotenv = Dotenv.configure()
                .directory(configDirectory.concat("/.env"))
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        Map<String, String> envsMap = new HashMap<>();
        for (DotenvEntry e : dotenv.entries()) {
            envsMap.put(e.getKey(), e.getValue());
        }
        return envsMap;
    }
}
