package rolf.nomnoms.nomnoms

import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.listitem_noms.view.*
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.ModelNomEvent
import rolf.nomnoms.nomnoms.model.ModelNoms
import java.util.*


class ViewHolderNoms(view: View, val viewEvent: (eventId: Int, itemId: Long) -> Unit ) : RecyclerView.ViewHolder(view) {
    val textTitle = view.text_nom_title
    val textSubtitle = view.text_nom_subtitle
    val textLastEvent = view.text_last_event
    val imageTest: ImageView = view.image_test

    private var model: ModelNoms = ModelNoms(-1, "", "", "", 0)

    private var isImageFitToScreen: Boolean = false

    init {
        view.setOnClickListener(this::onRowClick)
        imageTest.setOnClickListener(this::onImageClick)
    }

    fun onRowClick(v:View?){
        viewEvent(adapterPosition, -1)
    }

    fun onImageClick(v:View?){
        viewEvent(-3, adapterPosition.toLong())
    }

    fun bind(modelNoms : ModelNoms){
        Log.i("NomsViewHolder", "Bind ${modelNoms.itemId}, ${modelNoms.latestDate}")

        this.model = modelNoms

        isImageFitToScreen = false
        textTitle.text = modelNoms.name
        textSubtitle.text = modelNoms.subtitle

        if( modelNoms.latestDate != 0L ) {
            val calendar = Calendar.getInstance()
            val now = calendar.timeInMillis - modelNoms.latestDate

            val oneDayInMillis = 1000L * 60L * 60L * 24L

            textLastEvent.text = (now / oneDayInMillis).toString() + "d"
        } else textLastEvent.text = "<>"
    }
}

class AdapterNoms(val context: Context, items : List<ModelNoms>, val createNewNoms: (i: Int) -> Unit ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val nomItems = ArrayList<ModelNoms>()

    init {
        nomItems.addAll(items)
        nomItems.add(ModelNoms(-1, "+", "", "", 0))
    }

    fun onClick(eventId: Int, itemId: Long) {
        Log.i("AdapterNoms", "Event id = ${eventId.toString()}")

        if( eventId == nomItems.size-1 )
            createNewNoms(0)
        else if( eventId == -3 )
            dateEvent(itemId)
    }

    private fun dateEvent(adapterPosition: Long){
        val model = nomItems.get( adapterPosition.toInt() )

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
        return ViewHolderNoms( LayoutInflater.from(parent.context).inflate(R.layout.listitem_noms, parent, false)) { i, i2 -> onClick(i, i2)}
    }

    override fun getItemCount(): Int {
        return nomItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderNoms).bind(nomItems[position])
    }
}