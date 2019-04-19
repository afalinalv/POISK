package site.app4web.as_k

import android.os.AsyncTask
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.customsearch.Customsearch
import com.google.api.services.customsearch.CustomsearchRequestInitializer
import com.google.api.services.customsearch.model.Result
import com.google.gson.Gson


/* Обращается к Google Search для поиска передаваемой строки
    Google отдает 10 результатов в Search
    Перебрасываю их с ArraList и отдаю на высветку
 */

class  Poisk : AsyncTask<String, Void, MutableList<Result>?>() {
    val myCX = "003159383193150926956:c6m9ah2oy8e" //Your search engine
    val myKey = "AIzaSyCL_iY2ALQxC4w6Qfld253adeI_GGl_bmw"
    val myApp = "APP4WEB"

    override fun doInBackground(vararg searchQuery: String):MutableList<Result>? {

        val cs = Customsearch.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance(), null)
            .setApplicationName(myApp)
            .setGoogleClientRequestInitializer(CustomsearchRequestInitializer(myKey))
            .build()

            val list = cs.cse().list(searchQuery[0]).setCx(myCX)   //Set search parameter
            val result = list.execute()    //Execute search
            if (result == null) return null
            if (result.items == null) return null

            val resitem: MutableList<Result>?
        resitem = result.items
        val resObj = CrunchifyMapToJsonObject(resitem)



            val sites: ArrayList<HashMap<String, String>>?
                sites = ArrayList<HashMap<String, String>>(result.items.size)

                var map: HashMap<String, String>
                result.items.forEach { res ->
                    map = HashMap()
                    map["Http"]  = res.link
                    map["Title"] = res.title
                    sites.add(map)
                }
                return resitem
    }
}

