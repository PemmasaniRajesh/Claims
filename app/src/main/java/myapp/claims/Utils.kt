package myapp.claims

import java.text.SimpleDateFormat
import java.util.*

class Utils {

    public fun dateTime(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return simpleDateFormat.format(timeStamp)
    }
}