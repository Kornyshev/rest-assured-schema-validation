### Способы валидации JSON-схем и сравнения JSON-объектов в автоматизации тестирования

Валидация JSON-схем является неотъемлемой частью автоматизации API-тестирования, особенно когда важно проверить, соответствует ли структура данных, возвращаемая сервисом, заранее определённой схеме. Это помогает убедиться, что сервис возвращает корректные данные в правильном формате, а тестировщики могут быстро выявить проблемы, связанные с изменениями в API. В этой статье мы рассмотрим несколько популярных способов валидации JSON-схем и сравнения JSON-объектов, используемых в API тестировании с помощью библиотек на Java.

### Зачем нужна валидация JSON-схем?

При взаимодействии с API важно проверять не только сами данные, но и их структуру. Это особенно актуально при разработке и тестировании API, так как некорректные или изменённые структуры ответов могут привести к поломкам на стороне потребителей данных (клиентов).

Валидация JSON-схем помогает:
- Проверить соответствие ответа API требованиям и ожидаемой структуре.
- Обеспечить обратную совместимость API (при добавлении новых полей старые ответы остаются корректными).
- Автоматизировать тесты для сложных структур данных, проверяя не только значения полей, но и их типы и обязательность.

### Подходы к валидации JSON-схем и сравнения JSON-объектов

Существует несколько подходов и библиотек для проверки структуры и значений JSON-ответов в Java. В этой статье мы рассмотрим два основных аспекта: валидацию JSON-схем и сравнение JSON-объектов.

#### 1. Валидация JSON-схем с RestAssured

RestAssured — это одна из самых популярных библиотек для тестирования REST API на Java. Она предоставляет удобные механизмы для валидации структуры и содержимого ответов.

Пример запроса с использованием RestAssured:
```java
given()
    .when()
    .get("/films/1/")
    .then()
    .statusCode(200)
    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/film-schema.json"));
```

В этом примере используется JSON-схема, которая хранится в файле `film-schema.json`. Этот файл содержит описание структуры ответа, и RestAssured проверяет, соответствует ли фактический ответ этой схеме.

### Как работает JSON Schema Validator в RestAssured?

RestAssured использует библиотеку `json-schema-validator`, которая позволяет загружать JSON-схемы из файлов или строк и проверять, соответствует ли им JSON-ответ от API. В данном примере схема может быть следующей:

```json
{
  "type": "object",
  "properties": {
    "title": {"type": "string"},
    "episode_id": {"type": "integer"},
    "director": {"type": "string"}
  },
  "required": ["title", "episode_id", "director"]
}
```

Это простая схема, описывающая, что объект должен содержать строковые поля `title` и `director`, а также числовое поле `episode_id`. Если ответ API не соответствует этой схеме (например, одно из полей отсутствует или имеет неверный тип данных), тест провалится.

### 2. Валидация JSON-схем с использованием библиотеки Everit

Ещё один способ валидации JSON-схем — использование библиотеки **Everit JSON Schema**. Эта библиотека позволяет вручную загружать и проверять схемы, предоставляя более гибкий подход к валидации.

Пример использования библиотеки Everit:

1. Добавляем зависимость в `pom.xml`:
   ```xml
    <dependency>
            <groupId>com.github.erosb</groupId>
            <artifactId>everit-json-schema</artifactId>
            <version>1.14.4</version>
    </dependency>
   ```

2. Код для валидации JSON-схемы:
   ```java
   import org.everit.json.schema.Schema;
   import org.everit.json.schema.loader.SchemaLoader;
   import org.json.JSONObject;

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

       // Проверяем корректный JSON
       schema.validate(json);
   }
   ```

Библиотека **Everit** позволяет программно загружать JSON-схемы и проверять соответствие данных этим схемам. Это даёт более гибкие возможности для использования схем, чем в RestAssured, особенно когда нужно валидировать JSON-ответы динамически.

### 3. Сравнение двух JSON-объектов

Помимо проверки схемы, часто возникает необходимость сравнить два JSON-объекта: фактический ответ от API и ожидаемый результат. Для этого можно использовать библиотеку **JSONAssert**.

1. Добавляем зависимость JSONAssert в `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.skyscreamer</groupId>
       <artifactId>jsonassert</artifactId>
       <version>1.5.1</version>
       <scope>test</scope>
   </dependency>
   ```

2. Пример кода для сравнения двух JSON-объектов:
   ```java
   import org.json.JSONObject;
   import org.junit.jupiter.api.Test;
   import org.skyscreamer.jsonassert.JSONAssert;

   @Test
   public void compareTwoJsonObjects() throws Exception {
       String json1 = "{ \"title\": \"A New Hope\", \"episode_id\": 4, \"director\": \"George Lucas\" }";
       String json2 = "{ \"title\": \"A New Hope\", \"episode_id\": 4, \"director\": \"George Lucas\" }";

       JSONObject jsonObject1 = new JSONObject(json1);
       JSONObject jsonObject2 = new JSONObject(json2);

       // Сравнение двух идентичных объектов JSON
       JSONAssert.assertEquals(jsonObject1, jsonObject2, true);
   }
   ```

**JSONAssert** позволяет удобно сравнивать JSON-объекты, игнорируя порядок ключей, что особенно полезно при работе с API, где порядок элементов может быть разным. При необходимости можно указать, что порядок имеет значение, передав соответствующий параметр.

### 4. Использование AssertJ для проверки полей объекта

Для проверки значений внутри объекта модели, полученного из JSON-ответа, можно использовать библиотеку **AssertJ**. Это позволяет писать более выразительные и читабельные утверждения.

Пример использования **AssertJ** для проверки значений:

```java
import static org.assertj.core.api.Assertions.assertThat;

@Test
public void getFilmAsModelTest() {
    Film film = given()
            .when()
            .get("/films/1/")
            .then()
            .statusCode(200)
            .extract()
            .as(Film.class);

    // Проверки с использованием AssertJ
    assertThat(film.getTitle()).isEqualTo("A New Hope");
    assertThat(film.getDirector()).isEqualTo("George Lucas");
}
```

**AssertJ** предоставляет множество методов для проверки значений, которые помогают писать выразительные и понятные тесты. В случае ошибки выводится более детализированное сообщение, что упрощает отладку тестов.

### Заключение

Валидация JSON-схем и сравнение JSON-объектов — важные инструменты в арсенале тестировщика API. Существует несколько подходов для этих задач, включая библиотеки **RestAssured**, **Everit JSON Schema** и **JSONAssert**.

- **RestAssured** и **Everit** позволяют удобно валидировать структуры JSON, проверяя соответствие схемам.
- **JSONAssert** — мощный инструмент для сравнения JSON-объектов с гибкостью игнорирования порядка ключей.
- **AssertJ** — для проверки конкретных значений внутри моделей объектов, что делает тесты выразительными и лёгкими для понимания.

Каждая из этих библиотек имеет свои сильные стороны и может быть использована в зависимости от конкретных требований к проекту и тестированию.