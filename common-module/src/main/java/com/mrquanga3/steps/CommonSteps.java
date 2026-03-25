package com.mrquanga3.steps;

import com.mrquanga3.keywords.WebKeywords;
import com.mrquanga3.utils.ActorManager;
import com.mrquanga3.utils.PropertiesLoader;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;

/**
 * Generic Cucumber step definitions reusable across all feature modules.
 * Locator keys are resolved at runtime by {@link PropertiesLoader}.
 *
 * <p>Multi-actor support: use the "opens a browser" and "switching to"
 * steps to run multiple browser sessions within one scenario.
 */
public class CommonSteps {

  @Steps
  WebKeywords keywords;

  // ── Actor management ──────────────────────────────────────────────

  /** Opens a new browser session for the named actor. */
  @Given("{string} opens a {string} browser")
  public void actorOpensBrowser(String actorName, String browserType) {
    ActorManager.openBrowser(actorName, browserType);
  }

  /** Switches all subsequent steps to run in the named actor's browser. */
  @When("switching to {string}")
  public void switchingToActor(String actorName) {
    ActorManager.switchTo(actorName);
  }

  /** Closes all actor browsers after each scenario. */
  @After
  public void closeActorBrowsers() {
    ActorManager.closeAll();
  }

  // ── Navigation ────────────────────────────────────────────────────

  /** Opens the given URL in the browser. */
  @Given("I navigate to {string}")
  public void navigateToUrl(String url) {
    keywords.navigateTo(url);
  }

  /** Resolves a URL key from properties and navigates to that page. */
  @Given("I navigate to the {string} page")
  public void navigateToPage(String urlKey) {
    keywords.navigateTo(PropertiesLoader.get(urlKey));
  }

  // ── Interaction ───────────────────────────────────────────────────

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

  // ── Verification ──────────────────────────────────────────────────

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
