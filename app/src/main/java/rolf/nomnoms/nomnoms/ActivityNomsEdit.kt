package rolf.nomnoms.nomnoms

import android.app.Application
import android.arch.lifecycle.*
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_noms_edit.*
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.ModelNoms


class NomViewModel(app: Application) : AndroidViewModel(app) {
    private val liveDataNom = MutableLiveData<ModelNoms>()
    private val dataAccess : DataAccess = DataAccess(app)

    private var modelId : Long? = null

    fun setModelId(modelId: Long)
    {
        this.modelId = modelId
    }

    fun getNoms() : MutableLiveData<ModelNoms> {
        if( liveDataNom.value == null ){
            val dbModel = dataAccess.getNomById(modelId!!)
            liveDataNom.value = dbModel
        }

        return liveDataNom
    }

    fun saveNoms(title: String, subtitle: String, description: String){
        dataAccess.updateNoms(modelId!!, title, subtitle, description)
    }
}


class ActivityNomsEdit : AppCompatActivity() {
    private lateinit var title : EditText
    private lateinit var subtitle : EditText
    private lateinit var description : EditText

    private lateinit var viewModel : NomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms_edit)

        title = findViewById(R.id.textview_name)
        subtitle = findViewById(R.id.textview_subtitle_nom)
        description = findViewById(R.id.textview_description_noms)

        button_save.setOnClickListener {
            viewModel.saveNoms(title.text.toString(), subtitle.text.toString(), description.text.toString())
            finish()
        }
        button_cancel.setOnClickListener { finish() }

        viewModel = ViewModelProviders.of(this).get(NomViewModel::class.java)
        viewModel.setModelId( intent.getLongExtra("nom_id", -1) )

        viewModel.getNoms().observe(this, Observer<ModelNoms> {
            if( it == null ) {
                //clear?
                title.setText("null")
                title.setTextColor(Color.RED)
                return@Observer
            }

            val model = it
            title.setText(model.name)
            subtitle.setText(model.subtitle)
            description.setText(model.description)
        })

        Log.i("ActivityNomEdit", "Create")
    }
}