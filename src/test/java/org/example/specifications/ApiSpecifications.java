package org.example.specifications;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class ApiSpecifications {
    public static RequestSpecification defaultRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://swapi.dev/api/")
                .setContentType("application/json")
                .build();
    }
}

