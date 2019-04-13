package site.app4web.as_k

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent


import android.net.ConnectivityManager
import android.net.Uri
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
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    internal var httpEdit: String = ""
   // private var pingHttpTask: PingHttpTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        if (isConnected())Toast.makeText(applicationContext, "Инет Есть", Toast.LENGTH_SHORT).show()
                     else Toast.makeText(applicationContext, "НЕТ Инет ", Toast.LENGTH_SHORT).show()


    }
    fun onClick(view: View) {
        var answerURL:String? = null
        var startTime :Long = 0
        var endTime :Long = 0
        var deltaTime :Long = 0
        hideKeyboard(view)
         startTime = System.currentTimeMillis()
            httpEdit = editText.text.toString()
        val Zapros = listTryHttp(httpEdit)
            answerURL = tryHttp1(Zapros)
        if (answerURL !=null) { // сайт пингуется
            httpEdit = answerURL
            editText.setText(httpEdit)
            Toast.makeText(applicationContext, "tryHttp1 Сайт доступен", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(httpEdit)))
            return
        } else {   // сайт НЕ пингуется
             endTime = System.currentTimeMillis()
            deltaTime=endTime-startTime
            Toast.makeText(applicationContext, "tryHttp1 Сайт НЕ доступен  $deltaTime", Toast.LENGTH_SHORT).show()
        }
        //Thread.sleep(10000);
        startTime = System.currentTimeMillis()
            answerURL = tryHttp2(Zapros)
        if (answerURL !=null) { // сайт пингуется
            httpEdit = answerURL
            editText.setText(httpEdit)
            //textView.setText("!!!!!!!!Сайт доступен   $httpEdit")
            Toast.makeText(applicationContext, "!!!!!!!!Сайт доступен  $httpEdit", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(httpEdit)))
            return
        } else {
            endTime = System.currentTimeMillis()
            deltaTime=endTime-startTime
           // textView.setText("$httpEdit  Сайт НЕ доступен#######")
            Toast.makeText(applicationContext, "$httpEdit Сайт НЕ доступен####### $deltaTime", Toast.LENGTH_SHORT).show()
           // webView.loadUrl("")
        }
        //Thread.sleep(10000);
        startTime = System.currentTimeMillis()
        // Вызов GOOGLE Search
        val searchQuery = editText.text.toString()  //The query to search
        val poisk = Poisk().execute (searchQuery)
        try {
        val result = poisk.get(15, TimeUnit.SECONDS)
            val Link = Lv(result)
            } catch (e: TimeoutException) {
            poisk.cancel(true)
            endTime = System.currentTimeMillis()
            deltaTime=endTime-startTime
            Toast.makeText(applicationContext, " Вышло Время Поиска $deltaTime", Toast.LENGTH_SHORT).show()
        }

     //   if (LINK != null) httpEdit = LINK
    }

    private inner class MyWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }
    }

    fun Lv(result:Array<Array<String>>?): String? {

            if (result != null)  {   // Вывод результата
                val rezList = ArrayList<String>(result[1].size)
              /*  for (i in 0 until result[0].size){
                    rezList.add(result[0][i])
                }*/
                result[0].forEach { ri -> rezList.add(ri) }

                val adapter: ListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rezList)
                listView.adapter = adapter

                // добвляем для списка слушатель
                listView.setOnItemClickListener { _, _, position, _ ->
                    // по позиции получаем выбранный элемент
                  val selectedItem = rezList[position]
                  val Link = result[1][position]
                    Toast.makeText(applicationContext, "выбран $selectedItem"  , Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, "LINK \n  $Link"  , Toast.LENGTH_LONG).show()
                   // startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(result.items[position].link)))

                   // return Link!!
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Link)))
                   //httpEdit = Link
                    }
            } else {
                Log.d("AS1", "resultgetItems()= null")
                Toast.makeText(applicationContext, "resultgetItems()= null", Toast.LENGTH_SHORT).show()
            }
        return null
    }
    private fun hideKeyboard(view: View) {
        // прячем клавиатуру. view - это кнопка
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
    }
    fun isConnected(): Boolean {
        val ni = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo
        return ni != null && ni.isConnected
    }
}
