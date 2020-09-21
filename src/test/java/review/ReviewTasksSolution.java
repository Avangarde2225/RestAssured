package review;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveOAuth2HeaderScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.GoRestPost;
import pojo.GoRestUser;

import java.util.Random;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ReviewTasksSolution {
    private RequestSpecification requestSpec;

    @BeforeClass
    public void init(){
        RestAssured.baseURI = "http://api.zippopotam.us";
    }

    @Test
    public  void task1() {
        given()
                .when()
                .get("us/90210")
                .then()
        .body( "places[0].'place name'", equalTo( "Beverly Hills" ) )
                ;
    }

    @Test
    public void task2(){
        given()
                .pathParam( "country", "us" )
                .pathParam( "zipcode", "90210" )
                .when()
                .get("/{country}/{zipcode}")
                .then()
                .body( "places", not( empty()) )
                ;
    }

    @Test(dataProvider = "task3data")
    public void task3(String placeName, String country, String zipCode){
        given()
                .pathParam( "country", country )
                .pathParam( "zipcode", zipCode )
                .when()
                .get("/{country}/{zipcode}")
                .then()
                .body( "places.'place name'", hasItem( placeName ) )
        ;
    }

    @DataProvider
    public Object[][] task3data() {
        return new Object[][] {
                {"Beverly Hills", "us", "90210"},
                {"Fort-de-France", "MQ", "97200"},
                {"Çiçekli Köyü", "TR", "01000"},
        };
    }

    @Test(dataProvider = "task4data")
    public void task4(Integer numberOfPlaces, String country, String zipCode){
        given()
                .pathParam( "country", country )
                .pathParam( "zipcode", zipCode )
                .when()
                .get("/{country}/{zipcode}")
                .then()
                .log().body()
                .body( "places.'place name'", hasSize( numberOfPlaces ) )
        ;
    }

    @DataProvider
    public Object[][] task4data() {
        return new Object[][] {
                {1, "us", "90210"},
                {1, "MQ", "97200"},
                {71, "TR", "01000"},
        };
    }

    @BeforeClass
    private void createRequestSpec() {
        PreemptiveOAuth2HeaderScheme auth2Scheme = new PreemptiveOAuth2HeaderScheme ();
        auth2Scheme.setAccessToken( "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" );

        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://gorest.co.in/public-api/")
                .setContentType( ContentType.JSON )
                .setAuth(auth2Scheme)
                .log( LogDetail.ALL )
                .build();
    }
    @Test
    public void task5() {
        GoRestUser user = getGoRestUser();
        String userId = getCreatedUserId( user );

        //Testing search
            given()
                    .spec( requestSpec )
                    .param( "first_name", user.getFirstName() )
                    .when()
                    .get("users")
                    .then()
            .body( "result.id", hasItem( userId ) )
            ;

        deleteUserByUserId( userId );
    }

    private String getCreatedUserId(GoRestUser user) {
        return given()
                .spec( requestSpec )
                .body( user )
                .when()
                .post("users")
                .then().log().everything()
                .body( "_meta.code", equalTo( 201 ) )
                .log().everything()
                .extract().jsonPath().getString( "result.id" );
    }
    private void deleteUserByUserId(String userId) {
        given()
                .spec( requestSpec )
                .when()
                .delete("users/"+userId)
                .then()
                .body( "_meta.code", equalTo( 204 ) )
        ;
    }
    private GoRestUser getGoRestUser() {
        GoRestUser user = new GoRestUser();
        user.setEmail( "asdfasdasdvx3@asd.as" );
        user.setFirstName( "My First Name" );
        user.setLastName( "My Last Name" );
        user.setGender( "male" );
        return user;
    }


    @Test
    public void task6() {
        GoRestUser user = getGoRestUser();
        String userId = getCreatedUserId( user );

        Integer randomNumber = new Random(  ).nextInt(10);
        System.out.println(randomNumber);
        for(int i = 0; i < randomNumber; i++) {
            createPost( userId );

        }

        // verify that the number of post is equal to the number of post you created
        given()
                .spec( requestSpec )
                .param( "user_id", userId )
                .when()
                .get("posts")
                .then()
                .body( "_meta.totalCount", equalTo( randomNumber ) )
                .body( "result", hasSize( randomNumber ) )
        .body( "result.user_id", everyItem( equalTo( userId ) ) )
        ;


        deleteUserByUserId( userId );
    }

    private void createPost(String userId) {
        GoRestPost post = new GoRestPost();
        post.setUserId( userId );
        post.setTitle( "new post" );
        post.setBody( "new body" );

        given()
                .spec( requestSpec )
                .body( post )
                .when()
                .post( "posts" )
                .then()
                .log().body()
                .body( "_meta.code", equalTo( 201 ) );
    }
}
