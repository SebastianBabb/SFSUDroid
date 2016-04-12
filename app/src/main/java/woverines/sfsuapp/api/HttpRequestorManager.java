package woverines.sfsuapp.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


public class HttpRequestorManager {

    private static HttpRequestorManager ourInstance = new HttpRequestorManager();
    private Context mContext;
    private static String mUrl_Request;

    public static HttpRequestorManager getInstance() {

        return ourInstance;
    }

    private HttpRequestorManager() {
        //Singleton constructor.
    }

    public void makeRESTRequest(Context ctx){
        String url = "http://ironsquishy.com:5656/api/example";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("VOLLEY", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("VOLLEY", error.toString());
                    }
                });


        HttpQueue.getInstance(ctx).addToRequestQueue(jsObjRequest);
    }
}
