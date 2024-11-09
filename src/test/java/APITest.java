import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class APITest {
    private static final String BASE_URL = "https://reqres.in/api";
    private static ExtentReports extent;
    private static ExtentTest test;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter("extent-report.html");
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setReportName("API - Resultados");

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    @Test
    public void getAllUsers() {
        test = extent.createTest("Busca todos os usuários");
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)// Verifica se o status foi 200
                .body("page", equalTo(1)); // Verifica se está na primera página
    }
    @Test
    public void getUserById() {
        test = extent.createTest("Busca usuário por ID");
        given()
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)// Verifica se o status foi 200
                .body("data.id", equalTo(2)); // Verifica se o id do usuário retornado é 2
    }
    @Test
    public void getUserFail() {
        test = extent.createTest("Busca usuário inexistente");
        given()
                .when()
                .get("/unknown/23")
                .then()
                .statusCode(404)//Verifica se o status foi 404
                .body(equalTo("{}")); // Verifica se o retorno foi vazio
    }
    @Test
    public void createUser() {
        test = extent.createTest("Cria novo usuário");
        String requestBody = "{\n" +
                "    \"name\": \"João do Teste\",\n" +
                "    \"job\": \"QA\"\n" +
                "}";

        String userId = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)// Verifica se o status foi 201
                .body("name", equalTo("João do Teste"))//Verifica se o nome do usuário foi cadastrado
                .body("job", equalTo("QA"))//Verifica se o cargo do usuário foi cadastrado
                .extract()
                .path("id");
        System.out.println("Created User ID: " + userId);
        Config.setUserId(userId);
    }
    @Test
    public void createUserFail() {
        test = extent.createTest("Cria novo usuário com dados incorretos");
        String requestBody = "{\n" +
                "    \"email\": \"sydney@fife\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/register")
                .then()
                .statusCode(400)//Verifica se o status foi 400
                .body("error", equalTo("Missing password"));//Verifica se a mensagem de erro está correta
    }
    @Test
    public void updateUser() {
        test = extent.createTest("Altera nome e cargo do usuário");
        String requestBody = "{\n" +
                "    \"name\": \"Maria do Teste\",\n" +
                "    \"job\": \"Developer\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/users/2")
                .then()
                .statusCode(200)// Verifica se o status foi 200
                .body("name", equalTo("Maria do Teste"))//Verifica se o nome do usuário foi atualizado
                .body("job", equalTo("Developer"));//Verifica se o cargo do usuário foi atualizado
    }
    @Test
    public void patchUser() {
        test = extent.createTest("Altera nome do usuário");
        String requestBody =  "{\n" +
                "    \"name\": \"Maria do Dev\",\n" +
                "    \"job\": \"Developer\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/users/2")
                .then()
                .statusCode(200)// Verifica se o status foi 200
                .body("name", equalTo("Maria do Dev"))//Verifica se o nome do usuário foi atualizado
                .body("job", equalTo("Developer"));//Verifica se o cargo do usuário se manteve
    }
    @Test
    public void deleteAllUsers() {
        test = extent.createTest("Deleta todos os usuários");
        given()
                .when()
                .delete("/users/2")
                .then()
                .statusCode(204);// Verifica se o status foi 204
    }

    @AfterClass
    public static void tearDown() {
        extent.flush();  // Gera o relatório
    }
}
