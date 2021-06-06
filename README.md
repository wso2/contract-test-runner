### What are we going to do
This API testing library is based on the concept named consumer-based contract testing. With this test framework, we will invoke some rest api calls to some live API endpoints and get the response to compare the attributes inside that response with a predefined response (The response we define inside the contract).
1. We will create the contract file including a sample response from the API endpoint. Let’s name that response as R1
2. We will invoke a REST API call to a live endpoint and get the response. Let’s name that response as R2
3. Tests will be executed to compare and assert the attributes of R1 and R2

*Note: To make the readers' life easy, we will name the sample response we define in the contract mentioned above in the first step as R1 and the real response from the API endpoint mentioned above in the second step as R2 in the below sections in this document.*

### Steps to execute a test
There are two main steps to execute a test
1. Create the contract file
2. Create the testng file, config files, suite files and place them in a proper order
   Run the docker image

### 1. Create the contract file
Let’s have a look at a sample contract file first.
```
{
 "name": "POST_CreateApp_Expect_Success",
 "request": {
   "method": "POST",
   "url": "/orgs/${ORG_NAME}/apps",
   "body": {
     "name":"${APP_NAME}",
     "displayName": "${APP_NAME}"
   },
   "headers": {
     "Authorization": "Bearer ${BEARER_TOKEN}",
     "Content-Type": "application/json",
     "Cookie": "cwatf=${CWATF}"
   }
 },
 "response": {
   "status": 200,
   "body": {
     "id": "29095",
     "name": "${APP_NAME}",
     "organization": {
       "id": "115"
     },
     "org": "${ORG_NAME}",
     "displayName": "${APP_NAME}",
     "createdAt": "2021-05-04T04:02:03Z",
     "observability": {}
   },
   "headers": {
     "Content-type": "application/json"
   }
 },
 "assertions": {
   "statusCodeCheck": true,
   "headersPathCheck": [
     {
       "jsonPath": "Content-type",
       "type": "EXACT"
     }
   ],
   "bodyPathCheck": [
     {
       "jsonPath": "name",
       "type": "EXACT"
     },
     {
       "jsonPath": "displayName",
       "type": "EXACT"
     },
     {
       "jsonPath": "organization.id",
       "value": "^\\d{5}$",
       "type": "REGEX"
     }
   ]
 },
 "postConditions": {
   "setEnvs": [
     {
       "key": "APP_ID",
       "jsonPath": "id"
     }
   ]
 }
}
```

As we can see, there are 5 major parts in the contract json file as below
1. name
   This is just the testname which can be used to identify the test which is being running
2. request
   This section include all the information to execute a rest api call to the relevant API endpoint which is going to be tested
3. assertions
   This section includes all the tests which should be executed. In other words, all the assertions we should do with the real response we get from the endpoint. There are three types of assertions for now as below,
    * StatusCodeCheck - This is a boolean variable. If this attribute is not there in the contract, this will be set to true automatically. In other words, if this is null or true, the status code of the real response (R2) will be checked with the status code in the contract (in R1).
    * BodyPathCheck - This is a list of PathChecks which includes the assertions which should be done between the body of the actual response (R2) and the sample response in the contract (R1). This will be a json array with json objects of PathChecks as below.

            {
                   "jsonPath": "organization.id",
                   "type": "REGEX",
                   "typeValue": "^\\d{5}$"
             }

        *  JsonPath - This is the path to the relevant attribute inside the body of the response which is going to be tested or asserted. In the above example, there should be an json object called organization inside the response body and inside that organization json object there should be an attribute named id as below

                "body": {
                    "organization": {
                       "id": "115"
                     }
                 },

           type - There are 3 types of assertions types as below
            * EXACT - check whether the relevant attribute in sample response in the contract (R1) and the real response (R2) are exactly the same
            * REGEX - check whether the relevant attribute in the real response (R2) is matched with a regular expression
            * IS_EMPTY - check whether the relevant attribute in the real response (R2) is empty or not
            * IS_NOT_EMPTY - check whether the relevant attribute in the real response (R2) is empty or not
            * STRUCTURE - check whether the keys (only the keys, not the values) in the relevant json object in real response (R2) are equal to the keys of the relevant json object in the sample response (R1) in the contract. In other words, this compares the structure of two json objects.
            * IS_ARRAY - Check whether the given Json path contains a Json array. Test will fail if it is not a Json array
            * IS_OBJECT - Check whether the given Json path contains a Json object. Test will fail if it is not a Json object

           typeValue - This should contain the value for the type as below
            * EXACT - there is no need of a typeValue for this
            * REGEX - the regex pattern as a string
            * IS_EMPTY - there is no need of a typeValue for this
            * IS_NOT_EMPTY - there is no need of a typeValue for this
            * STRUCTURE - there is no need of a typeValue for this
            * IS_ARRAY - there is no need of a typeValue for this
            * IS_OBJECT - there is no need of a typeValue for this

    * HeaderPathCheck - This is a list of PathChecks which includes the assertions which should be done between the headers of the actual response (R2) and the sample response in the contract (R2). This is also a json array with PathCheck json objects as mentioned above. But here, the json path should include the path inside the header except the body

