package rolf.nomnoms.nomnoms

import android.app.DatePickerDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import kotlinx.android.synthetic.main.listitem_noms.view.*
import android.widget.LinearLayout
import android.widget.Toast
import java.util.*


class ViewHolderNoms(view: View, val create: (i: Int) -> Unit ) : RecyclerView.ViewHolder(view) {
    val textTitle = view.text_nom_title
    val textSubtitle = view.text_nom_subtitle
    val imageTest: ImageView = view.image_test

    private var isImageFitToScreen: Boolean = false

    init {
        view.setOnClickListener(this::onRowClick)
        imageTest.setOnClickListener(this::onImageClick)
    }

    fun onRowClick(v:View?){
        create(adapterPosition)
    }

    fun onImageClick(v:View?){
//        create(-1)
        val calendar = Calendar.getInstance()
        DatePickerDialog(textTitle.context,
            { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                val millis = cal.timeInMillis
                Toast.makeText(textTitle.context, millis.toString(), Toast.LENGTH_SHORT).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))
                .show()
    }

    fun bind(s : ModelNoms){
        isImageFitToScreen = false
        textTitle.text = s.name
        textSubtitle.text = s.subtitle
    }
}

class AdapterNoms(items : List<ModelNoms>, val createNewNoms: (i: Int) -> Unit ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val nomItems = ArrayList<ModelNoms>()

    init {
        nomItems.addAll(items)
        nomItems.add(ModelNoms(-1, "+", ""))
    }

    fun onClick(i: Int) {
        if( i == nomItems.size-1 )
            createNewNoms(0)

        if( i == -1 )
            createNewNoms(-1)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderNoms( LayoutInflater.from(parent.context).inflate(R.layout.listitem_noms, parent, false)) { i -> onClick(i)}
    }

    override fun getItemCount(): Int {
        return nomItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderNoms).bind(nomItems[position])
    }
}