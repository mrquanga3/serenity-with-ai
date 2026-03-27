package com.mrquanga3.runner;

import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 Platform Suite that discovers and executes all Cucumber
 * feature files with Serenity BDD reporting.
 *
 * <p>Run via: {@code mvn test}
 *
 * <p>Reports generated via: {@code mvn serenity:aggregate}
 */
@Suite
@IncludeEngines("cucumber")
@ConfigurationParameter(
    key = FEATURES_PROPERTY_NAME,
    value = "src/test/resources/features")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "com.mrquanga3.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, json:target/cucumber-reports/cucumber.json")
public class CucumberTestRunner {
}
