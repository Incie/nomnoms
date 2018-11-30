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
    val textTitle = view.text_nom_title
    val textSubtitle = view.text_nom_subtitle
    val textLastEvent = view.text_last_event
    val imageTest: ImageView = view.image_test

    private var model: ModelNoms = ModelNoms(-1, "", "", "", 0)

    private var isImageFitToScreen: Boolean = false

    init {
        view.setOnClickListener(this::onRowClick)
        view.setOnLongClickListener(this::onRowLongClick)
        imageTest.setOnClickListener(this::onImageClick)
    }

    fun onRowLongClick(v: View?):Boolean{
        Log.i("NomsViewHolder", "Delete $adapterPosition")
        viewEvent(AdapterNomsEvent.DELETE_NOMS, adapterPosition)
        return true
    }

    fun onRowClick(v: View?){
        viewEvent(AdapterNomsEvent.VIEW_NOMS, adapterPosition)
    }

    fun onImageClick(v: View?){
        viewEvent(AdapterNomsEvent.NEW_EVENT_NOMS, adapterPosition)
    }

    fun bind(modelNoms : ModelNoms){
        Log.i("NomsViewHolder", "Bind ${modelNoms.itemId}, ${modelNoms.latestDate}")

        this.model = modelNoms

        isImageFitToScreen = false
        textTitle.text = modelNoms.name
        textSubtitle.text = modelNoms.subtitle

        if( modelNoms.latestDate != 0L ) {
            val calendar = Calendar.getInstance()

            val oneDayInMillis = 1000L * 60L * 60L * 24L
            val now = calendar.timeInMillis - (modelNoms.latestDate - (modelNoms.latestDate% oneDayInMillis))
            val daysSinceLast = now / oneDayInMillis;

            val weeksSinceLast = daysSinceLast / 7;

            if( weeksSinceLast > 0 )
                textLastEvent.text = "${weeksSinceLast.toString()}w ${(daysSinceLast % 7).toString()}d"
            else
                textLastEvent.text = "${daysSinceLast.toString()}d"
        } else textLastEvent.text = "<>"
    }
}