package ipvc.estg.tp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.tp1.adapter.LineAdapter
import ipvc.estg.tp1.dataclasses.Place
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //Definir array recyclerview
    private lateinit var myList: ArrayList<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Recyclerview
        myList = ArrayList<Place>()

        for (i in 0 until 50){
            myList.add(Place("Titulo $i", "ID $i"))
        }
        recycler_view.adapter = LineAdapter(myList)
        recycler_view.layoutManager = LinearLayoutManager(this)
    }


    //Ver menu opções
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    //Menu de opções
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.opt1->{
                Toast.makeText(this, "teste", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.opt2->{
                Toast.makeText(this, "teste2", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.opt3->{
                Toast.makeText(this, "teste3", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}