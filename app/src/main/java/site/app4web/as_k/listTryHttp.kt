package site.app4web.as_k

import java.net.MalformedURLException
import java.net.URL

fun listTryHttp(url: String): java.util.ArrayList<String> {
    var url = url

    val Zapros = ArrayList<String>()                      // 2 Лист сайтов

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
      // Toast.makeText(applicationContext, "Запрос не является URL: $url", Toast.LENGTH_SHORT).show()
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

    return Zapros
}
