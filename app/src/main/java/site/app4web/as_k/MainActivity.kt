package site.app4web.as_k

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.customsearch.Customsearch
import com.google.api.services.customsearch.CustomsearchRequestInitializer

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
    }
    fun onClick(view: View) {
        val myCX = "003159383193150926956:c6m9ah2oy8e" //Your search engine
        val myKey = "AIzaSyCL_iY2ALQxC4w6Qfld253adeI_GGl_bmw"
        val myApp = "APP4WEB"
        val searchQuery = editText.text.toString()  //The query to search
        try {
           val cs = Customsearch.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance(), null)
                .setApplicationName(myApp)
                .setGoogleClientRequestInitializer(CustomsearchRequestInitializer(myKey))
                .build()
            val list = cs.cse().list(searchQuery).setCx(myCX)   //Set search parameter
            val result = list.execute()    //Execute search
            if ((result != null) && (result.items != null)) {   // Вывод результата
                val rezList = ArrayList<String>(result.items.size)
                    result.items.forEach { ri -> rezList.add(ri.title) }
                val adapter :ListAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, rezList)
                 listView.adapter = adapter
                } else {
                    Log.d("AS1", "resultgetItems()= null")
                    Toast.makeText(applicationContext, "resultgetItems()= null", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Log.d("AS1", "Exception")
            Toast.makeText(applicationContext, "Exception", Toast.LENGTH_SHORT).show()
        }

        // Спрятать клавиатуру из kitty
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE)  as InputMethodManager
        imm.hideSoftInputFromWindow(button.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
    }
}