4. response
   This is the sample response (R1) we should get from the endpoint. We will not need to define everything in the response, but we should define everything we are going to test or compare. As an example, let’s consider the above example,
   Let’s have a look on the assertions section,

        "assertions": {
           "statusCodeCheck": true,
           "headersPathCheck": [
             {
               "jsonPath": "Content-type",
               "type": "EXACT"
             }
           ],
           "bodyPathCheck": [
             {
               "jsonPath": "name",
               "type": "EXACT"
             },
             {
               "jsonPath": "displayName",
               "type": "EXACT"
             },
             {
               "jsonPath": "organization.id",
               "value": "^\\d{5}$",
               "type": "REGEX"
             }
           ]
         },

   We can see that there are only four assertions. But if we take a look at the response part we can see there are more fields there. That is not necessary. So we can reduce the unwanted parts in the response and improve that as below.

        "response": {
           "status": 200,
           "body": {
             "id": "29095",
             "name": "${APP_NAME}",
             "organization": {
               "id": "115"
             },
             "org": "${ORG_NAME}",
             "displayName": "${APP_NAME}",
             "createdAt": "2021-05-04T04:02:03Z",
             "observability": {}
           },
           "headers": {
             "Content-type": "application/json"
           }
         },

5. postConditions
   As we know, there may be dependencies between some API endpoints. Some attribute from an API endpoint will be needed to execute another API call. Hence, those kinds of attributes can be saved as envs and can be injected to relevant contracts at the runtime. These conditions will only be checked within the before suite method.

   *Note: all the environmental variables mentioned in the contract can be injected through the config file. some attributes can be set as environment variables at the runtime during the before suite method.
   Create the testng file, config files, suite files and place them in a proper order*

### 2. Create the testng file, config files, suite files and place them in a proper order Run the docker image
This is how we should place the files inside the volume mount

    ├── configs
    │   ├── suite1config.json
    │   ├── suite1.xml
    │   ├── suite2config.json
    │   └── suite2.xml
    ├── contracts
    │   ├── DELETE_RemoveApp_Expect_Success.json
    │   ├── GET_AppStatus_Expect_CorrectData.json
    │   ├── GET_CheckBackEndHealth_Expect_CorrectData.json
    │   ├── GET_ConnectionConfig_Expect_CorrectData.json
    │   ├── GET_FetchApisOfOrgThroughProxy_Expect_CorrectData.json
    │   ├── GET_OrgStatus_Expect_CorrectData.json
    │   ├── POST_AppPing_Expect_200OK.json
    │   ├── POST_CreateApp_Expect_Success.json
    │   ├── POST_GetApisOfOrg_Expect_CorrectData.json
    │   └── PUT_AddApiToApp_Expect_CorrectData.json
    └── testng.xml

Here are some sample files

testng.xml

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd" >
    <suite name="allSuites" parallel="tests" thread-count="2" verbose="2">
       <suite-files>
           <suite-file path="configs/suite1.xml" />
           <suite-file path="configs/suite2.xml" />
       </suite-files>
    </suite>

There can be multiple test suites. We can pass a separate configuration file for each test suite.


