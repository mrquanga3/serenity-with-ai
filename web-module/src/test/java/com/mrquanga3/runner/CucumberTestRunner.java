package com.mrquanga3.runner;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * JUnit 4 runner that executes all Cucumber feature files with Serenity BDD.
 * Run via: {@code mvn test}
 * Reports generated via: {@code mvn serenity:aggregate}
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.mrquanga3.steps",
    plugin = {"pretty", "json:target/cucumber-reports/cucumber.json"}
)
public class CucumberTestRunner {
}
