package rolf.nomnoms.nomnoms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_noms_add.*
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.ModelNoms

class ActivityNomsAdd : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms_add)

        button_edit.setOnClickListener { finish() }
        button_save_and_add_more.setOnClickListener(this::saveNomAndAddMore)
        button_add.setOnClickListener {
            saveNomToDb()
            finish()
        }
    }

    private fun saveNomAndAddMore(view: View){
        saveNomToDb()
        clearForm()
    }

    private fun getNom() : ModelNoms {
        return ModelNoms(
            -1,
            textview_name.text.toString(),
            textview_subtitle_nom.text.toString(),
            textview_description_noms.text.toString(),
            0,
            -1
        )
    }

    private fun clearForm(){
        textview_name.setText("")
        textview_subtitle_nom.setText("")
        textview_description_noms.setText("")
    }

    private fun saveNomToDb(){
        val nom = getNom()
        Log.i("ActivityNomsAdd", "Saving '${nom.name}'")
        DataAccess(this).insertNoms(nom)

        Toast.makeText(this, "Saved ${nom.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if(textview_name.text.isBlank()) {
            super.onBackPressed()
            return
        }

        saveNomToDb()
        finish()
    }
}
