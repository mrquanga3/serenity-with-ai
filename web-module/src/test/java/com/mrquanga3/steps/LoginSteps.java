package com.mrquanga3.steps;

import com.mrquanga3.keywords.WebKeywords;
import com.mrquanga3.utils.PropertiesLoader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;

/**
 * Cucumber step definitions for the OpenCart admin login feature.
 * Locator keys come from the feature file and are resolved at runtime
 * by {@link PropertiesLoader} from all files under {@code properties/}.
 */
public class LoginSteps {

  private static final String LOGIN_URL =
      "http://103.245.237.118:8081/opencart/administrator/";

  @Steps
  WebKeywords keywords;

  /** Opens the OpenCart admin login page. */
  @Given("I navigate to the login page")
  public void navigateToLoginPage() {
    keywords.navigateTo(LOGIN_URL);
  }

  /** Types {@code text} into the field identified by {@code locatorKey}. */
  @When("I enter {string} to {string} field")
  public void enterTextToField(String text, String locatorKey) {
    keywords.inputText(PropertiesLoader.get(locatorKey), text);
  }

  /** Clicks the element identified by {@code locatorKey}. */
  @And("I click {string}")
  public void clickElement(String locatorKey) {
    keywords.clickElement(PropertiesLoader.get(locatorKey));
  }

  /** Asserts the element identified by {@code locatorKey} is visible. */
  @Then("I should see {string}")
  public void verifyElementVisible(String locatorKey) {
    keywords.verifyElementVisible(PropertiesLoader.get(locatorKey));
  }

  /** Asserts the current URL contains {@code fragment}. */
  @Then("the URL should contain {string}")
  public void verifyUrlContains(String fragment) {
    keywords.verifyUrlContains(fragment);
  }
}
