package site.app4web.as_k

import android.util.Log

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper

import com.google.api.services.customsearch.model.Result


import com.google.gson.GsonBuilder
import com.google.gson.Gson




/**
 * @author Crunchify.com
 * Program: 4 Best ways to convert Java Map to JSONObject.
 * Version: 1.0.0
 * https://crunchify.com/in-java-how-to-convert-map-hashmap-to-jsonobject-4-different-ways/
 *
 * 1  implementation 'com.google.code.gson:gson:2.8.5'
 * 2 нет //implementation 'org.json:json:20180130'
 * 3  implementation 'com.fasterxml.jackson.core:jackson-core:2.9.8'
 * 3  implementation 'com.fasterxml.jackson.core:jackson-databind:2.9.8'
 * 4 нет implementation 'com.googlecode.json-simple:json-simple:1.1.1'
 */

fun  CrunchifyMapToJsonObject(resitem: MutableList<Result>?): Gson {


        val crunchifyMap=resitem
      //  val resultMap: MutableList<Result>?
      //  resultMap = result.items

        log("Raw Map ===> $crunchifyMap")

        // Use this builder to construct a Gson instance when you need to set configuration options other than the default.
        val gsonMapBuilder = GsonBuilder()
        val gsonObject = gsonMapBuilder.create()
        val JSONObject = gsonObject.toJson(crunchifyMap)
        log("\nMethod-1: Using Google GSON ==> $JSONObject")

        val prettyGson = GsonBuilder().setPrettyPrinting().create()
        val prettyJson = prettyGson.toJson(crunchifyMap)
        log("\nPretty JSONObject ==> $prettyJson")

        // Construct a JSONObject from a Map.  nMethod-2
//        val crunchifyObject = JSONObject(crunchifyMap)
//        log("\nMethod-2: Using new JSONObject() ==> $crunchifyObject")


        val objectMapper: String?
        try {
            // Default constructor, which will construct the default JsonFactory as necessary, use SerializerProvider as its
            // SerializerProvider, and BeanSerializerFactory as its SerializerFactory.

            objectMapper = ObjectMapper().writeValueAsString(crunchifyMap)
            log("\nMethod-3: Using ObjectMapper().writeValueAsString() ==> $objectMapper")
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }


        // Convert an object to JSON text. If this object is a Map or a List, and it's also a JSONAware, JSONAware will be considered firstly.
//        val jsonValue = JSONValue.toJSONString(crunchifyMap)
//        log("\nMethod-4: Using JSONValue.toJSONString() ==> $jsonValue")
            log("\nMethod-4: Using JSONValue.toJSONString() ==> STOP")

            return prettyGson

    }

    private fun log(print: Any) {
        // System.out.println(print);
        Log.d("Json", "" + print)

    }
