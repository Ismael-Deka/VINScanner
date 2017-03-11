package com.example.vinscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.example.vinscanner.MainActivity.LOG_TAG;

/**
 * Created by Ismael on 2/21/2017.
 */

public class QueryUtils {

    private static final String NHTSA_REQUEST_URL_BASE = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevin/";
    private static final String CARS_DOT_COM_URL_BASE = "https://www.cars.com/research/";


    public static Car extractCar(String vin) {
        int errorCode = -1;
        String make = "";
        String model = "";
        String year = "";
        String trim = "";


    Log.e(LOG_TAG,vin);
        try {

            //Forming complete Url from Base Url, VIN number, and format parameter
            String requestUrl =NHTSA_REQUEST_URL_BASE + vin + "?format=json";

            String jsonResponse = makeHttpRequest(createUrl(requestUrl));


            JSONObject reader = new JSONObject(jsonResponse);
            JSONArray arr = reader.getJSONArray("Results");
            for(int i = 0; i < arr.length();i++){
                reader = arr.getJSONObject(i);

                switch(reader.getString("Variable")){
                    case "Error Code":
                        errorCode = Integer.parseInt(reader.getString("ValueId"));
                    case "Make":
                        make = reader.getString("Value");
                        break;
                    case "Model":
                        model = reader.getString("Value");
                        break;
                    case "Model Year":
                        year = reader.getString("Value");
                        break;
                    case "Trim":
                        trim = reader.getString("Value");
                    default:
                        break;

                }

            }




        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the JSON results", e);

        } catch (IOException e){
            Log.e("QueryUtils", "Problem reading from Input Stream", e);
        }
        Bitmap carImage = getCarImage(make,model,year,trim);

        return new Car(errorCode,make,model,year,vin,trim,carImage);
    }

    private static URL createUrl(String requestUrl) {
        URL url = null;

        try {
            url = new URL(requestUrl);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {


        // If the URL is null, then return early.
        if (url == null) {
            return null;
        }

        String response = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                response = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the response.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
                inputStream.close();
            }
        }
        return response;
    }
    private static String readFromStream(InputStream inputStream) throws IOException {
        //Getting String JSON response from InputStream
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }

        }
        return output.toString();
    }

    private static Bitmap getCarImage(String make, String model, String year, String trim){

        Document doc = null;
        try {
            model=model.replace(" ","_");
            model=model.replace("-","_");

            doc = Jsoup.connect(CARS_DOT_COM_URL_BASE+make+"-"+model+"-"+year).get();
            if(isQueryFailed(doc.getElementsByTag("title").first())){
                //Some Models in the NHTSA Database have space in between certain phases(i.e. MITSUBISHI 3000 GT instead of 3000GT)
                //This is to correct for those differences
                doc = Jsoup.connect(CARS_DOT_COM_URL_BASE+make+"-"+model.replace("_","")+"-"+year).get();
            }
            if(isQueryFailed(doc.getElementsByTag("title").first())){
                //Some Model on Cars.com required the trim as well as the model.
                //This statement includes the trim in the request.
                doc = Jsoup.connect(CARS_DOT_COM_URL_BASE+make.toLowerCase()+"-"+model.toLowerCase()+"_"+trim+"-"+year).get();


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements element;
        String imageUrl;

        element= doc.getElementsByAttributeValueContaining("ng-controller","pageStateController as psCtrl");
        String galleryUrls = element.first().getElementsByAttributeValue("name", "mmy-gallery-lightbox").attr("images");

        if(galleryUrls.isEmpty()){
            element= doc.getElementsByAttributeValueContaining("class", "slide nonDraggableImage");
            imageUrl = element.first().attr("src");
        }else {

            imageUrl = galleryUrls.split("\"[&quot|&quot;,&quot;|&quot;]\"")[0];
            imageUrl = imageUrl.substring(2);
        }

        Bitmap carImage = null;

        try {
            InputStream inputStream = createUrl(imageUrl).openStream();
            carImage =BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return carImage;
    }


    private static boolean isQueryFailed(Element element){
        //All failed querys have a title of only Cars.com rather than the name of the vehicle
        if(element.text().equals("Cars.com")){
            return true;
        }else{
            return false;
        }

    }
}
