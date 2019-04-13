package site.app4web.as_k

import android.os.AsyncTask
import android.util.Log
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.customsearch.Customsearch
import com.google.api.services.customsearch.CustomsearchRequestInitializer
import com.google.api.services.customsearch.model.Search

/* Обращается к Google Search для поиска передаваемой строки
    Google отдает 10 результатов в Search
    передает их наверх не изменяя
 */

class  Poisk : AsyncTask<String, Void, Array<Array<String>>?>() {
    val myCX = "003159383193150926956:c6m9ah2oy8e" //Your search engine
    val myKey = "AIzaSyCL_iY2ALQxC4w6Qfld253adeI_GGl_bmw"
    val myApp = "APP4WEB"
    lateinit var cs : Customsearch
    override fun doInBackground(vararg searchQuery: String): Array<Array<String>>? {
        var result: Search? = null
        cs = Customsearch.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance(), null)
            .setApplicationName(myApp)
            .setGoogleClientRequestInitializer(CustomsearchRequestInitializer(myKey))
            .build()
        try {
            val list = cs.cse().list(searchQuery[0]).setCx(myCX)   //Set search parameter
            result = list.execute()    //Execute search
            if ((result!=null) && (result.items!=null)) {
                val items = result.items
                val Nanswer = result.items.size
                var sites: Array<Array<String>>?
                sites = Array(2) { Array<String>(Nanswer){""}}
                for (i in 0 .. Nanswer-1){
                    sites[0][i] = result.items[i].title
                    sites[1][i] = result.items[i].link

                }
                return sites
            }
        } catch (e: Exception) {
            Log.d("POISK", "Exception")
           // Toast.makeText(applicationContext, "POISK : Exception", Toast.LENGTH_SHORT).show()
        }
        return null
    }
}

