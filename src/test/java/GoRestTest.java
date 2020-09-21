
import io.restassured.authentication.PreemptiveOAuth2HeaderScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;

import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.GoRestPost;
import pojo.GoRestUser;


import java.util.HashMap;

import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class GoRestTest {

    private RequestSpecification requestSpec;

    //https://gorest.co.in/public-api/users?_format=json&access-token=j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap
    @Test
    public void queryParamsTest(){
        given().
                log().uri()
                .param( "access-token", "jj-ay3daD0ZR14xidz7IUROPXvkjrD_7aVjt" )
                .param( "_format", "json" )
                .when()
                .get("https://gorest.co.in/public-api/users")
                .then()
                .log().status()
                .log().body()
                .body( "code", equalTo( 200 ) );
    }

    @Test
    public void basicAuthTest(){
        given()
                .auth()         // basic auth
                .preemptive()// basic auth
                .basic("pNqJLeycBKwoT7KMcKMSmGXCy4mnroG7ImGc", "") // basic auth
                 .log().headers()
                .when()
                .get("https://gorest.co.in/public-api/users").
                then().
                log().status().
                log().body().
                body( "code", equalTo( 200 ) );
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
                body( "code", equalTo( 200 ) );
    }

    @Test
    public void noAuthTest(){
        given()
                .when()
                .get("https://gorest.co.in/public-api/users").
                then().
                log().status().
                log().body().
                body( "code", equalTo( 401 ) );
    }

    @BeforeClass
    private void createRequestSpec() {
        PreemptiveOAuth2HeaderScheme auth2Scheme = new PreemptiveOAuth2HeaderScheme ();
        auth2Scheme.setAccessToken( "pNqJLeycBKwoT7KMcKMSmGXCy4mnroG7ImGc" );

        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://gorest.co.in/public-api/")
                .setContentType( ContentType.JSON )
                .setAuth(auth2Scheme)
                .log( LogDetail.ALL )
                .build();
    }

    @Test
    public void createPostTest(){
        GoRestUser user = getGoRestUser();
        String userId = getCreatedUserId( user );

        GoRestPost post = new GoRestPost();
        post.setUserId( userId );
        post.setTitle( "new post" );
        post.setBody( "new body" );

        String postId = given()
                .spec( requestSpec )
                .body( post )
                .when()
                .post( "posts" )
                .then()
                .log().body()
                .body( "code", equalTo( 201 ) )
                .contentType( ContentType.JSON )
                .extract().jsonPath().getString( "result.id" );

        Map<String, String> updatePost = new HashMap<>(  );
        updatePost.put( "title", "Updated title" );
        updatePost.put( "body", "Updated body" );

        given()
                .spec( requestSpec )
                .body( updatePost )
                .when()
                .patch( "posts/"+postId )
                .then()
                .body( "code", equalTo( 200 ) );

        given()
                .spec( requestSpec )
                .when()
                .delete("posts/"+postId)
                .then()
                .body( "code", equalTo( 204 ) );

        deleteUserByUserId( userId );
    }

    @Test
    public void createUserTest() {
        GoRestUser user = getGoRestUser();

        // Create user part
        String userId = getCreatedUserId( user );

        // Create user negative case part
        given()
                .spec( requestSpec )
                .body( user )
                .when()
                .post("users")
                .then()
                .body( "code", equalTo( 422 ) );

        // Get user part
        given()
                .spec( requestSpec )
                .when()
                .get("users/" + userId)
                .then()
                .body( "code", equalTo( 200 ) )
                .body( "data.email", equalTo( user.getEmail() ) )
        ;

        // Update user part
        Map<String, String> updateUser = new HashMap<>(  );
        updateUser.put( "first_name", "Updated first name" );
        updateUser.put( "last_name", "Updated last name" );

        given()
                .spec( requestSpec )
                .body( updateUser )
                .when()
                .patch( "users/"+userId )
                .then()
                .body( "code", equalTo( 200 ) );

        // Get user update check part
        given()
                .spec( requestSpec )
                .when()
                .get("users/"+userId)
                .then()
                .body( "code", equalTo( 200 ) )
                .body( "data.first_name", equalTo( updateUser.get( "first_name" ) ) )
                .body( "data.last_name", equalTo( updateUser.get( "last_name" ) ) )
        ;

        // Delete user part
        deleteUserByUserId( userId );

        // Get user negative part
        given()
                .spec( requestSpec )
                .when()
                .get("users/"+userId)
                .then()
                .body( "_meta.code", equalTo( 404 ) )
        ;

        // Update user negative part
        Map<String, String> updateUserNegative = new HashMap<>(  );
        updateUserNegative.put( "first_name", "Negative first name" );
        updateUserNegative.put( "last_name", "Negative last name" );

        given()
                .spec( requestSpec )
                .body( updateUserNegative )
                .when()
                .patch( "users/"+ userId )
                .then()
                .body( "code", equalTo( 404 ) );
    }

    private void deleteUserByUserId(String userId) {
        given()
                .spec( requestSpec )
                .when()
                .delete("users/"+userId)
                .then()
                .body( "code", equalTo( 204 ) )
        ;
    }

    private String getCreatedUserId(GoRestUser user) {
        return given()
                .spec( requestSpec )
                .body( user )
                .when()
                .post("users")
                .then().log().everything()
                .body( "code", equalTo( 201 ) )
                .log().everything()
                .extract().jsonPath().getString( "result.id" );
    }

    private GoRestUser getGoRestUser() {
        GoRestUser user = new GoRestUser();
        user.setEmail( "asdfvx3@asd.as" );
        user.setFirstName( "My First Name" );
        user.setLastName( "My Last Name" );
        user.setGender( "male" );
        return user;
    }
}
