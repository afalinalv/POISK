package site.app4web.as_k

import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun tryHttp1(Zapros: ArrayList<String>): String? {
    // Порождает кучу задач по количеству в листе
    var answer: String? = null
    var pingHttpTask: PingHttpTask
    val listpingHttpTask = ArrayList<PingHttpTask>()    //  лист задач
    Zapros.forEach { Str ->
        pingHttpTask = PingHttpTask()
        listpingHttpTask.add(pingHttpTask)
        pingHttpTask.execute(Str)
    }
    // Проверяет кто из них жив true и  прибивает
    listpingHttpTask.forEach { task ->
        try {
            if (answer==null) answer =task.get(3, TimeUnit.SECONDS)
        } catch (e: TimeoutException) {
        } catch (e: ExecutionException) {
        } catch (e: InterruptedException) {
        }
        task.cancel(true)
    }
    return answer
}

fun tryHttp2(Zapros: ArrayList<String>): String? {
    var answer: String? = null
    //Toast.makeText(applicationContext, "Стартует большая проверка шагов= " + Zapros.size, Toast.LENGTH_LONG).show()
    val pingHttpTask = PingHttpTask()
    pingHttpTask.execute(*Zapros.toTypedArray())
    try {
        try {
            answer = pingHttpTask.get(5, TimeUnit.SECONDS)
        } catch (e: TimeoutException) {
            pingHttpTask.cancel(true)
        }
    } catch (e: ExecutionException) {
    } catch (e: InterruptedException) {
    }
    pingHttpTask.cancel(true)
    return answer
}
