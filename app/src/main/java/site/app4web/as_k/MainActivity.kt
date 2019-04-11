package site.app4web.as_k

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent


import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.customsearch.Customsearch
import com.google.api.services.customsearch.CustomsearchRequestInitializer
import com.google.api.services.customsearch.model.Search

import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity() {
    val myCX = "003159383193150926956:c6m9ah2oy8e" //Your search engine
    val myKey = "AIzaSyCL_iY2ALQxC4w6Qfld253adeI_GGl_bmw"
    val myApp = "APP4WEB"
    lateinit var cs : Customsearch
    internal var httpEdit: String = ""
    private var pingHttpTask: PingHttpTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

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

         httpEdit = editText.text.toString()
        val Zapros = listTryHttp(httpEdit)
        if (tryHttp1(Zapros)) { // сайт пингуется
            editText.setText(httpEdit)
            Toast.makeText(applicationContext, "Сайт доступен", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(httpEdit)))
            return
        } else {   // сайт НЕ пингуется
            Toast.makeText(applicationContext, "Сайт НЕ доступен", Toast.LENGTH_SHORT).show()
        }

        if (tryHttp2(Zapros)) { // работает изучать
            editText.setText(httpEdit)
            //textView.setText("!!!!!!!!Сайт доступен   $httpEdit")
            Toast.makeText(applicationContext, "!!!!!!!!Сайт доступен  $httpEdit", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(httpEdit)))
        } else {
           // textView.setText("$httpEdit  Сайт НЕ доступен#######")
            Toast.makeText(applicationContext, "$httpEdit Сайт НЕ доступен#######", Toast.LENGTH_SHORT).show()
           // webView.loadUrl("")

        }
        // Вызов GOOGLE Search
        val searchQuery = editText.text.toString()  //The query to search
        val poisk = Poisk().execute (searchQuery)
        val result = poisk.get()
        val LINK = LV(result)
     //   if (LINK != null) httpEdit = LINK
    }
    fun isConnected(): Boolean {
        val ni = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo
        return ni != null && ni.isConnected
    }
    fun listTryHttp(url: String): java.util.ArrayList<String> {
        var url = url

        val Zapros = java.util.ArrayList<String>()                      // 2 Лист сайтов

        // Если точно не САЙТ - в ПОИСК (false)
        if (!url.contains(".")) return Zapros
        url = url.trim { it <= ' ' }.toLowerCase()
        if (url.startsWith(".")) return Zapros
        if (url.endsWith(".")) return Zapros
        if (url.contains(" ")) return Zapros

        // Если это все таки URL - проверка на правильность и http <--> https
        try {
            URL(url)
            // Это наконец есть ли указанный "точно" сайт в Сети
            Zapros.add(url)  // Простая прямая проверка на то что ввели в url
            if (url.startsWith("http:")) Zapros.add(url.replace("http://", "https://"))
            if (url.startsWith("https:")) Zapros.add(url.replace("https://", "http://"))
        } catch (e: MalformedURLException) {
            Toast.makeText(applicationContext, "Запрос не является URL: $url", Toast.LENGTH_SHORT).show()
        }

        // Если сайт без HTTH то приклеть и проверить Сайт или в Поиск
        if (!url.startsWith("http")) Zapros.add("https://$url")
        if (!url.startsWith("http")) Zapros.add("http://$url")

        // Если WWW или м.б. сайт без HTTH то удалить приклеть и проверить Сайт или в Поиск
        if (url.contains("www.")) Zapros.add(url.replace("www.", ""))
        if (url.startsWith("www.")) Zapros.add("http://$url")
        if (url.startsWith("www.")) Zapros.add("https://$url")
        if (url.startsWith("www.")) Zapros.add(url.replace("www.", "http://"))
        if (url.startsWith("www.")) Zapros.add(url.replace("www.", "http://"))

        // TimeUnit.SECONDS.sleep(1);
        return Zapros
    }

    fun tryHttp1(Zapros: java.util.ArrayList<String>): Boolean {
        Toast.makeText(applicationContext, "Стартуют малые проверки задач=" + Zapros.size, Toast.LENGTH_LONG).show()
        // Порождает кучу задач по количеству в листе
        var answer: Boolean = false
        var pingHttpTask: PingHttpTask
        val listpingHttpTask = java.util.ArrayList<PingHttpTask>()    //  лист задач
        for (Str in Zapros) {
            pingHttpTask = PingHttpTask()
            listpingHttpTask.add(pingHttpTask)
            pingHttpTask.execute(Str)
        }
        // Проверяет кто из них жив true и  прибивает
        for (task in listpingHttpTask) {
            try {
                if (task.get(3, TimeUnit.SECONDS)) answer = true
                //if (task.get()) answer = true
            } catch (e: TimeoutException) {
            } catch (e: ExecutionException) {
            } catch (e: InterruptedException) {
            }

            task.cancel(true)
        }
        return answer
    }

    fun tryHttp2(Zapros: java.util.ArrayList<String>): Boolean {
        var answer = false
        Toast.makeText(applicationContext, "Стартует большая проверка шагов= " + Zapros.size, Toast.LENGTH_LONG).show()
        pingHttpTask = PingHttpTask()
        pingHttpTask!!.execute(*Zapros.toTypedArray())
        try {
            try {
                answer = pingHttpTask!!.get(5, TimeUnit.SECONDS)
            } catch (e: TimeoutException) {
                pingHttpTask!!.cancel(true)
            }

        } catch (e: ExecutionException) {
        } catch (e: InterruptedException) {
        }

        pingHttpTask!!.cancel(true)
        return answer
    }

    inner class PingHttpTask : AsyncTask<String, Void, Boolean>() {
        lateinit internal var httpConnection: HttpURLConnection
        //private URL url;
        private var temp: String = ""

        override fun onPreExecute() {}

        override fun doInBackground(vararg urls: String): Boolean {
            for (url in urls) {
                try {
                    httpConnection = URL(url).openConnection() as HttpURLConnection
                    httpConnection.requestMethod = "HEAD"
                    // httpConnection.setReadTimeout(10000);
                    val code = httpConnection.responseCode
                    if (code == HttpURLConnection.HTTP_OK) { // HttpURLConnection.HTTP_OK (200) HttpURLConnection.HTTP_NOT_FOUND
                        temp = url
                        httpEdit = temp
                        try {
                        httpConnection.disconnect()
                        } catch (e: Exception) {}
                        return true
                    }
                } catch (e: MalformedURLException) {
                } catch (e: IOException) {
                }

            }
            temp = "catch $httpEdit"
            return false
        }

        override fun onPostExecute(result: Boolean) {  // Почему-то сюда не попадаю
            if (result) httpEdit = temp
            try {
                httpConnection.disconnect()
            } catch (e: Exception) {
            }

            Toast.makeText(applicationContext, "$result  Подзадача  $temp", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }
    }
    inner class  Poisk :  AsyncTask<String, Void, Search?>() {
        override fun doInBackground(vararg searchQuery: String): Search? {
            var result: Search? = null
            try {
                val list = cs.cse().list(searchQuery[0]).setCx(myCX)   //Set search parameter
                result = list.execute()    //Execute search
                return result;
            } catch (e: Exception) {
                Log.d("POISK", "Exception")
                Toast.makeText(applicationContext, "POISK : Exception", Toast.LENGTH_SHORT).show()
            }
            return result
        }
    }
    fun LV(result:Search?): String? {

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
                    Toast.makeText(applicationContext, "LINK \n  ${result.items[position].link}"  , Toast.LENGTH_LONG).show()
                   // startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(result.items[position].link)))
                  val LINK = result.items[position].link
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(LINK)))
                   //httpEdit = LINK
                    }

            } else {
                Log.d("AS1", "resultgetItems()= null")
                Toast.makeText(applicationContext, "resultgetItems()= null", Toast.LENGTH_SHORT).show()
            }

        return null
    }

}
