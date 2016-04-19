package woverines.sfsuapp;


import android.content.Context;

import org.junit.Test;

import woverines.sfsuapp.api.API_RequestBuilder;
import woverines.sfsuapp.api.Callback;
import woverines.sfsuapp.api.HttpRequestorManager;
import woverines.sfsuapp.models.CoursesModels;
import woverines.sfsuapp.models.NULLOBJ;

import static org.junit.Assert.*;

public class HttpRequestGETUnitTest {

    private API_RequestBuilder api_requestBuilder;

    @Test
    public void returnCourseModel(Context ctx){
        //Initliaze the http requstor.
        HttpRequestorManager.initialize(ctx);

        //Instantiate new api_request object.
        api_requestBuilder = new API_RequestBuilder();

        //request model populate
        api_requestBuilder.populateModel("CSC", new CoursesModels(), new Callback() {
            @Override
            public void response(Object object) {
                assertNotNull(object);
                assertEquals(new CoursesModels(), object);
            }

            @Override
            public void error(NULLOBJ nullObj) {
                assertNull(nullObj);
            }
        });

    }
}
