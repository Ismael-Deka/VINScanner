package com.example.vinscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.vinscanner.car.Car;
import com.example.vinscanner.car.CarAttribute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.vinscanner.ui.MainActivity.LOG_TAG;

/**
 * Created by Ismael on 2/21/2017.
 */

public class QueryUtils {


    private static final String NHTSA_VIN_DECODE_URL_BASE = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinextended/";
    private static final String NHTSA_RECALL_URL_BASE = "https://one.nhtsa.gov/webapi/api/Recalls/vehicle/modelyear/";
    private static final String CARS_DOT_COM_URL_BASE = "https://www.cars.com/research/";
    private static final String NHTSA_VEHICLE_LOGO_BASE = "https://vpic.nhtsa.dot.gov/decoder/Images/Logos/";
    private static boolean isTrimIncluded = false;
    private static String MSRP = "N/A";


    public static Car extractCar(String vin) {
        Car decodedCar = new Car(-1,null,null,null,null,null,null,null,null,null,null);
        Bitmap[] carImages;


    Log.e(LOG_TAG,vin);
        try {

            //Forming complete Url from Base Url, VIN number, and format parameter
            String vinUrl = NHTSA_VIN_DECODE_URL_BASE + vin + "?format=json";
            Log.e(LOG_TAG,vin);
            String jsonResponse = makeHttpRequest(createUrl(vinUrl));

            Car car = getCarInfo(jsonResponse);
            String recallUrl;
            if(car.getModel().contains(" ")) {
                String model = car.getModel().replace(" ", "");
                recallUrl = NHTSA_RECALL_URL_BASE + car.getYear() + "/make/" + car.getMake() + "/model/"+model+"?format=json";
            }else {
                recallUrl = NHTSA_RECALL_URL_BASE + car.getYear() + "/make/" + car.getMake() + "/model/"+car.getModel()+"?format=json";
            }

            Log.e(LOG_TAG,recallUrl);
            jsonResponse = makeHttpRequest(createUrl(recallUrl));

            ArrayList<RecallAttribute> recallInfo = getRecallInfo(jsonResponse);

            carImages = getCarImage(car.getMake(),car.getModel(),car.getYear(),car.getTrim(),car.getErrorCode());

            if(isTrimIncluded && !car.getTrim().equals("null"))
                decodedCar = new Car(car.getErrorCode(),car.getMake(),car.getModel()+" "+car.getTrim(),car.getTrim(),car.getYear(),vin,carImages,car.getAttributes(),recallInfo,getCarLogo(car.getMake()),MSRP);
            else {
                decodedCar = new Car(car.getErrorCode(), car.getMake(), car.getModel(), car.getTrim(), car.getYear(), vin, carImages, car.getAttributes(), recallInfo,getCarLogo(car.getMake()),MSRP);
            }


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
        JSONObject reader = new JSONObject(jsonResponse);
        JSONArray arr = reader.getJSONArray("Results");
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
            consequence = reader.getString("Conequence");
            remedy = reader.getString("Remedy");
            date = reader.getString("ReportReceivedDate");
            recallInfo.add(new RecallAttribute(campaignNumber, component, summary, consequence, remedy, date));

        }

        return recallInfo;
    }

    private static Car getCarInfo(String jsonResponse) throws JSONException {
        int errorCode = -1;
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
                    errorCode = Integer.parseInt(reader.getString("ValueId"));
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



        return new Car(errorCode, make, model,trim, year, null, null, attributes,null,null,null);

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

    private static Bitmap[] getCarImage(String make, String model, String year, String trim,int errorCode){
        if(errorCode != 0){
            return null;
        }

        Document doc = null;
        try {
            model=model.replace(" ","_");
            model=model.replace("-","_");
            model=model.replace("&","and");

            doc = Jsoup.connect(CARS_DOT_COM_URL_BASE+make+"-"+model+"-"+year).get();
            Log.e(LOG_TAG,CARS_DOT_COM_URL_BASE+make+"-"+model+"-"+year);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(doc == null){
            try {
                //Some Models in the NHTSA Database have space in between certain phases(i.e. MITSUBISHI 3000 GT instead of 3000GT)
                //This is to correct for those differences
                doc = Jsoup.connect(CARS_DOT_COM_URL_BASE+make+"-"+model.replace("_","")+"-"+year).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(LOG_TAG,CARS_DOT_COM_URL_BASE+make+"-"+model.replace("_","")+"-"+year);
        }
        if(doc == null){
            try {
                //Some Model on Cars.com required the trim as well as the model.
                //This statement includes the trim in the request.
                doc = Jsoup.connect(CARS_DOT_COM_URL_BASE+make.toLowerCase()+"-"+model.toLowerCase()+"_"+trim+"-"+year).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(LOG_TAG,CARS_DOT_COM_URL_BASE+make.toLowerCase()+"-"+model.toLowerCase()+"_"+trim+"-"+year);
            isTrimIncluded = true;
        }
        if (doc == null){
            //if all other query methods fail.
            return null;
        }

        Elements element;
        ArrayList<String> imageUrls = new ArrayList<>();

        element= doc.getElementsByAttributeValueContaining("ng-controller","pageStateController as psCtrl");
        String galleryUrls = element.first().getElementsByAttributeValue("name", "mmy-gallery-lightbox").attr("images");

        Elements msrp = doc.getElementsByAttributeValueContaining("itemprop","priceSpecification");
        String str = msrp.attr("content");
        if(!str.equals("")) {
            String[] strs = str.split("-");
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            strs[0] = formatter.format(Integer.valueOf(strs[0]));
            strs[1]= formatter.format(Integer.valueOf(strs[1]));
            MSRP = "$"+strs[0]+" - $"+strs[1];
        }


        if(galleryUrls.isEmpty()){
            element= doc.getElementsByAttributeValueContaining("class", "slide nonDraggableImage");
            imageUrls.add(element.first().attr("src"));
        }else {
            String[] urls = galleryUrls.split("\"[&quot|&quot;,&quot;|&quot;]\"");
            imageUrls.add(urls[0].substring(2));
            for(int i =1; i < urls.length-1; i++) {
                imageUrls.add(urls[i]);
            }
            String lastUrl = urls[urls.length-1];
            imageUrls.add(lastUrl.substring(0,lastUrl.length()-2));

        }

        Bitmap[] carImages = new Bitmap[imageUrls.size()];

        for(int i = 0; i < carImages.length; i++)
        try {
            InputStream inputStream = createUrl(imageUrls.get(i)).openStream();
            carImages[i] =BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return carImages;
    }




    public static Bitmap getCarLogo(String make){
        Bitmap logo = null;
        Log.e(LOG_TAG,NHTSA_VEHICLE_LOGO_BASE+make+".jpg");
        try {
            InputStream inputStream = createUrl(NHTSA_VEHICLE_LOGO_BASE+make+".jpg").openStream();
            logo =BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(LOG_TAG,(logo == null)+"");
        return logo;
    }
}
