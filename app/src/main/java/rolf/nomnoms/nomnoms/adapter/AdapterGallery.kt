package rolf.nomnoms.nomnoms.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import rolf.nomnoms.nomnoms.model.NomImageModel
import kotlinx.android.synthetic.main.listitem_gallery.view.*
import rolf.nomnoms.nomnoms.R


class AdapterGallery (val context: Context, val model : List<NomImageModel>, val showImageId: Int, private val imageEvent: (nomImage: NomImageModel) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listitem_gallery, parent, false )
        return ViewHolderGalleryImage(view, imageEvent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderGalleryImage).bind(model[position])
    }

    override fun getItemCount(): Int {
        return model.count()
    }
}

class ViewHolderGalleryImage(private val view : View,  val imageEvent: (nomImage: NomImageModel) -> Unit) : RecyclerView.ViewHolder(view) {
    private val imageView = view.imageView!!

    private lateinit var imageModel : NomImageModel

    init {
        view.setOnClickListener{
            imageEvent(imageModel)
        }
    }

    fun bind(nomImageModel : NomImageModel){
        imageModel = nomImageModel
        Glide.with(view.context)
            .load(imageModel.imagePath)
            .into(imageView)
    }
}
