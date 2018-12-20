package rolf.nomnoms.nomnoms.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.listitem_nom_events.view.*
import rolf.nomnoms.nomnoms.R
import rolf.nomnoms.nomnoms.model.ModelNomEvent
import rolf.nomnoms.nomnoms.model.NomEventViewModel
import java.text.SimpleDateFormat
import java.util.*

class ViewHolderNomEventLog(view: View) : RecyclerView.ViewHolder(view) {
    private val textTimespan = view.textview_timespan
    private val textDate = view.textview_date
    private val textTitle = view.textview_title
    private val textSubtitle = view.textview_subtitle

    fun bind(nomEvent: NomEventViewModel) {
        textTimespan.text = epochToSpan(nomEvent.nomEvent.date)
        textDate.text = epochToDate(nomEvent.nomEvent.date)
        textTitle.text = nomEvent.nom.name
        textSubtitle.text = nomEvent.nom.subtitle
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


class AdapterNomEventLog (
    private val context: Context,
    private val items : List<NomEventViewModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_nom_events, parent, false)
        return ViewHolderNomEventLog(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderNomEventLog).bind(items[position])
    }
}