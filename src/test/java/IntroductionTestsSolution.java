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

public class IntroductionTestsSolution {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;

    @BeforeClass
    public  void createResponseSpecification() {
        responseSpec = new ResponseSpecBuilder().
                expectStatusCode(200).
                expectContentType(ContentType.JSON).
                build();
    }

    @BeforeClass
    public void createRequestSpecification() {
        requestSpec = new RequestSpecBuilder().
                setBaseUri("http://zippopotam.us").
                setAccept(ContentType.JSON).
                build();
    }

    @Test
    public void checkPlaceNameInResponseBodyWithResponseSpec() {
        given().
                spec(requestSpec). // using spec here
                when().
                get("us/90210").
                then()

                .spec(responseSpec)// using spec here
                .body("places[0].'place name'", equalTo("Beverly Hills"));
    }


    @Test
    public void statusCodeTestWithRequestSpec() {
        given().
                spec(requestSpec).  // using spec here
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
    public void simpleGetTest() {
        given().
                when().
                get( "/us/90210" ).
                then().
                log().body()
                .statusCode( 200 );
    }
    @Test
    public void extractBody(){
       String placeName= given().
                when().
                get("/tr/34295").
                then().
               log().body().
                extract()
                .path( "places[1].'place name'");
        Assert.assertEquals(placeName,"Beşyol Mah.");
    }

    @Test
    public void simpleResponseTypeTest() {
        given().
                when().
                get( "/us/90210" ).
                then().
                log().body()
                .assertThat()
                .contentType( ContentType.JSON );
    }

    @Test
    public void logRequestAndResponseDetails() {
        given().
                log().all(). // add this
                when().
                get("http://zippopotam.us/us/90210").
                then().
                log().body();  // and this
    }

    @Test
    public void checkReponseBody() {
        given().
                when().
                get("http://zippopotam.us/us/90210").
                then().
                log().body()
                .body( "places[0].state", equalTo( "California" ) );
    }

    @Test
    public void checkReponseBodyPostCode() {
        given().
                when().
                get("http://zippopotam.us/us/90210").
                then().
                body( "'post code'", equalTo( "90210" ) );
    }

    @Test
    public void checkListHasItem(){
        given().
                when().
                get("/tr/34295").
                then().
                body( "places.'place name'",  hasItem( "Kartaltepe Mah." ));
    }

    @Test
    public void  checkListSize(){
        given().
                when().
                get("/tr/34840").
                then().
                body( "places", hasSize( 2 ) );
    }

    @Test
    public void combinedTest(){
        given().
                when().
                get( "/us/90210" ).
                then().
                statusCode( 200 ).
                contentType( ContentType.JSON ).
                body( "places[0].state", equalTo( "California" ) );
    }

    @Test
    public void pathParameterTest(){
        given().
                log().uri().
                pathParam( "country", "us" ).
                pathParam( "zipcode", "90210" ).
                when().
                get("/{country}/{zipcode}").
                then().
                log().body()
                .log().status().
                statusCode( 200 );
    }

    //https://gorest.co.in/public-api/users?_format=json&access-token=j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap
    @Test
    public void queryParamsTest(){
        given().
                log().uri().
                param( "access-token", "j6XoJSutZrv-ikB-4X4_Zndi54_iqSZES-Ap" ).
                param( "_format", "json" ).
                when().
                get("https://gorest.co.in/public-api/users").
                then().
                log().status().
                log().body().
                statusCode( 200 )
        .body( "result", not(empty()) );
    }


    @Test
    public void extractValueFromResponseBody(){
        String placeName = given().
                when().
                get( "us/90210" ).
                then().
                log().body().
                extract().
                path( "places[0].'place name'" );

        Assert.assertEquals(placeName, "Beverly Hills");
    }
}