suite1.xml

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd" >
    <suite name="suite1" parallel="tests" thread-count="2" verbose="2">
       <parameter name="configName" value="suite1config.json"/>
       <test name="GET_OrgStatus_Expect_CorrectData">
           <classes>
               <class name="com.wso2.choreo.integrationtests.BasicContractTest">
                   <parameter name="contractName" value="GET_OrgStatus_Expect_CorrectData.json"/>
               </class>
           </classes>
       </test>
       <test name="POST_AppPing_Expect_200OK">
           <classes>
               <class name="com.wso2.choreo.integrationtests.BasicContractTest">
                   <parameter name="contractName" value="POST_AppPing_Expect_200OK.json"/>
               </class>
           </classes>
       </test>
       <test name="GET_CheckBackEndHealth_Expect_CorrectData">
           <classes>
               <class name="com.wso2.choreo.integrationtests.BasicContractTest">
                   <parameter name="contractName" value="GET_CheckBackEndHealth_Expect_CorrectData.json"/>
               </class>
           </classes>
       </test>
    </suite>

We can pass the configuration file as a parameter for the suite. Inside the configuration file, we can define the contracts which should be executed within the before suite method and after suite method. All the tests defined here will be executed parallely


suite1config.json

    {
     "beforeSuiteConfigs": {
       "contracts": [
         "POST_CreateApp_Expect_Success.json"
       ]
     },
     "afterSuiteConfigs": {
       "contracts": [
         "DELETE_RemoveApp_Expect_Success.json"
       ]
     },
     "envs": {
       "BASE_URL": "https://app.dv.choreo.dev"
     }
    }

In the beforeSuiteConfigs, we can add the contracts that we want to execute before executing the tests. postConditions section in the contracts will be executed there. The envs we set in that postConditions section will be available for all the tests in the suite. As an example, if we need to test the following API endpoint,

	"url": "/app/${APP_ID}/documents/${DOCUMENT_ID}"

As we can see, there are two environmental variables in this url, APP_ID and DOCUMENT_ID. In order to test this endpoint we will have to create an app and a document inside that app. Hence, we will have to execute some API calls (execute contracts) to create the app and the document before running this test. Let’s consider that, we have two contracts called POST_CreateApp_Expect_Success.json and POST_CreateDocument_Expect_Success.json to do this task, we can include that within the beforeSuiteConfigs as below,

    "beforeSuiteConfigs": {
       "contracts": [
         "POST_CreateApp_Expect_Success.json",
         "POST_CreateDocument_Expect_Success.json"
       ]
     },

These contracts will be executed in the beforeSuite method in testng. In other words, These contracts will be executed synchronously one by one and execute the post condition section in each contract. Then all the tests in the relevant suite file will be executed parallely and they can use this APP_ID and DOCUMENT_ID set by those contracts in the beforeSuiteConfigs. Note that the assertions inside these contracts in the beforeSuiteMethod will also be executed. If we want not to execute those tests in the contracts, we can include them in the preContracts attribute. Imagine that, we are creating the app with a unique static name. Hence, if there is an app with the same name already, the POST_CreateApp_Expect_Success will be failed. Then the entire test suite will not be executed. Hence we can do a cleanup before the tests. We can execute the DELETE_RemoveApp_Expect_Success.json contract to delete the app with the same name if it exists. But, we can’t predict the response of that contract hence the response will depend on the existence of the particular app. Hence, we have to include that in the preContracts section as below,


    "beforeSuiteConfigs": {
       "preContracts": [
         "DELETE_RemoveApp_Expect_Success.json"
       ]
       "contracts": [
         "POST_CreateApp_Expect_Success.json",
         "POST_CreateDocument_Expect_Success.json"
       ]
     },

### 3. Run the docker image
Now we can execute the docker image using the following command

    docker run -e RESOURCES_PATH='{PATH}' -d -v {path_to_file_mount}:{Path} org/image

Execute the sample
Follow the below steps to execute the sample
* ```git clone https://github.com/HasithaAthukorala/contract-runner-sample.git```
* ```cd contract-runner```
* ```sh docker_run.sh```

###Todos
* Implement XML paths
* Add more assertion check types