package rolf.nomnoms.nomnoms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.ModelNoms

class ActivityNomsAdd : AppCompatActivity() {

    var nomName : TextView? = null
    var nomSubtitle : TextView? = null
    var nomDescription : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms_add)

        nomName = findViewById(R.id.edittext_name)
        nomSubtitle = findViewById(R.id.edittext_subtitle)
        nomDescription = findViewById(R.id.edittext_description)
        findViewById<Button>(R.id.button_abort).setOnClickListener {finish()}

        findViewById<Button>(R.id.button_save_and_add_more).setOnClickListener {
            saveNomToDb()
            clearForm()
        }
    }

    fun getNom() : ModelNoms {
        return ModelNoms(
            -1,
            nomName!!.text.toString(),
            nomSubtitle!!.text.toString(),
            nomDescription!!.text.toString(),
            0
        )
    }

    private fun clearForm(){
        nomName!!.text = ""
        nomSubtitle!!.text = ""
        nomDescription!!.text = ""
    }

    private fun saveNomToDb(){
        val nom = getNom()
        Log.i("ActivityNomsAdd", "Saving '${nom.name}'")
        DataAccess(this).insertNoms(nom)

        Toast.makeText(this, "Saved ${nom.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if( nomName?.text?.equals("") == true ){
            super.onBackPressed()
        }

        if( !nomName!!.text.equals("") )
            saveNomToDb()
        finish();
    }
}
