package review;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class intro {
    @Test
    public void statusCodeTest() {
        given().
                when().
                get("http://zippopotam.us/us/90210").
                then().
                statusCode(200);
    }

    @Test
    public void contentTypeTest() {
        given().
                when().
                get("http://zippopotam.us/us/90210").
                then().
                assertThat().
                contentType(ContentType.JSON);
    }
    @Test
    public void logRequestAndResponseDetails() {
        given().
                log().all().
                when().
                get("http://zippopotam.us/us/90210").
                then().
                log().body();
    }

    @Test
    public void checkStateInResponseBody() {
        given().
                when().
                get("http://zippopotam.us/us/90210").
                then().
                assertThat().
                body("places[0].state",equalTo("California"));

    }

    @Test
    public void checkListHasItem(){
        given().
                when().
                get("http://api.zippopotam.us/tr/34295").
                then().
                body("places.'place name'",hasItem("Kartaltepe Mah."));
    }
    @Test
    public void checkNumberOfPlaceNamesInResponseBody() {
        given().
                when().
                get("http://zippopotam.us/tr/34840").
                then().
                assertThat().
                body("places.'place name'", hasSize(2));
    }
}
