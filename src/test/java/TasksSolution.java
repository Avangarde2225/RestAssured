import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.Todo;
import pojo.task7.Page;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TasksSolution {
    @Test(enabled = false)
    public void task1() {
        given().
                when().
                get( "https://httpstat.us/203" ).
                then().
                statusCode( 203 ).
                contentType( ContentType.TEXT ).
                log().body().
                body( equalTo( "203 Non-Authoritative Information" ) );
    }

    @Test(enabled = false)
    public void task2() {
        given().
                when().
                get( "https://httpstat.us/418" ).
                then().
                statusCode( 418 ).
                contentType( ContentType.TEXT ).
                body( equalTo( "418 I'm a teapot" ) );
    }

    @Test
    public void task3() {
        given().
                when().
                get( "https://jsonplaceholder.typicode.com/todos/2" ).
                then().
                statusCode( 200 ).
                contentType( ContentType.JSON ).
                body( "title", equalTo( "quis ut nam facilis et officia qui" ) );
    }

    @Test
    public void task3alternative() {
        String title = given().
                when().
                get( "https://jsonplaceholder.typicode.com/todos/2" ).
                then().
                statusCode( 200 ).
                contentType( ContentType.JSON ).
                log().body().
                extract().path( "title" );
        Assert.assertEquals( title, "quis ut nam facilis et officia qui" );
    }

    @Test
    public void task4() {
        given().
                when().
                get( "https://jsonplaceholder.typicode.com/todos/2" ).
                then().
                statusCode( 200 ).
                contentType( ContentType.JSON ).
                log().body().
                body( "completed", equalTo( false ) );
    }

    @Test
    public void task4alternative() {
        Boolean status = given().
                when().
                get( "https://jsonplaceholder.typicode.com/todos/2" ).
                then().
                statusCode( 200 ).
                contentType( ContentType.JSON ).
                extract().path( "completed" );
        Assert.assertFalse( status );
    }

    @Test
    public void task5() {
        given().
                when().
                get( "https://reqres.in/api/users?page=2" ).
                then().
                statusCode( 200 ).
                contentType( ContentType.JSON ).
                body( "data.first_name", hasItem( "George" ) );
    }

    @Test
    public void task6() {
        Todo todo = given().
                when().
                get( "https://jsonplaceholder.typicode.com/todos/2" ).
                then().
                statusCode( 200 ).
                contentType( ContentType.JSON ).
                extract().as( Todo.class );
        System.out.println(todo);
        Assert.assertFalse( todo.getCompleted() );
    }


    @Test
    public void task7() {
        Page page2 = given().
                when().
                get( "https://reqres.in/api/users?page=2" ).
                then().
                log().body().
                statusCode( 200 ).
//                body( "data", not( empty() ) ).
                contentType( ContentType.JSON ).
                extract().as( Page.class );

//        Assert.assertNotEquals( page2.getData().size(), 0, "Data should not be empty");

        assertThat( page2.getData(),  not(empty() ));
    }
}
