package rolf.nomnoms.nomnoms.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.listitem_noms.view.*
import rolf.nomnoms.nomnoms.model.Epoch.Companion.getSpan
import rolf.nomnoms.nomnoms.model.ModelNoms
import android.support.v4.util.Pair
import com.bumptech.glide.Glide
import rolf.nomnoms.nomnoms.R
import rolf.nomnoms.nomnoms.dataaccess.DataAccess

enum class AdapterNomsEvent {
    NEW_NOMS,
    DELETE_NOMS,
    EDIT_NOMS,
    NEW_EVENT_NOMS,
    VIEW_NOMS,
    OPEN_GALLERY
}

class ViewHolderNoms(private val view: View, val viewEvent: (eventId: AdapterNomsEvent, itemId: Int, sharedView: Array<Pair<View,String>>?) -> Unit ) : RecyclerView.ViewHolder(view) {
    private val textTitle = view.text_nom_title
    private val textSubtitle = view.text_nom_subtitle
    private val textLastEvent = view.text_last_event
    private val imageTest: ImageView = view.image_test

    private var model: ModelNoms = ModelNoms(-1, "", "", "", 0, -1)

    private var isImageFitToScreen: Boolean = false

    init {
        view.setOnClickListener(this::onRowClick)
        view.setOnLongClickListener(this::onRowLongClick)
        imageTest.setOnClickListener(this::onImageClick)
        imageTest.setOnLongClickListener(this::onImageLongClick)
    }

    private fun onImageLongClick(V: View?):Boolean {
        Log.i("NomsViewHolder", "Open_Gallery $adapterPosition")
        val list : Array<Pair<View,String>> = arrayOf(Pair<View,String>(imageTest, imageTest.transitionName) )
        viewEvent(AdapterNomsEvent.OPEN_GALLERY, adapterPosition, list)
        return true
    }

    private fun onRowLongClick(v: View?):Boolean{
        Log.i("NomsViewHolder", "Delete $adapterPosition")
        viewEvent(AdapterNomsEvent.DELETE_NOMS, adapterPosition, null)
        return true
    }

    private fun onRowClick(v: View?){
        Log.i("NomsViewHolder", "View $adapterPosition - $model.name")

        val list : Array<Pair<View,String>> = arrayOf(Pair(view, "nom_panel"), Pair<View,String>(imageTest, imageTest.transitionName) )
        viewEvent(AdapterNomsEvent.VIEW_NOMS, adapterPosition, list)
    }

    private fun onImageClick(v: View?){
        viewEvent(AdapterNomsEvent.NEW_EVENT_NOMS, adapterPosition, null)
    }

    fun bind(modelNoms : ModelNoms){
        this.model = modelNoms

        isImageFitToScreen = false
        textTitle.text = modelNoms.name
        textSubtitle.text = modelNoms.subtitle

        if( modelNoms.latestDate == 0L )
            textLastEvent.text = "<>"
        else
            textLastEvent.text = getSpan(modelNoms.latestDate)

        if( modelNoms.defaultImage < 0 )
            Glide.with(view.context).load(R.drawable.ai_launcher).into(imageTest)
        else {
            val imagePath = DataAccess(view.context).getImagePathById(modelNoms.defaultImage)
            Log.i("ViewHolderNoms", "Loading Image $imagePath")
            Glide.with(view.context).load(imagePath).into(imageTest)
        }
    }
}