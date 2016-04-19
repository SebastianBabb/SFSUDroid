package woverines.sfsuapp.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import woverines.sfsuapp.models.NULLOBJ;


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

    public void makeRESTRequest(String URL, final Callback callback){

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       callback.response(response);
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
