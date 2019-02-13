package rolf.nomnoms.nomnoms.model

import java.text.SimpleDateFormat
import java.util.*


class Epoch {

    companion object {
        fun getSpan(epoch: Long):String {
            val calendar = Calendar.getInstance()

            val oneDayInMillis = 1000L * 60L * 60L * 24L
            val now = calendar.timeInMillis - (epoch - (epoch% oneDayInMillis))
            val daysSinceLast = now / oneDayInMillis;

            val weeksSinceLast = daysSinceLast / 7;

            if( weeksSinceLast > 0 )
                return "${weeksSinceLast}w ${(daysSinceLast % 7)}d"
            else
                return "${daysSinceLast}d"
        }

        fun epochToDate(epoch:Long):String{
            try {
                val sdf = SimpleDateFormat("MM/dd/yyyy")
                val netDate = Date(epoch)
                return sdf.format(netDate)
            } catch (e: Exception) {
                return e.toString()
            }
        }

        fun epochToSpan(epoch: Long):String{
            val calendar = Calendar.getInstance()

            val oneDayInMillis = 1000L * 60L * 60L * 24L
            val now = calendar.timeInMillis - (epoch - (epoch% oneDayInMillis))
            val daysSinceLast = now / oneDayInMillis;

            val weeksSinceLast = daysSinceLast / 7;

            if( weeksSinceLast > 0 )
                return "${weeksSinceLast}w ${(daysSinceLast % 7)}d"
            else
                return "${daysSinceLast}d"
        }
    }
}