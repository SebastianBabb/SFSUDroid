package woverines.sfsuapp.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


public class HttpRequestorManager {

    private static HttpRequestorManager ourInstance;
    private Context mContext;
    private static String mUrl_Request;

    public static HttpRequestorManager getInstance(Context ctx) {
        if(ourInstance == null){
            return new HttpRequestorManager(ctx);
        }
        return ourInstance;
    }

    private HttpRequestorManager(Context ctx) {
        //Singleton constructor.
        this.mContext = ctx.getApplicationContext();
    }

    public void makeRESTRequest(final Callback callback){
        String url = "http://ironsquishy.com:5656/api/example";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.i("VOLLEY", response.toString());
                       callback.response(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("VOLLEY", error.toString());
                    }
                });


        HttpQueue.getInstance(this.mContext).addToRequestQueue(jsObjRequest);
    }
}
