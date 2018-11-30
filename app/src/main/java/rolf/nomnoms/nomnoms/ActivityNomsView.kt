package rolf.nomnoms.nomnoms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import rolf.nomnoms.nomnoms.dataaccess.DataAccess

class ActivityNomsView : AppCompatActivity() {

    private var nomId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms_view)

        nomId = intent.getLongExtra("nom_id", -1L)

        if( nomId == -1L ){
            finish()
            return
        }

        val model = DataAccess(this).getNomById(nomId as Long)

        if( model == null ){
            finish();
            return;
        }

        findViewById<TextView>(R.id.textview_name).text = model.name
        findViewById<TextView>(R.id.textview_subtitle_nom).text = model.subtitle
        findViewById<TextView>(R.id.textview_description_noms).text = model.description

        //get images
        //get events

    }
}
