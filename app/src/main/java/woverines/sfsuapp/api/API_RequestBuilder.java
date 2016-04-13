package woverines.sfsuapp.api;


import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.concurrent.Future;

import woverines.sfsuapp.models.CoursesModels;
import woverines.sfsuapp.models.ForumsModel;
import woverines.sfsuapp.models.ReviewsModel;

public class API_RequestBuilder {
    private String mURL;
    private int flag;

    public API_RequestBuilder(){
        //default constructor.
    }

    public API_RequestBuilder(String pURL){
        this.mURL = pURL;
    }



    public void populateModel(String paramAPI, Object model, final Callback callback){

        String URL = API_URLS.base_URL;
        if(model instanceof CoursesModels) URL = URL + API_URLS.courses_URL+paramAPI;

        if(model instanceof ForumsModel) URL = URL + API_URLS.forums_URL+paramAPI;

        if(model instanceof ReviewsModel) URL = URL + API_URLS.reviews_URL+paramAPI;

         Callback cb = new Callback() {
            @Override
            public void response(Object object) {

                JSONObject json = (JSONObject) object;

                if (object instanceof CoursesModels) {
                    CoursesModels data = new Gson().fromJson(json.toString(), CoursesModels.class);
                    callback.response(data);
                }

                if(object instanceof ForumsModel) {
                    ForumsModel data =  new Gson().fromJson(json.toString(), ForumsModel.class);
                    callback.response(data);
                }

                if(object instanceof ReviewsModel) {
                    ReviewsModel data = new Gson().fromJson(json.toString(), ReviewsModel.class);
                    callback.response(data);
                }
            }

             @Override
             public void error(Object object) {
                 //TODO handle error;
             }
         };

       HttpRequestorManager.getInstance().makeRESTRequest(URL, cb);
    }

}
