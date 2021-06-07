package com.wso2.choreo.integrationtests.contractrunner.controller;

import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.wso2.choreo.integrationtests.contractrunner.configuration.EnvLevel;
import com.wso2.choreo.integrationtests.contractrunner.configuration.SuiteConfig;
import com.wso2.choreo.integrationtests.contractrunner.domain.entity.CustomEnv;
import com.wso2.choreo.integrationtests.contractrunner.respository.FileRepository;
import com.wso2.choreo.integrationtests.contractrunner.respository.FileRepositoryImpl;
import com.wso2.choreo.integrationtests.contractrunner.respository.NetworkRepository;
import com.wso2.choreo.integrationtests.contractrunner.respository.NetworkRepositoryImpl;
import com.wso2.choreo.integrationtests.contractrunner.usecase.*;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ContractController {
    private static final Logger logger = LogManager.getLogger(ContractController.class);
    private final NetworkRepository networkRepository = new NetworkRepositoryImpl();
    private final FileRepository fileRepository = new FileRepositoryImpl();

    public String[] getJsonFilesInDirectory(String directory) {
        File configDirectory = new File(directory);
        String[] fileNames = configDirectory.list((f, name) -> name.endsWith(".json"));
        assert fileNames != null;
        Arrays.sort(fileNames, String::compareTo);
        return fileNames;
    }

    public JsonPath getJsonPathFromFile(String fileName) {
        return JsonPath.from(fileRepository.getFileContent(fileName));
    }

    public void runContract(String contractName, boolean skipTests, boolean skipPostConditions) {
        String baseUrl = SuiteConfig.getEnvs().get("BASE_URL");
        StrSubstitutor sub = new StrSubstitutor(SuiteConfig.getEnvs());
        String jsonStringWithEnvs = sub.replace(
                fileRepository.getFileContent(System.getenv("RESOURCES_PATH")
                        .concat("/contracts/".concat(contractName))));
        JsonPath contractJsonPath = JsonPath.from(jsonStringWithEnvs);

        Response response = networkRepository.getResponse(contractJsonPath,
                baseUrl.concat(contractJsonPath.getString("request.url")),
                SuiteConfig.getRestAssuredConfig());

        if (!skipTests)
            runAllChecks(contractName, response, contractJsonPath);
        if (!skipPostConditions)
            runPostContractConditions(response, contractJsonPath);
    }

    public void setSuiteConfigs(String configName) {
        SuiteConfig.initializeSuiteEnvs();
        if (configName != null) {
            JsonPath configJsonPath = getJsonPathFromFile(System
                    .getenv("RESOURCES_PATH").concat("/configs/".concat(configName)));
            if (configJsonPath.getMap("envs") != null)
                SuiteConfig.putEnvs(configJsonPath.getMap("envs"), EnvLevel.SUITE);
            SuiteConfig.setBeforeSuitePreContracts(configJsonPath
                    .getList("beforeSuiteConfigs.preContracts").toArray(new String[0]));
            SuiteConfig.setBeforeSuiteContracts(configJsonPath
                    .getList("beforeSuiteConfigs.contracts").toArray(new String[0]));
            SuiteConfig.setAfterSuiteContracts(configJsonPath
                    .getList("afterSuiteConfigs.contracts").toArray(new String[0]));
        }
    }

    public void setAuthenticationAttributes(String authType){
        networkRepository.setAuthenticationAttributes(authType);
    }

    private void runAllChecks(String contractName, Response response, JsonPath contractJsonPath) {
        UseCase assertBasicsUseCase =
                new AssertMetaDataUseCase(contractName, contractJsonPath);
        assertBasicsUseCase.execute();

        UseCase assertStatusCodeUseCase =
                new AssertStatusCodeUseCase(response, contractJsonPath);
        assertStatusCodeUseCase.execute();

        if (contractJsonPath.get("assertions") == null) {
            logger.debug("No assertions in this contract: ".concat(contractJsonPath.getString("name")));
            return;
        }

        UseCase assertHeadersUseCase =
                new AssertHeadersUseCase(response, contractJsonPath);
        UseCase assertBodyUseCase =
                new AssertBodyUseCase(response, contractJsonPath);
        assertHeadersUseCase.execute();
        assertBodyUseCase.execute();
    }

    private void runPostContractConditions(Response response, JsonPath contractJsonPath) {
        if (contractJsonPath.get("postConditions") != null) {
            setPostContractEnvs(response, contractJsonPath, "postConditions.setEnvs", EnvLevel.SUITE);
            setPostContractEnvs(response, contractJsonPath, "postConditions.setGlobalEnvs", EnvLevel.GLOBAL);
        }
    }

    private void setPostContractEnvs(Response response, JsonPath contractJsonPath, String jsonPathStr, EnvLevel level) {
        if (contractJsonPath.get(jsonPathStr) != null) {
            List<CustomEnv> customEnvs = contractJsonPath
                    .getList(jsonPathStr, CustomEnv.class);
            for (CustomEnv env : customEnvs) {
                JsonPath responsePath = JsonPath.from(response.asInputStream());

                Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider()).build();
                Object objectEnv = com.jayway.jsonpath.JsonPath.using(conf).parse(responsePath.prettify())
                        .read((env.getPath().equals("")) ? "$" : "$.".concat(env.getPath()));

                SuiteConfig.putEnv(env.getKey(), objectEnv.toString(), level);
                logger.debug("SETTING ".concat(level.toString()).concat(" ENV: ").concat(env.getKey()).concat(": ")
                        .concat(objectEnv.toString()));
            }
        }
    }
}
