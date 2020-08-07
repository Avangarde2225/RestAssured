package review;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class intro {

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
}
