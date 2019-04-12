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

class  Poisk :  AsyncTask<String, Void, Search?>() {
    val myCX = "003159383193150926956:c6m9ah2oy8e" //Your search engine
    val myKey = "AIzaSyCL_iY2ALQxC4w6Qfld253adeI_GGl_bmw"
    val myApp = "APP4WEB"
    lateinit var cs : Customsearch
    override fun doInBackground(vararg searchQuery: String): Search? {
        var result: Search? = null
        cs = Customsearch.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance(), null)
            .setApplicationName(myApp)
            .setGoogleClientRequestInitializer(CustomsearchRequestInitializer(myKey))
            .build()
        try {
            val list = cs.cse().list(searchQuery[0]).setCx(myCX)   //Set search parameter
            result = list.execute()    //Execute search
            val items = result.items
            return result;
        } catch (e: Exception) {
            Log.d("POISK", "Exception")
           // Toast.makeText(applicationContext, "POISK : Exception", Toast.LENGTH_SHORT).show()
        }
        return result
    }
}