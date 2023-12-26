package com.ismaelDeka.vinscanner;

import static com.ismaelDeka.vinscanner.ui.MainActivity.LOG_TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ismaelDeka.vinscanner.car.Car;
import com.ismaelDeka.vinscanner.car.CarAttribute;
import com.ismaelDeka.vinscanner.car.CarComplaintAttribute;
import com.ismaelDeka.vinscanner.car.RecallAttribute;

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
import java.util.ArrayList;

/**
 * Created by Ismael on 2/21/2017.
 */

public class QueryUtils {


    private static final String NHTSA_VIN_DECODE_URL_BASE = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinextended/";
    private static final String NHTSA_RECALL_URL_BASE = "https://api.nhtsa.gov/recalls/recallsByVehicle?";
    private static final String NHTSA_COMPLAINTS_URL_BASE = "https://api.nhtsa.gov/complaints/complaintsByVehicle?";
    private static final String NHTSA_VEHICLE_LOGO_BASE = "https://vpic.nhtsa.dot.gov/decoder/Images/Logos/";



    public static Car extractCar(String vin) {
        Car decodedCar = new Car();

        try {

            //Forming complete Url from Base Url, VIN number, and format parameter
            String vinUrl = NHTSA_VIN_DECODE_URL_BASE + vin + "?format=json";
            String jsonResponse = makeHttpRequest(createUrl(vinUrl));

            if(jsonResponse == null){
                return new Car(vin);
            }

            Car car = getCarInfo(jsonResponse);
            String recallUrl;
            String complaintUrl;
            if(car.getModel().contains(" ")) {
                String model = car.getModel().replace(" ", "");
                recallUrl = NHTSA_RECALL_URL_BASE + "make=" + car.getMake() + "&model="+ model +"&modelYear="+car.getYear();
                complaintUrl = NHTSA_COMPLAINTS_URL_BASE + "make=" + car.getMake() + "&model="+ model +"&modelYear="+car.getYear();
            } else {
                recallUrl = NHTSA_RECALL_URL_BASE + "make=" + car.getMake() + "&model="+ car.getModel() +"&modelYear="+car.getYear();
                complaintUrl = NHTSA_COMPLAINTS_URL_BASE + "make=" + car.getMake() + "&model="+ car.getModel() +"&modelYear="+car.getYear();
            }


            jsonResponse = makeHttpRequest(createUrl(recallUrl));

            if(jsonResponse == null){

                return new Car(vin);
            }

            ArrayList<RecallAttribute> recallInfo = getRecallInfo(jsonResponse);

            jsonResponse = makeHttpRequest(createUrl(complaintUrl));

            ArrayList<CarComplaintAttribute> complaints = getComplaints(jsonResponse);


            decodedCar = new Car(car.getErrorCode(), car.getMake(), car.getModel(), car.getTrim(), car.getYear(), vin,
                    car.getAttributes(), recallInfo,complaints,getCarLogo(car.getMake()),null);



    } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the JSON results", e);

        } catch (IOException e){
            Log.e("QueryUtils", "Problem reading from Input Stream", e);
        }

        return decodedCar;
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

    private static ArrayList<RecallAttribute> getRecallInfo(String jsonResponse) throws JSONException {
        ArrayList<RecallAttribute> recallInfo = new ArrayList<>();

        if(jsonResponse == null){
            return recallInfo;
        }

        JSONObject reader = new JSONObject(jsonResponse);
        JSONArray arr = reader.getJSONArray("results");
        String campaignNumber;
        String component;
        String summary;
        String consequence;
        String remedy;
        String date;
        for(int i = 0; i < arr.length();i++){
            reader = arr.getJSONObject(i);
            campaignNumber = reader.getString("NHTSACampaignNumber");
            component = reader.getString("Component");
            summary = reader.getString("Summary");
            consequence = reader.getString("Consequence");
            remedy = reader.getString("Remedy");
            date = reader.getString("ReportReceivedDate");
            recallInfo.add(new RecallAttribute(campaignNumber, component, summary, consequence, remedy, date));

        }

        return recallInfo;
    }

    private static ArrayList<CarComplaintAttribute> getComplaints(String jsonResponse) throws JSONException {
        ArrayList<CarComplaintAttribute> complaints = new ArrayList<>();

        if(jsonResponse == null){
            return complaints;
        }

        JSONObject reader = new JSONObject(jsonResponse);
        JSONArray arr = reader.getJSONArray("results");

        String odiNumber;
        String crash;
        String fire;
        int numberInjured;
        int numberDeaths;
        String dateIncident = null;
        String dateFiled;
        String component;
        String summary;
        for(int i = 0; i < arr.length();i++){
            reader = arr.getJSONObject(i);
            odiNumber = reader.getString("odiNumber");
            component = reader.getString("components");
            summary = reader.getString("summary");
            numberDeaths= reader.getInt("numberOfDeaths");
            numberInjured= reader.getInt("numberOfInjuries");
            if(reader.has("dateOfIncident")) {
                dateIncident = reader.getString("dateOfIncident");
            }
            dateFiled = reader.getString("dateComplaintFiled");
            crash =reader.getString("crash");
            fire = reader.getString("fire");
            complaints.add(new CarComplaintAttribute(odiNumber,crash,fire,numberInjured,numberDeaths,
                                                    dateIncident,dateFiled,component,summary));

        }
        return complaints;
    }

    private static Car getCarInfo(String jsonResponse) throws JSONException {
        String errorCode = "";
        String make = "";
        String model = "";
        String year = "";
        String trim = "";

        ArrayList<CarAttribute> attributes = new ArrayList<>();
        JSONObject reader = new JSONObject(jsonResponse);
        JSONArray arr = reader.getJSONArray("Results");


        for(int i = 0; i < arr.length();i++) {
            reader = arr.getJSONObject(i);
            String variable = reader.getString("Variable");
            String value = reader.getString("Value");
            switch (variable) {
                case "Error Code":
                    errorCode = reader.getString("ValueId");
                case "Make":
                    make = value;
                    break;
                case "Model":
                    model = value;
                    break;
                case "Model Year":
                    year = value;
                    break;
                case "Trim":
                    trim = value;
                default:
                    if (!value.equals("null"))
                        attributes.add(new CarAttribute(variable, value,CarCategoryFinder.getCarCategory(variable)));

            }
        }



        return new Car(errorCode, make, model,trim, year, attributes);

    }

    private static String makeHttpRequest(URL url) throws IOException {


        // If the URL is null, then return early.
        if (url == null) {
            return null;
        }

        String response = null;
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
                Log.e(LOG_TAG, "URL: " + url);
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the response.", e);

        } finally {
            if (urlConnection != null&&inputStream!=null) {
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



    public static Bitmap getCarLogo(String make){
        Bitmap logo = null;

        try {
            InputStream inputStream = createUrl(NHTSA_VEHICLE_LOGO_BASE+make+".jpg").openStream();
            logo =BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logo;
    }
}
