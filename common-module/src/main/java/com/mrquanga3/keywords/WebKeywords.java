package com.mrquanga3.keywords;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.Serenity;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Keyword-driven library of reusable browser actions.
 * Locator strings follow the format {@code type:value},
 * e.g. {@code id:input-username} or {@code css:button[type='submit']}.
 */
public class WebKeywords {

  private static final int DEFAULT_TIMEOUT_SECONDS = 10;

  private WebDriver driver() {
    return Serenity.getWebdriverManager().getWebdriver();
  }

  private WebDriverWait createWait() {
    return new WebDriverWait(driver(), Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
  }

  /** Opens the given URL in the current browser session. */
  @Step("Navigate to '{0}'")
  public void navigateTo(String url) {
    driver().get(url);
  }

  /** Clears the element matched by {@code locator} and types {@code text}. */
  @Step("Input '{1}' into element '{0}'")
  public void inputText(String locator, String text) {
    WebElement element = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(parseLocator(locator)));
    element.clear();
    element.sendKeys(text);
  }

  /** Clicks the element matched by {@code locator}. */
  @Step("Click element '{0}'")
  public void clickElement(String locator) {
    WebElement element = createWait().until(
        ExpectedConditions.elementToBeClickable(parseLocator(locator)));
    element.click();
  }

  /** Asserts the element matched by {@code locator} is displayed on the page. */
  @Step("Verify element '{0}' is visible")
  public void verifyElementVisible(String locator) {
    WebElement element = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(parseLocator(locator)));
    assertThat(element.isDisplayed())
        .as("Element '%s' should be visible", locator)
        .isTrue();
  }

  /** Asserts the current URL contains the given fragment. */
  @Step("Verify URL contains '{0}'")
  public void verifyUrlContains(String fragment) {
    createWait().until(ExpectedConditions.urlContains(fragment));
    assertThat(driver().getCurrentUrl())
        .as("Current URL should contain '%s'", fragment)
        .contains(fragment);
  }

  /**
   * Parses a locator string of the form {@code type:value} into a Selenium {@link By}.
   * Supported types: {@code id}, {@code css}, {@code xpath}, {@code name}.
   */
  private By parseLocator(String locator) {
    if (locator.startsWith("id:")) {
      return By.id(locator.substring(3));
    }
    if (locator.startsWith("css:")) {
      return By.cssSelector(locator.substring(4));
    }
    if (locator.startsWith("xpath:")) {
      return By.xpath(locator.substring(6));
    }
    if (locator.startsWith("name:")) {
      return By.name(locator.substring(5));
    }
    throw new IllegalArgumentException("Unknown locator format: " + locator);
  }
}
