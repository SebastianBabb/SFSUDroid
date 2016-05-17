package woverines.sfsuapp.api;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.concurrent.Future;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.QueryStringSigningStrategy;
import woverines.sfsuapp.models.CoursesModels;
import woverines.sfsuapp.models.DepartmentsModel;
import woverines.sfsuapp.models.ForumsModel;
import woverines.sfsuapp.models.NULLOBJ;
import woverines.sfsuapp.models.Professors;
import woverines.sfsuapp.models.ReviewsModel;

public class API_RequestBuilder {

    public API_RequestBuilder(){
        //default constructor.
    }

    public void populateModel(String paramAPI, Object model, final Callback callback){

        String URL = null;
        String flag = null;

        if(model instanceof CoursesModels) {
            URL = API_URLS.base_URL + API_URLS.courses_URL + paramAPI;
            flag = "courses";
        } else if(model instanceof ForumsModel){
            URL = API_URLS.base_URL + API_URLS.forums_URL + paramAPI;
            flag = "forums";
        } else if(model instanceof ReviewsModel){
            URL = API_URLS.base_URL + API_URLS.reviews_URL + paramAPI;
            flag = "reviews";
        } else if(model instanceof DepartmentsModel){
            URL = API_URLS.base_URL + API_URLS.departments_URL;
            flag = "departments";
        } else if(model instanceof Professors){
            URL = API_URLS.base_URL + API_URLS.professor_URL + paramAPI;
            flag = "professors";
        }
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

    public void getSchoolTwitter(Object model, final Callback callback){
        String URL = setupTwitterAuthentication();
        String flag = "twitter";

        Log.i("TWITTER", "MAKING Twitter request");
        HttpRequestorManager.getInstance().makeRESTRequest(URL, flag, new Callback() {
            @Override
            public void response(Object object) { callback.response(object);}

            @Override
            public void error(NULLOBJ nullObj) {}
        });

    }


    private String setupTwitterAuthentication(){
        Log.i("TWITTER", "Preparing authentication....");
        String signURL = "";
        String requestUrl = "https://api.twitter.com/1.1/search/tweets.json?q=%23SFSU";
        String Consumer_Key = "g6PWXQyD5DMiqqFuR47Zn3q88";
        String Consumer_Secret = "kifpdIKdJhbYwyjIjaAEv2MvQyXlOXSQzKW8XNKHwpwWkxqrRK";

        String Access_Token = "3fq1bJDIvtsiEiEcScBe4XZtQBZ5AfbGPaHI0Mo5sP4ou";
        String Access_Secret = "3279797876-SWCSY2D5E3FHlvZS6aM2CoRV7urLcWsKAgtrsvQ";

        OAuthConsumer consumer = new DefaultOAuthConsumer(Consumer_Key, Consumer_Secret);
        consumer.setMessageSigner(new HmacSha1MessageSigner());
        consumer.setTokenWithSecret(Access_Token, Access_Secret);
        consumer.setSendEmptyTokens(true);
        consumer.setSigningStrategy(new QueryStringSigningStrategy());

        try{
            signURL = consumer.sign(requestUrl);
            Log.i("TWITTER", "New Signed URL: "  + signURL);
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }

        /*OAuthConsumer consumer = new CommonsHttpOAuthConsumer(YelpAuthKeys._CONSUMER_KEY, YelpAuthKeys._CONSUMER_SECRET);

        consumer.setMessageSigner(new HmacSha1MessageSigner());
        consumer.setTokenWithSecret(YelpAuthKeys._TOKEN, YelpAuthKeys._TOKEN_SECRET);
        consumer.setSendEmptyTokens(true);

        consumer.setSigningStrategy(new QueryStringSigningStrategy());

        //Sign the URL.
        try {
            Log.i(YELP, "Signing url request.");

            signedQuery = consumer.sign(requestUrl);

        } catch (OAuthMessageSignerException e) {

            Log.e(YELP, "OAuthMessageSignerException thrown");
            e.printStackTrace();

        } catch (OAuthExpectationFailedException e) {

            Log.e(YELP, "OAuthExpectationFailedException thrown.");
            e.printStackTrace();

        } catch (OAuthCommunicationException e) {

            Log.e(YELP, "OAuthCommunicationException thrown.");
            e.printStackTrace();
        }*/

        return signURL;
    }
}
