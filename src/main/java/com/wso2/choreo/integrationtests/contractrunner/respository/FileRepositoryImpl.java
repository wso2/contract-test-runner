package com.wso2.choreo.integrationtests.contractrunner.respository;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileRepositoryImpl implements FileRepository {
    private static final Logger logger = LogManager.getLogger(FileRepositoryImpl.class);

    @Override
    public String getFileContent(String filePath) {
        var mainFile = new File(filePath);
        try (InputStream inputStream = new FileInputStream(mainFile)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            logger.error("File not found: ".concat(filePath), e);
            return null;
        }
    }
}
