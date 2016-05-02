package woverines.sfsuapp.api;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.concurrent.Future;

import woverines.sfsuapp.models.CoursesModels;
import woverines.sfsuapp.models.DepartmentsModel;
import woverines.sfsuapp.models.ForumsModel;
import woverines.sfsuapp.models.NULLOBJ;
import woverines.sfsuapp.models.ReviewsModel;

public class API_RequestBuilder {

    public API_RequestBuilder(){
        //default constructor.
    }

    public void populateModel(String paramAPI, Object model, final Callback callback){

        String URL = null;
        String flag = null;

        if(model instanceof CoursesModels) URL = API_URLS.base_URL + API_URLS.courses_URL + paramAPI;

        if(model instanceof ForumsModel) URL = API_URLS.base_URL + API_URLS.forums_URL + paramAPI;

        if(model instanceof ReviewsModel) URL = API_URLS.base_URL + API_URLS.reviews_URL + paramAPI;

        if(model instanceof DepartmentsModel) URL = API_URLS.base_URL + API_URLS.departments_URL;

       HttpRequestorManager.getInstance().makeRESTRequest(URL, flag, new Callback() {
           @Override
           public void response(Object object) {
               callback.response(object);
           }

           @Override
           public void error(NULLOBJ nullObj) {
                callback.error(nullObj);
           }
       });
    }

}
