package woverines.sfsuapp.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import woverines.sfsuapp.models.CoursesModels;
import woverines.sfsuapp.models.DepartmentsModel;
import woverines.sfsuapp.models.ForumsModel;
import woverines.sfsuapp.models.NULLOBJ;
import woverines.sfsuapp.models.ReviewsModel;


public class HttpRequestorManager {

    private static HttpRequestorManager ourInstance;
    private Context mContext;

    public static HttpRequestorManager initialize(Context ctx) {
        if(ourInstance == null){
            ourInstance = new HttpRequestorManager(ctx);
            return ourInstance;
        }
        return ourInstance;
    }

    public static HttpRequestorManager getInstance() {
        return ourInstance;
    }


    private HttpRequestorManager(Context ctx) {
        //Singleton constructor.
        this.mContext = ctx.getApplicationContext();
    }

    public void makeRESTRequest(String URL, String state, final Callback callback){
        Log.i("VOLLEY", "Call to URL -> " + URL);
        final String flag = state;
        Log.i("VOLLEY", "Flag set -> "+flag);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("VOLLEY", "Successful response.....");
                        if (flag == "courses") {
                            CoursesModels data = new Gson().fromJson(response.toString(), CoursesModels.class);
                            callback.response(data);
                        }else if(flag == "forums") {
                            ForumsModel data =  new Gson().fromJson(response.toString(), ForumsModel.class);
                            callback.response(data);
                        }else if(flag == "reviews") {
                            ReviewsModel data = new Gson().fromJson(response.toString(), ReviewsModel.class);
                            callback.response(data);
                        }else if(flag == "departments") {
                            DepartmentsModel data = new Gson().fromJson(response.toString(), DepartmentsModel.class);
                            callback.response(data);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.error(new NULLOBJ());
                    }
                });


        HttpQueue.getInstance(this.mContext).addToRequestQueue(jsObjRequest);
    }
}
