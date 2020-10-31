package ipvc.estg.tp1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ipvc.estg.app.adapters.LineAdapter
import ipvc.estg.tp1.AddNote.Companion.EXTRA_REPLY2
import ipvc.estg.tp1.entities.Note
import ipvc.estg.tp1.viewModel.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //Definir array recyclerview
    private lateinit var noteViewModel: NoteViewModel
    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = LineAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //view Model
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe( this, { notes ->
            notes?.let { adapter.setNotes(it) }
        })

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
                val intent = Intent(this@MainActivity, AddNote::class.java)
                startActivityForResult(intent,newWordActivityRequestCode)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {

            val titulo = data?.getStringExtra(AddNote.EXTRA_REPLY)
            val conteudo = data?.getStringExtra(AddNote.EXTRA_REPLY2)

            if (titulo!= null && conteudo != null) {
                val note = Note(title = titulo, note = conteudo)
                noteViewModel.insert(note)
            }

        } else {
            Toast.makeText(
                applicationContext,
                    "Nota Vazia: Não Inserida",
                Toast.LENGTH_LONG).show()
        }
    }
}
