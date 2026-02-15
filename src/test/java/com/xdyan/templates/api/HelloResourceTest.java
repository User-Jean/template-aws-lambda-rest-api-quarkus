package com.xdyan.templates.api;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestHTTPEndpoint(HelloResource.class)
class HelloResourceTest {

  @Test
  @DisplayName("return success when get resource hello")
  public void returnSuccessWhenGetResourceHello() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get()
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body("message", equalTo("Hello World"));
  }
}