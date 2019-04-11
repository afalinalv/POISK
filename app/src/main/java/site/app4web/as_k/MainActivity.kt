package site.app4web.as_k

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
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
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    val myCX = "003159383193150926956:c6m9ah2oy8e" //Your search engine
    val myKey = "AIzaSyCL_iY2ALQxC4w6Qfld253adeI_GGl_bmw"
    val myApp = "APP4WEB"
    lateinit var cs : Customsearch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        if (isConnected())
            Toast.makeText(applicationContext, "Инет Есть", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(applicationContext, "НЕТ Инет ", Toast.LENGTH_SHORT).show()

        cs = Customsearch.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance(), null)
            .setApplicationName(myApp)
            .setGoogleClientRequestInitializer(CustomsearchRequestInitializer(myKey))
            .build()
    }
    fun onClick(view: View) {
        // Спрятать клавиатуру из kitty
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE)  as InputMethodManager
        imm.hideSoftInputFromWindow(button.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)

        var TryText = editText.text.toString()
        if (tryHttp(TryText)) { // сайт пингуется
            Toast.makeText(applicationContext, "Сайт доступен", Toast.LENGTH_SHORT).show()
            TryText = editText.text.toString()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TryText)))
            return
        } else {   // сайт НЕ пингуется
            Toast.makeText(applicationContext, "Сайт НЕ доступен", Toast.LENGTH_SHORT).show()
        }
        val searchQuery = editText.text.toString()  //The query to search
        Poisk(searchQuery)
    }
    fun isConnected(): Boolean {
        val ni = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo
        return ni != null && ni.isConnected
    }
    fun tryHttp(url: String): Boolean {
        var url=url
        // Если точно не САЙТ - в ПОИСК
        if (!url.contains("."))     return false
        url = url.trim { it <= ' ' }
        if (url.startsWith("."))    return false
        if (url.endsWith("."))      return false
        if (url.contains(" "))      return false
        // Если WWW или м.б. сайт без HTTH то удалить приклеть и проверить Сайт или в Поиск
        url = url.toLowerCase()
        if (url.contains("www."))       if (pingHttp(url.replace("www.", ""))) return true
        if (url.startsWith("www."))     if (pingHttp("http://$url")) return true
        if (url.startsWith("www."))     if (pingHttp("https://$url")) return true
        if (url.startsWith("www."))     if (pingHttp(url.replace("www.", "http://"))) return true
        if (url.startsWith("www."))     if (pingHttp(url.replace("www.", "https://"))) return true
        if (!url.startsWith("http"))    if (pingHttp("http://$url")) return true
        if (!url.startsWith("http"))    if (pingHttp("https://$url")) return true
        // Если это все таки URL - проверка на правильность и http <--> https
        try { URL(url) } catch (e: MalformedURLException) { return false }
        // Это наконец есть ли указанный "точно" сайт в Сети
        if (pingHttp(url)) return true  // Простая прямая проверка на то что ввели в url
        if (url.startsWith("http:"))    if (pingHttp(url.replace("http://", "https://"))) return true
        if (url.startsWith("https:"))   if (pingHttp(url.replace("https://", "http://"))) return true

        return false
    }

    fun pingHttp(url: String): Boolean {
        val httpConnection: HttpURLConnection
        try {
            httpConnection = URL(url).openConnection() as HttpURLConnection
            httpConnection.requestMethod = "HEAD"
            if (httpConnection.responseCode == 200) {
                editText.setText(url)
              //  Toast.makeText(applicationContext, "Сайт доступен  $url", Toast.LENGTH_LONG).show()
                return true
            }
        } catch (e: IOException) {  }
        // Toast.makeText(applicationContext, "САЙТ $url НЕ доступен", Toast.LENGTH_SHORT).show()
        return false
    }
    fun Poisk(searchQuery: String): Boolean {
        try {
            val list = cs.cse().list(searchQuery).setCx(myCX)   //Set search parameter
            val result = list.execute()    //Execute search
            if ((result != null) && (result.items != null)) {   // Вывод результата
                val rezList = ArrayList<String>(result.items.size)
                result.items.forEach { ri -> rezList.add(ri.title) }

                val adapter: ListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rezList)
                listView.adapter = adapter

                // добвляем для списка слушатель
                listView.setOnItemClickListener { _, _, position, _ ->
                    // по позиции получаем выбранный элемент
                        val selectedItem = rezList[position]
                    Toast.makeText(applicationContext, "выбран $selectedItem"  , Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, "выбран \n  ${result.items[position].link}"  , Toast.LENGTH_LONG).show()
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(result.items[position].link)))
                    }
                return true
            } else {
                Log.d("AS1", "resultgetItems()= null")
                Toast.makeText(applicationContext, "resultgetItems()= null", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("POISK", "Exception")
            Toast.makeText(applicationContext, "POISK : Exception", Toast.LENGTH_SHORT).show()
        }
        return false
    }

}
