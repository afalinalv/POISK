package site.app4web.as_k

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import android.widget.SimpleAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isConnected())Toast.makeText(applicationContext, "Инет Есть", Toast.LENGTH_SHORT).show()
                     else Toast.makeText(applicationContext, "НЕТ Инет ", Toast.LENGTH_SHORT).show()
    }
    fun onClick(view: View) {
        hideKeyboard(view)
        var httpEdit :String?
        httpEdit = editText.text.toString()

        val Zapros = listTryHttp(httpEdit)
            httpEdit = tryHttp1(Zapros)
        if (httpEdit !=null) { // сайт пингуется
            editText.setText(httpEdit)
            Toast.makeText(applicationContext, "tryHttp1 Сайт доступен $httpEdit", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(httpEdit)))
            return
        } else {   // сайт НЕ пингуется
             Toast.makeText(applicationContext, "tryHttp1 Сайт НЕ доступен  ", Toast.LENGTH_SHORT).show()
        }
            httpEdit = tryHttp2(Zapros)
        if (httpEdit !=null) { // сайт пингуется
            editText.setText(httpEdit)
            Toast.makeText(applicationContext, "!!!!!!!!Сайт доступен  $httpEdit", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(httpEdit)))
            return
        } else {
            Toast.makeText(applicationContext, "tryHttp2 Сайт НЕ доступен####### ", Toast.LENGTH_SHORT).show()
        }
        // Вызов GOOGLE Search
        val searchQuery = editText.text.toString()  //The query to search
        val poisk = Poisk().execute (searchQuery)
        try {
            Lv(poisk.get(15, TimeUnit.SECONDS))
            } catch (e: TimeoutException) {
            poisk.cancel(true)
            Toast.makeText(applicationContext, " Вышло Время Поиска ", Toast.LENGTH_SHORT).show()
            listView.adapter = null
        }
    }

    fun Lv(result:ArrayList<HashMap<String, String>>?): String? {
        if (result == null) return null   // Вывод результата
            listView.adapter = SimpleAdapter(
                this, result, android.R.layout.simple_list_item_2,
                arrayOf("Http", "Title"),
                intArrayOf(android.R.id.text1, android.R.id.text2)
            )
            // добвляем для списка слушатель
                listView.setOnItemClickListener { _, _, position, _ ->
                    Toast.makeText(applicationContext, "выбран $position "  , Toast.LENGTH_SHORT).show()
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(result[position]["Http"])))
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
