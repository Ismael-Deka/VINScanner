package com.example.vinscanner;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


    public static Car extractCar(String vin) {
        int errorCode = -1;
        String make = "";
        String model = "";
        String year = "";


        try {



            JSONObject reader = new JSONObject(makeHttpRequest(createUrl(vin)));
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


        return new Car(errorCode,make,model,year,vin);
    }

    private static URL createUrl(String vin) {
        URL url = null;
        //Forming complete Url from Base Url, VIN number, and format parameter
        String requestUrl = NHTSA_REQUEST_URL_BASE + vin + "?format=json";


        try {
            url = new URL(requestUrl);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

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
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
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

}
