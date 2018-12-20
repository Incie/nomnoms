package rolf.nomnoms.nomnoms.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.listitem_noms.view.*
import rolf.nomnoms.nomnoms.model.ModelNoms
import java.util.*

enum class AdapterNomsEvent {
    NEW_NOMS,
    DELETE_NOMS,
    EDIT_NOMS,
    NEW_EVENT_NOMS,
    VIEW_NOMS
}

class ViewHolderNoms(view: View, val viewEvent: (eventId: AdapterNomsEvent, itemId: Int) -> Unit ) : RecyclerView.ViewHolder(view) {
    private val textTitle = view.text_nom_title
    private val textSubtitle = view.text_nom_subtitle
    private val textLastEvent = view.text_last_event
    private val imageTest: ImageView = view.image_test

    private var model: ModelNoms = ModelNoms(-1, "", "", "", 0)

    private var isImageFitToScreen: Boolean = false

    init {
        view.setOnClickListener(this::onRowClick)
        view.setOnLongClickListener(this::onRowLongClick)
        imageTest.setOnClickListener(this::onImageClick)
    }

    private fun onRowLongClick(v: View?):Boolean{
        Log.i("NomsViewHolder", "Delete $adapterPosition")
        viewEvent(AdapterNomsEvent.DELETE_NOMS, adapterPosition)
        return true
    }

    private fun onRowClick(v: View?){
        Log.i("NomsViewHolder", "View $adapterPosition - $model.name")
        viewEvent(AdapterNomsEvent.VIEW_NOMS, adapterPosition)
    }

    private fun onImageClick(v: View?){
        viewEvent(AdapterNomsEvent.NEW_EVENT_NOMS, adapterPosition)
    }

    fun bind(modelNoms : ModelNoms){
        this.model = modelNoms

        isImageFitToScreen = false
        textTitle.text = modelNoms.name
        textSubtitle.text = modelNoms.subtitle

        if( modelNoms.latestDate != 0L ) {

        } else textLastEvent.text = "<>"
    }

    fun getSpan(epoch: Long){
        val calendar = Calendar.getInstance()

        val oneDayInMillis = 1000L * 60L * 60L * 24L
        val now = calendar.timeInMillis - (epoch - (epoch% oneDayInMillis))
        val daysSinceLast = now / oneDayInMillis;

        val weeksSinceLast = daysSinceLast / 7;

        if( weeksSinceLast > 0 )
            textLastEvent.text = "${weeksSinceLast.toString()}w ${(daysSinceLast % 7).toString()}d"
        else
            textLastEvent.text = "${daysSinceLast.toString()}d"
    }
}