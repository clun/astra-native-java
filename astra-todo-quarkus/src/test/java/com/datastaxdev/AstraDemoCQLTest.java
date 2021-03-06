package com.datastaxdev;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AstraDemoCQLTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/v1/john/todos/")
          .then().statusCode(200);
    }

}
