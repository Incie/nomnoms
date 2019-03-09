package rolf.nomnoms.nomnoms.model

import java.text.SimpleDateFormat
import java.util.*


class Epoch {

    companion object {

        fun getEpochFrom(year: Int, month: Int, day: Int):Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            return calendar.timeInMillis
        }

        fun getSpan(epoch: Long):String {
            val calendar = Calendar.getInstance()

            val oneDayInMillis = 1000L * 60L * 60L * 24L
            val now = calendar.timeInMillis - (epoch - (epoch% oneDayInMillis))
            val daysSinceLast = now / oneDayInMillis;

            val weeksSinceLast = daysSinceLast / 7;

            return if( weeksSinceLast > 0 )
                "${weeksSinceLast}w ${(daysSinceLast % 7)}d"
            else
                "${daysSinceLast}d"
        }

        fun epochToDate(epoch:Long):String{
            return try {
                val sdf = SimpleDateFormat("MM/dd/yyyy")
                val netDate = Date(epoch)
                sdf.format(netDate)
            } catch (e: Exception) {
                e.toString()
            }
        }

        fun epochToSpan(epoch: Long):String{
            val calendar = Calendar.getInstance()

            val oneDayInMillis = 1000L * 60L * 60L * 24L
            val now = calendar.timeInMillis - (epoch - (epoch% oneDayInMillis))
            val daysSinceLast = now / oneDayInMillis;

            val weeksSinceLast = daysSinceLast / 7;

            return if( weeksSinceLast > 0 )
                "${weeksSinceLast}w ${(daysSinceLast % 7)}d"
            else
                "${daysSinceLast}d"
        }
    }
}