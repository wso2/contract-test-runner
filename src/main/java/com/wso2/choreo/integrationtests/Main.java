package com.wso2.choreo.integrationtests;

import com.wso2.choreo.integrationtests.contractrunner.application.ContractRunner;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class Main {

    public static void main(String[] args) {
        prepareLoggers();
        (new ContractRunner()).initTestNG(System.getenv("RESOURCES_PATH"));
    }

    public static void prepareLoggers() {
        ConfigurationBuilder<BuiltConfiguration> builder
                = ConfigurationBuilderFactory.newConfigurationBuilder();
        AppenderComponentBuilder consoleAppender
                = builder.newAppender("stdout", "Console");
        consoleAppender.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.DENY).
                addAttribute("level", Level.INFO));
        consoleAppender.add(builder.newLayout("PatternLayout").
                addAttribute("pattern", "%-1level: %msg%n%throwable"));
        builder.add(consoleAppender);

        AppenderComponentBuilder fileDebugAppender
                = builder.newAppender("debug_log", "File");
        fileDebugAppender.addAttribute("fileName", System.getenv("RESOURCES_PATH")
                .concat("/test-outputs/logs.log"));
        fileDebugAppender.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.DENY).
                addAttribute("level", Level.DEBUG));
        fileDebugAppender.add(builder.newLayout("PatternLayout").
                addAttribute("pattern", "%-1level: %msg%n%throwable"));
        fileDebugAppender.addAttribute("append", false);
        builder.add(fileDebugAppender);

        RootLoggerComponentBuilder rootLogger
                = builder.newRootLogger(Level.ALL);
        rootLogger.add(builder.newAppenderRef("debug_log"));
        rootLogger.add(builder.newAppenderRef("stdout"));
        builder.add(rootLogger);
        Configurator.initialize(builder.build());
    }
}
