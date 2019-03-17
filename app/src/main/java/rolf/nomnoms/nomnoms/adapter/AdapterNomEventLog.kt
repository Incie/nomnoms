package rolf.nomnoms.nomnoms.adapter

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.listitem_nom_events.view.*
import rolf.nomnoms.nomnoms.R
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.Epoch.Companion.epochToDate
import rolf.nomnoms.nomnoms.model.Epoch.Companion.epochToSpan
import rolf.nomnoms.nomnoms.model.NomEventViewModel

class ViewHolderNomEventLog(view: View, onEvent: (Int, Int) -> Unit) : RecyclerView.ViewHolder(view) {
    private val textTimespan = view.textview_timespan
    private val textDate = view.textview_span
    private val textTitle = view.textview_title
    private val textSubtitle = view.textview_subtitle

    init {
        view.setOnLongClickListener {
            val position = adapterPosition
            onEvent(0, position)
            return@setOnLongClickListener true
        }
    }

    fun bind(nomEvent: NomEventViewModel) {
        textTimespan.text = epochToSpan(nomEvent.nomEvent.date)
        textDate.text = epochToDate(nomEvent.nomEvent.date)
        textTitle.text = nomEvent.nom.name
        textSubtitle.text = nomEvent.nom.subtitle
    }
}


class AdapterNomEventLog( private val context: Context, private var items: List<NomEventViewModel> ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var model = ArrayList<NomEventViewModel>()
    private var filter = ""

    init { updateModel() }

    fun setFilter(newFilter : String){
        filter = newFilter.toLowerCase()
        updateModel()
    }

    fun getFilter() : String {
        return filter
    }

    private fun updateModel(){
        model.clear()

        if( filter.isNotEmpty() ) {
            for (i in items) {
                if (i.nom.name.toLowerCase().contains(filter))
                    model.add(i)
            }
        } else {
            model.addAll(items)
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_nom_events, parent, false)
        return ViewHolderNomEventLog(view, this::onEventAction)
    }

    override fun getItemCount(): Int {
        return model.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderNomEventLog).bind(model[position])
    }

    private fun onEventAction(eventId: Int, adapterPosition: Int)
     {
        val model = model[adapterPosition]

        if( eventId == 0 ) {
            val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            builder.setTitle("Delete Event")
            builder.setMessage("Delete event '${model.nom.name}'?")
            builder.setPositiveButton("Delete it") { _, _ ->
                val dataAccess = DataAccess(context)
                dataAccess.deleteEventById(model.nomEvent.eventId)
                dataAccess.findAndSetLatesNomDate(model.nomEvent.nomId)
                Toast.makeText(context, "Deleted ${model.nom.name}'s Event", Toast.LENGTH_SHORT).show()

                items = items.filter { it.nomEvent.eventId != model.nomEvent.eventId }

                updateModel()
                notifyItemRemoved(adapterPosition)
            }

            builder.create().show()
        }
    }
}