package review;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class intro {
    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;

    @BeforeClass
    public  void createResponseSpecification() {
        responseSpec = new ResponseSpecBuilder().
                expectStatusCode(200).
                expectContentType(ContentType.JSON).
                build();
    }

    @Test
    public void checkPlaceNameInResponseBodyWithResponseSpec() {
        given().
                spec(requestSpec).
                when().
                get("us/90210").
                then().
                spec(responseSpec).
                body("places[0].'place name'", equalTo("Beverly Hills"));
    }

    @Test
    public void extractPlaceNameFromResponseBody() {
        String placeName =
                given().
                        spec(requestSpec).
                        when().
                        get("us/90210").
                        then().
                        log().body().
                        extract().
                        path("places[0].'place name'");

        Assert.assertEquals(placeName, "Beverly Hills");
    }



    @BeforeClass
    public void createRequestSpecification(){
        requestSpec = new RequestSpecBuilder().
                setBaseUri("http://zippopotam.us").
                setAccept(ContentType.JSON).
                build();
    }

    @Test
    public void statusCodeTestWithRequestSpec() {

        given().
                spec(requestSpec).
                when().
                get("us/90210").
                then().
                statusCode(200);
    }

    @BeforeClass
    public void init(){
        RestAssured.baseURI = "http://api.zippopotam.us";
    }

    @Test
    public void statusCodeTest() {
        given().
                when().
                get("/us/90210").
                then().
                statusCode(200);
    }

    @Test
    public void contentTypeTest() {
        given().
                when().
                get("/us/90210").
                then().
                assertThat().
                contentType(ContentType.JSON);
    }
    @Test
    public void logRequestAndResponseDetails() {
        given().
                log().all().
                when().
                get("/us/90210").
                then().
                log().body();
    }

    @Test
    public void checkStateInResponseBody() {
        given().
                when().
                get("/us/90210").
                then().
                assertThat().
                body("places[0].state",equalTo("California"));

    }

    @Test
    public void checkListHasItem(){
        given().
                when().
                get("/tr/34295").
                then().
                body("places.'place name'",hasItem("Kartaltepe Mah."));
    }
    @Test
    public void checkNumberOfPlaceNamesInResponseBody() {
        given().
                when().
                get("/tr/34840").
                then().
                assertThat().
                body("places.'place name'", hasSize(2));
    }
    @Test
    public void combiningTest() {
        given().
                when().
                get("/us/90210").
                then().
                statusCode(200)
                .contentType(ContentType.JSON)
                .body("places[0].state", equalTo("California"))
                .body("places.'place name'", hasItem("Beverly Hills"))
                .body("places.'place name'", hasSize(1));
    }
    @Test
    public void pathParametersTest() {
        given().
                log().uri().
                pathParam("countryCode", "us").pathParam("zipCode", "90210").
                when().
                get("/{countryCode}/{zipCode}").
                then().
                log().all().
                assertThat().
                body("places[0].'place name'", equalTo("Beverly Hills"));
    }
    //https://gorest.co.in/public-api/users?_format=json&access-token=j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap
    @Test
    public void queryParametersTest() {
        given().
                log().uri().
                param("_format", "json").param("access-token", "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap").
                when().
                get("https://gorest.co.in/public-api/users").
                then().
                assertThat().
                log().body().
                log().status().
                body("result", not(empty()));
    }
}
