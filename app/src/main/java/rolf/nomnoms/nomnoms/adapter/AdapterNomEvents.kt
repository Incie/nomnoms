package rolf.nomnoms.nomnoms.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.listitem_nom_event.view.*
import rolf.nomnoms.nomnoms.R
import rolf.nomnoms.nomnoms.model.ModelNomEvent
import rolf.nomnoms.nomnoms.model.ModelNoms
import java.util.ArrayList

class ViewHolderNomEvent(view: View) : RecyclerView.ViewHolder(view) {
    private val textTimespan = view.textview_timespan
    private val textDate = view.textview_date

    fun bind(nomEvent: ModelNomEvent) {
        textTimespan.text = "${nomEvent.nomId}, ${nomEvent.itemId}"
        textDate.text = nomEvent.date.toString()
    }
}


class AdapterNomEvents (
    private val context: Context,
    private val items : List<ModelNomEvent>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_nom_event, parent, false)
        return ViewHolderNomEvent(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderNomEvent).bind(items[position])
    }
}