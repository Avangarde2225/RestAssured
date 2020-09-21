import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.Todo;
import pojo.task7.Page;

import static io.restassured.RestAssured.given;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Tasks {

    /** Task 1
     * create a request to https://httpstat.us/203
     * expect status 203
     * expect content type text
     * **/
    @BeforeClass
    public void init(){
        RestAssured.baseURI = "https://httpstat.us";
    }

    @Test
    public void statusCodeTest() {
        given().
                log().all().
                when().
                get("/200").
                then().
                statusCode(200).
                contentType(ContentType.TEXT).
                log().body();
    }


    /** Task 2
     * create a request to https://httpstat.us/418
     * expect status 418
     * expect content type text
     * expect body to be equal to "418 I'm a teapot"
     * **/

    @Test
    public void bodyTest(){
        given().
                log().all().
                when().
                get("/418").
                then().
                log().all().
                statusCode(418).
                contentType(ContentType.TEXT).
                assertThat().
                body(equalTo( "418 I'm a teapot"));

    }

    /** Task 3
     * create a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type json
     * expect title in response body to be "quis ut nam facilis et officia qui"
     * **/

    @Test
    public void jasonTest(){
        given().
                log().all().
                when().
                get("https://jsonplaceholder.typicode.com/todos/2").
                then().
                log().all().
                statusCode(200).
                contentType(ContentType.JSON).
                assertThat().
                body("title",equalTo( "quis ut nam facilis et officia qui"));


    }
    @Test
    public void jasonTestAlt(){
       String title= given().
                log().all().
                when().
                get("https://jsonplaceholder.typicode.com/todos/2").
                then().
                log().all().
                statusCode(200).
                contentType(ContentType.JSON).
               extract().path( "title" );


        Assert.assertEquals(title,"quis ut nam facilis et officia qui");


    }

    /** Task 4
     * create a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type json
     * expect response completed status to be false
     * **/

    @Test
    public void jasonFalseTest(){
        given().
                log().all().
                when().
                get("https://jsonplaceholder.typicode.com/todos/2").
                then().
                log().all().
                statusCode(200).
                contentType(ContentType.JSON).
                body("completed", equalTo(false));


    }

    /** Task 5
     * create a request to https://reqres.in/api/users?page=2
     * expect status 200
     * expect content type json
     * expect data to contain first name "George"
     * **/
    @Test
    public void task5(){
        given().
                log().all().
                when().
                get("https://reqres.in/api/users?page=2").
                then().
                log().body().
                statusCode(200).
                contentType(ContentType.JSON).
                body("data[0].first_name", equalTo("Michael"));


    }


    /** Task 6
     * create a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type json
     * create a pojo and extract it from response body
     * expect completed property of your pojo to be false
     * **/

    @Test
    public void task6(){
        Todo todo = given().
                log().all().
                when().
                get("https://jsonplaceholder.typicode.com/todos/2").
                then().
                log().all().
                statusCode(200).
                contentType(ContentType.JSON).
                extract().body().as( Todo.class );

        Assert.assertFalse(todo.getCompleted());


    }
    /** Task 7
     * create a request to https://reqres.in/api/users?page=2
     * expect status 200
     * expect content type json
     * create a pojos to extract response body
     * expect "data" property of your pojo to be not empty
     * **/

    @Test
    public void task7() {
        Page page2 = given().
                log().all().
                when().
                get("https://reqres.in/api/users?page=2").
                then().
                log().body().
                statusCode(200).
                contentType(ContentType.JSON).

                extract().as(Page.class);

        assertThat(page2.getData(), not(empty()));


    }

        /** Task 8
     * create a pojo for posts
     * create a post request to https://gorest.co.in/public-api/posts
     * send your pojo inside body of your post request
     * expect status 201
     * expect content type json
     * extract post id from body
     * **/

    /** Task 9
     * create a patch request to https://gorest.co.in/public-api/posts/{postId}
     * send your pojo inside body of your post request
     * expect status 200
     * expect content type json
     *
     * after that delete the post using postId
     * expect status 204
     * **/
}
