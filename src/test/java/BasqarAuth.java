import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BasqarAuth {
    @Test
    public void cookiesTest(){
        Map<String, String> loginCredentials = new HashMap<>(  );
        loginCredentials.put( "username", "nigeria_tenant_admin" );
        loginCredentials.put( "password", "TnvLOl54WxR75vylop2A" );

        Cookies cookies = given()
                .contentType( ContentType.JSON )
                .body( loginCredentials )
                .when()
                .post( "https://basqar.techno.study" )
                .then()
                .statusCode( 200 )
                .extract().response().getDetailedCookies();


        given()
                .cookies( cookies )
                .when()
                .get("https://basqar.techno.study/school-service/api/countries")
                .then()
                .log().body()
                .statusCode( 200 );
    }
}
