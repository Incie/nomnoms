package rolf.nomnoms.nomnoms.adapter

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.support.v4.util.Pair
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import rolf.nomnoms.nomnoms.R
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.ModelNomEvent
import rolf.nomnoms.nomnoms.model.ModelNoms
import rolf.nomnoms.nomnoms.model.NomSort
import java.util.*

class AdapterNoms (
    private val context: Context,
    items : List<ModelNoms>,
    private val NomEvent: (adapterNomEvent: AdapterNomsEvent, itemId: Long, sharedView: Array<Pair<View,String>>?) -> Unit ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val nomItems = ArrayList<ModelNoms>()

    init {
        nomItems.addAll(items)
        nomItems.add(ModelNoms(-1, "+", "", "", 0, -1))
    }

    private fun onViewEvent(eventId: AdapterNomsEvent, adapterPosition: Int, sharedView: Array<Pair<View,String>>?) {
        Log.i("AdapterNoms", "Event id = $eventId")

        val itemId = nomItems[adapterPosition].itemId;

        when {
            adapterPosition == nomItems.size-1 ->
                NomEvent(AdapterNomsEvent.NEW_EVENT_NOMS, -1, null)
            eventId == AdapterNomsEvent.NEW_EVENT_NOMS ->
                dateEvent(adapterPosition)
            eventId == AdapterNomsEvent.DELETE_NOMS ->
                NomEvent(eventId, itemId, null)
            eventId == AdapterNomsEvent.VIEW_NOMS ->
                NomEvent(eventId, itemId, sharedView)
            eventId == AdapterNomsEvent.OPEN_GALLERY ->
                NomEvent(eventId, itemId, sharedView)
        }
    }

    private fun dateEvent(adapterPosition: Int){
        val model = nomItems[adapterPosition]

        val calendar = Calendar.getInstance()
        val dpd = DatePickerDialog(context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    val millis = cal.timeInMillis

                    Toast.makeText(context, "Added new event for '${model.name}'", Toast.LENGTH_SHORT).show()
                    val da = DataAccess(context)
                    da.insertNomEvent(ModelNomEvent(-1, model.itemId, millis) )
                    da.updateLatestNomDate(model.itemId, millis)

                    if( millis > model.latestDate ) {
                        nomItems[adapterPosition] = ModelNoms(model.itemId, model.name, model.subtitle, model.description, millis, -1)
                        notifyItemChanged(adapterPosition)
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        dpd.show()
        dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
    }

    private var imageTransitionName = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_noms, parent, false )
        val image = view.findViewById<ImageView>(R.id.image_test)
        image.transitionName = image.transitionName + "_$imageTransitionName++"

        return ViewHolderNoms(view, viewEvent = this::onViewEvent)
    }

    override fun getItemCount(): Int {
        return nomItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderNoms).bind(nomItems[position])
    }

    fun sort(sortType: NomSort) {
        if(sortType == NomSort.Alphabetical ){
            val list = nomItems.sortedWith(compareBy(ModelNoms::name))
            nomItems.clear()
            nomItems.addAll( list )
        }

        if( sortType == NomSort.Ascending ){
            val list = nomItems.sortedWith(compareBy(ModelNoms::latestDate))
            nomItems.clear()
            nomItems.addAll( list )
        }

        if( sortType == NomSort.Descending ){
            val list = nomItems.sortedWith(compareBy(ModelNoms::latestDate))
            nomItems.clear()
            nomItems.addAll( list )
            nomItems.reverse()
        }

        notifyDataSetChanged()
    }
}