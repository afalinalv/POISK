package site.app4web.as_k;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.customsearch.model.Search;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import org.json.simple.JSONValue;
//import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Crunchify.com
 * Program: 4 Best ways to convert Java Map to JSONObject.
 * Version: 1.0.0
 * https://crunchify.com/in-java-how-to-convert-map-hashmap-to-jsonobject-4-different-ways/
 *
 *   1  implementation 'com.google.code.gson:gson:2.8.5'
 *   2 нет //implementation 'org.json:json:20180130'
 *   3  implementation 'com.fasterxml.jackson.core:jackson-core:2.9.8'
 *   3  implementation 'com.fasterxml.jackson.core:jackson-databind:2.9.8'
 *   4 нет implementation 'com.googlecode.json-simple:json-simple:1.1.1'
 *
 */

public class  CrunchifyMapToJsonObject  {
    public   CrunchifyMapToJsonObject(Search result) {

        Map<String, String> crunchifyMap = new HashMap<String, String>();
        crunchifyMap.put("Google", "San Jose");
        crunchifyMap.put("Facebook", "Mountain View");
        crunchifyMap.put("Crunchify", "NYC");
        crunchifyMap.put("Twitter", "SFO");
        crunchifyMap.put("Microsoft", "Seattle");
        log("Raw Map ===> " + crunchifyMap);

        // Use this builder to construct a Gson instance when you need to set configuration options other than the default.
        GsonBuilder gsonMapBuilder = new GsonBuilder();

        Gson gsonObject = gsonMapBuilder.create();

        String JSONObject = gsonObject.toJson(crunchifyMap);
        log("\nMethod-1: Using Google GSON ==> " + JSONObject);

        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = prettyGson.toJson(crunchifyMap);

        log("\nPretty JSONObject ==> " + prettyJson);

        // Construct a JSONObject from a Map.  nMethod-2
 //       org.json.JSONObject crunchifyObject = new JSONObject(crunchifyMap);
 //       log("\nMethod-2: Using new JSONObject() ==> " + crunchifyObject);

        try {
            // Default constructor, which will construct the default JsonFactory as necessary, use SerializerProvider as its
            // SerializerProvider, and BeanSerializerFactory as its SerializerFactory.
            String objectMapper = new ObjectMapper().writeValueAsString(crunchifyMap);
            log("\nMethod-3: Using ObjectMapper().writeValueAsString() ==> " + objectMapper);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        // Convert an object to JSON text. If this object is a Map or a List, and it's also a JSONAware, JSONAware will be considered firstly.
  //      String jsonValue = JSONValue.toJSONString(crunchifyMap);
  //      log("\nMethod-4: Using JSONValue.toJSONString() ==> " + jsonValue);


    }

    private static void log(Object print) {
       // System.out.println(print);
        Log.d("Json", ""+print);

    }
}