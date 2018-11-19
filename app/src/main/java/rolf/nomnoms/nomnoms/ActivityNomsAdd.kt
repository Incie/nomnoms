package rolf.nomnoms.nomnoms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import rolf.nomnoms.nomnoms.dataaccess.DataAccess

class ActivityNomsAdd : AppCompatActivity() {

    var nomName : TextView? = null
    var nomSubtitle : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms_add)

        nomName = findViewById(R.id.edittext_name)
        nomSubtitle = findViewById(R.id.edittext_subtitle)
        findViewById<Button>(R.id.button_abort).setOnClickListener {finish()}

        findViewById<Button>(R.id.button_save_and_add_more).setOnClickListener {
            saveNomToDb()
            clearForm()
        }
    }

    fun getNom() : ModelNoms {
        return ModelNoms(-1,
            nomName!!.text.toString(),
            nomSubtitle!!.text.toString() )
    }

    private fun clearForm(){
        nomName!!.text = ""
    }

    private fun saveNomToDb(){
        val nom = getNom()
        Log.i("ActivityNomsAdd", "Saving '${nom.name}'")
        DataAccess(this).insert(nom)

        Toast.makeText(this, "Saved ${nom.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if( nomName?.text?.equals("") == true ){
            super.onBackPressed()
        }

        saveNomToDb()
        finish();
    }
}
