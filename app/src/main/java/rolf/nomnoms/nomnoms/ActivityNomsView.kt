package rolf.nomnoms.nomnoms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import rolf.nomnoms.nomnoms.adapter.AdapterNomEvents
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

        val model = DataAccess(this).getNomById(nomId)

        if( model == null ){
            finish();
            return;
        }

        findViewById<TextView>(R.id.textview_name).text = model.name
        findViewById<TextView>(R.id.textview_subtitle_nom).text = model.subtitle
        findViewById<TextView>(R.id.textview_description_noms).text = model.description
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_events)

        val nomEvents = DataAccess(this).getEventsByNomId(nomId)
        recyclerView.adapter = AdapterNomEvents(this, nomEvents.reversed() )
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        //get images
    }
}
