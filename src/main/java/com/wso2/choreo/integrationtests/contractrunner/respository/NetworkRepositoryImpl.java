package com.wso2.choreo.integrationtests.contractrunner.respository;

import com.google.gson.JsonObject;
import com.wso2.choreo.integrationtests.contractrunner.configuration.Constant;
import com.wso2.choreo.integrationtests.contractrunner.configuration.EnvLevel;
import com.wso2.choreo.integrationtests.contractrunner.configuration.SuiteConfig;
import com.wso2.choreo.integrationtests.contractrunner.domain.entity.AuthType;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class NetworkRepositoryImpl implements NetworkRepository {
    private static final Logger logger = LogManager.getLogger(NetworkRepositoryImpl.class);

    private RequestSpecification getRequestSpec(JsonPath contractJsonPath, RestAssuredConfig config) {
        RequestSpecification specs = given();
        if (contractJsonPath.get("request.headers") != null)
            specs = specs
                    .headers(contractJsonPath.<Map<String, String>>getJsonObject("request.headers"));
        if (contractJsonPath.get("request.body") != null)
            specs = specs.body(contractJsonPath.<Map<String, String>>getJsonObject("request.body"));
        specs.config(config).log().all();
        return specs;
    }

    @Override
    public Response getResponse(JsonPath contractJsonPath, String url, RestAssuredConfig config) {
        var method = contractJsonPath
                .getString("request.method");
        RequestSpecification specs = getRequestSpec(contractJsonPath, config);
        Response response;
        switch (method) {
            case "POST":
                response = given().spec(specs).post(url);
                break;
            case "PUT":
                response = given().spec(specs).put(url);
                break;
            case "PATCH":
                response = given().spec(specs).patch(url);
                break;
            case "DELETE":
                response = given().spec(specs).delete(url);
                break;
            default:
                response = given().spec(specs).get(url);
        }
        logger.debug(Constant.RESPONSE_LOG, method, url,
                ((!response.body().asString().equals("")) ? response.asString() : ""));
        return response;
    }

    @Override
    public void setAuthenticationAttributes(String authType) {
        switch (AuthType.valueOf(authType)) {
            case IDP:
                setIDPAuthenticationAttributes();
                break;
            case APIM:
                setAPIMAuthenticationAttributes();
                break;
        }
    }

    private void setIDPAuthenticationAttributes() {
        RequestSpecification specs = given()
                .header(Constant.AUTHORIZATION_HEADER, SuiteConfig.getEnvs().get("IDP_AUTH_HEADER"))
                .header(Constant.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("scope", "openid")
                .formParam("username", SuiteConfig.getEnvs().get("IDP_USERNAME"))
                .formParam("password", SuiteConfig.getEnvs().get("IDP_PASSWORD"));

        specs.config(SuiteConfig.getRestAssuredConfig()).log().all();

        var response = given().spec(specs).post(SuiteConfig.getEnvs().get("IDP_URL"));

        String[] fragments = response.body().jsonPath().getString("id_token").split("\\.");
        String token = fragments[0].concat(".").concat(fragments[1]);
        String cwatf = fragments[2];

        SuiteConfig.putEnv("BEARER_TOKEN", token, EnvLevel.GLOBAL);
        SuiteConfig.putEnv("CWATF", cwatf, EnvLevel.GLOBAL);

        logger.debug("BEARER_TOKEN: {}", token);
        logger.debug("CWATF: {}", cwatf);
    }

    private void setAPIMAuthenticationAttributes() {
        var clientRegistrationParams = new JsonObject();
        clientRegistrationParams.addProperty("clientName", "contract_test_client");
        clientRegistrationParams.addProperty("grantType", "client_credentials password");
        clientRegistrationParams.addProperty("owner", SuiteConfig.getEnvs().get("APIM_CLIENT_REG_OWNER"));
        clientRegistrationParams.addProperty("saasApp", true);

        RequestSpecification clientRegistrationSpecs = given()
                .header(Constant.AUTHORIZATION_HEADER, SuiteConfig.getEnvs().get("APIM_CLIENT_REG_AUTH_HEADER"))
                .header(Constant.CONTENT_TYPE, "application/json")
                .body(clientRegistrationParams);
        clientRegistrationSpecs.config(SuiteConfig.getRestAssuredConfig()).log().all();
        var clientRegistrationResponse = given().spec(clientRegistrationSpecs)
                .post(SuiteConfig.getEnvs().get("APIM_CLIENT_REG_URL"));

        logger.debug(Constant.RESPONSE_LOG, "POST", SuiteConfig.getEnvs().get("APIM_CLIENT_REG_URL"),
                ((!clientRegistrationResponse.body().asString().equals(""))
                        ? clientRegistrationResponse.asString() : ""));

        var clientDetailsJsonPath = clientRegistrationResponse.jsonPath();
        var base64Token = Base64.getEncoder().encodeToString((clientDetailsJsonPath.getString("clientId")
                .concat(":").concat(clientDetailsJsonPath.getString("clientSecret"))).getBytes());

        RequestSpecification specs = given()
                .header(Constant.AUTHORIZATION_HEADER, "Basic ".concat(base64Token))
                .header(Constant.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("scope", SuiteConfig.getEnvs().get("APIM_USER_SCOPES"))
                .formParam("username", SuiteConfig.getEnvs().get("APIM_USERNAME"))
                .formParam("password", SuiteConfig.getEnvs().get("APIM_PASSWORD"));
        logger.debug("base64 token: {}", base64Token);
        specs.config(SuiteConfig.getRestAssuredConfig()).log().all();
        var tokensResponse = given().spec(specs).post(SuiteConfig.getEnvs().get("APIM_TOKEN_ENDPOINT"));

        logger.debug(Constant.RESPONSE_LOG, "POST", SuiteConfig.getEnvs().get("APIM_TOKEN_ENDPOINT"),
                ((!tokensResponse.body().asString().equals("")) ? tokensResponse.asString() : ""));

        var tokensJsonPath = tokensResponse.jsonPath();
        SuiteConfig.putEnv("BEARER_TOKEN", tokensJsonPath.getString("access_token"), EnvLevel.GLOBAL);
        logger.debug("BEARER_TOKEN: {}", tokensJsonPath.getString("access_token"));
    }
}
