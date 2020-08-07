package review;

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
}
