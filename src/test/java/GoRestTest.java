import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import pojo.GoRestUser;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class GoRestTest {
    //https://gorest.co.in/public-api/users?_format=json&access-token=j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap
    @Test
    public void queryParamsTest(){
        given().
                log().uri()
                .param( "access-token", "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" ).
                param( "_format", "json" ).
                when().
                get("https://gorest.co.in/public-api/users").
                then().
                log().status().
                log().body().
                body( "_meta.code", equalTo( 200 ) );
    }

    @Test
    public void basicAuthTest(){
        given()
                .auth()         // basic auth
                .preemptive()// basic auth
                .basic("j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap", "") // basic auth
                 .log().headers()
                .when()
                .get("https://gorest.co.in/public-api/users").
                then().
                log().status().
                log().body().
                body( "_meta.code", equalTo( 200 ) );
    }


    @Test
    public void oAuth2Test(){
        given()
                .auth()
                .oauth2("j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap") // basic OAuth 2
                .log().headers()
                .when()
                .get("https://gorest.co.in/public-api/users").
                then().
                log().status().
                log().body().
                body( "_meta.code", equalTo( 200 ) );
    }

    @Test
    public void noAuthTest(){
        given()
                .when()
                .get("https://gorest.co.in/public-api/users").
                then().
                log().status().
                log().body().
                body( "_meta.code", equalTo( 401 ) );
    }

    @Test
    public void createUserTest() {
        GoRestUser user = new GoRestUser();
        user.setEmail( "asdfvx2@asd.as" );
        user.setFirstName( "My First Name" );
        user.setLastName( "My Last Name" );
        user.setGender( "male" );

        // Create user part
        String userId = given()
                .contentType( ContentType.JSON )
                .auth()
                .oauth2( "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" ) // basic OAuth 2
                .body( user )
                .when()
                .post( "https://gorest.co.in/public-api/users" )
                .then()
                .body( "_meta.code", equalTo( 201 ) )
                .extract().jsonPath().getString( "result.id" );

        // Create user negative case part
        given()
                .contentType( ContentType.JSON )
                .auth()
                .oauth2( "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" ) // basic OAuth 2
                .body( user )
                .when()
                .post( "https://gorest.co.in/public-api/users" )
                .then()
                .body( "_meta.code", equalTo( 422 ) );

        // Get user part
        given()
                .auth()
                .oauth2( "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" ) // basic OAuth 2
                .when()
                .get("https://gorest.co.in/public-api/users/"+userId)
                .then()
                .body( "_meta.code", equalTo( 200 ) )
                .body( "result.email", equalTo( user.getEmail() ) )
        ;

        // Update user part
        Map<String, String> updateUser = new HashMap<>(  );
        updateUser.put( "first_name", "Updated first name" );
        updateUser.put( "last_name", "Updated last name" );

        given()
                .contentType( ContentType.JSON )
                .auth()
                .oauth2( "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" ) // basic OAuth 2
                .body( updateUser )
                .when()
                .patch( "https://gorest.co.in/public-api/users/" + userId )
                .then()
                .body( "_meta.code", equalTo( 200 ) );

        // Get user update check part
        given()
                .auth()
                .oauth2( "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" ) // basic OAuth 2
                .when()
                .get("https://gorest.co.in/public-api/users/"+userId)
                .then()
                .body( "_meta.code", equalTo( 200 ) )
                .body( "result.first_name", equalTo( updateUser.get( "first_name" ) ) )
                .body( "result.last_name", equalTo( updateUser.get( "last_name" ) ) )
        ;

        // Delete user part
        given()
                .auth()
                .oauth2( "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" ) // basic OAuth 2
                .when()
                .delete("https://gorest.co.in/public-api/users/"+userId)
                .then()
                .body( "_meta.code", equalTo( 204 ) )
        ;

        // Get user negative part
        given()
                .auth()
                .oauth2( "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" ) // basic OAuth 2
                .when()
                .get("https://gorest.co.in/public-api/users/"+userId)
                .then()
                .body( "_meta.code", equalTo( 404 ) )
        ;

        // Update user negative part
        Map<String, String> updateUserNegative = new HashMap<>(  );
        updateUserNegative.put( "first_name", "Negative first name" );
        updateUserNegative.put( "last_name", "Negative last name" );

        given()
                .contentType( ContentType.JSON )
                .auth()
                .oauth2( "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" ) // basic OAuth 2
                .body( updateUserNegative )
                .when()
                .patch( "https://gorest.co.in/public-api/users/" + userId )
                .then()
                .body( "_meta.code", equalTo( 404 ) );
    }
}
