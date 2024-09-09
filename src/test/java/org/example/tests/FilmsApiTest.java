package org.example.tests;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.example.models.Film;
import org.example.specifications.ApiSpecifications;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmsApiTest {

    @BeforeAll
    public static void setup() {
        RestAssured.requestSpecification = ApiSpecifications.defaultRequestSpec();
    }

    @Test
    public void getFilmByIdTest() {
        given()
                .when()
                .get("/films/1/")
                .then()
                .statusCode(200)
                .body("title", equalTo("A New Hope"))
                .body("director", equalTo("George Lucas"));
    }

    @Test
    public void validateFilmJsonSchema() {
        given()
                .when()
                .get("/films/1/")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/film-schema.json"));
    }

    @Test
    public void getFilmAsModelTest() {
        Film film = given()
                .when()
                .get("/films/1/")
                .then()
                .statusCode(200)
                .extract()
                .as(Film.class);
        assertThat(film.getTitle()).isEqualTo("A New Hope");
        assertThat(film.getDirector()).isEqualTo("George Lucas");
    }

    @Test
    public void validateJsonSchema() {
        String jsonString = "{ \"title\": \"A New Hope\", \"episode_id\": 4, \"director\": \"George Lucas\" }";
        String schemaString = "{\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"title\": {\"type\": \"string\"},\n" +
                "    \"episode_id\": {\"type\": \"integer\"},\n" +
                "    \"director\": {\"type\": \"string\"}\n" +
                "  },\n" +
                "  \"required\": [\"title\", \"episode_id\", \"director\"]\n" +
                "}";
        JSONObject json = new JSONObject(jsonString);
        JSONObject schemaJson = new JSONObject(schemaString);
        Schema schema = SchemaLoader.load(schemaJson);
        assertDoesNotThrow(() -> schema.validate(json));
        JSONObject invalidJson = new JSONObject("{ \"title\": \"A New Hope\", \"episode_id\": \"four\" }");
        assertThrows(Exception.class, () -> schema.validate(invalidJson));
    }
}
