package rolf.nomnoms.nomnoms.adapter

import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import rolf.nomnoms.nomnoms.R
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.ModelNomEvent
import rolf.nomnoms.nomnoms.model.ModelNoms
import java.util.*

class AdapterNoms(
    val context: Context,
    items : List<ModelNoms>,
    val NomEvent: (adapterNomEvent: AdapterNomsEvent, itemId: Long) -> Unit ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val nomItems = ArrayList<ModelNoms>()

    init {
        nomItems.addAll(items)
        nomItems.add(ModelNoms(-1, "+", "", "", 0))
    }

    fun onViewEvent(eventId: AdapterNomsEvent, adapterPosition: Int) {
        Log.i("AdapterNoms", "Event id = ${eventId.toString()}")

        val itemId = nomItems[adapterPosition].itemId;

        if( adapterPosition == nomItems.size-1 ) {
            //AdapterNomsEvent.NEW_EVENT_NOMS
            NomEvent(AdapterNomsEvent.NEW_EVENT_NOMS, -1)
            return;
        }
        else if( eventId == AdapterNomsEvent.NEW_EVENT_NOMS )
            dateEvent(adapterPosition)
        else if( eventId == AdapterNomsEvent.DELETE_NOMS )
            NomEvent(eventId, itemId)
        else if( eventId == AdapterNomsEvent.VIEW_NOMS )
            NomEvent(eventId, itemId)
    }

    private fun dateEvent(adapterPosition: Int){
        val model = nomItems[adapterPosition]

        val calendar = Calendar.getInstance()
        DatePickerDialog(context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                val millis = cal.timeInMillis

                Toast.makeText(context, millis.toString(), Toast.LENGTH_SHORT).show()
                val da = DataAccess(context)
                da.insertNomEvent(ModelNomEvent(-1, model.itemId, millis) )
                da.updateLatestNomDate(model.itemId, millis)

                if( millis > model.latestDate ) {
                    nomItems.set(adapterPosition.toInt(), ModelNoms(model.itemId, model.name, model.subtitle, model.description, millis) )
                    notifyItemChanged(adapterPosition.toInt())
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))
            .show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderNoms(
            LayoutInflater.from(parent.context).inflate(
                R.layout.listitem_noms,
                parent,
                false
            ),
            viewEvent = this::onViewEvent
        )
    }

    override fun getItemCount(): Int {
        return nomItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderNoms).bind(nomItems[position])
    }
}