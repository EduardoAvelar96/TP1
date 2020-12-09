package ipvc.estg.tp1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.app.adapters.LineAdapter
import ipvc.estg.app.adapters.OnNoteItemClickListener
import ipvc.estg.tp1.entities.Note
import ipvc.estg.tp1.viewModel.NoteViewModel

class NotesActivity : AppCompatActivity(), OnNoteItemClickListener {



    //Definir array recyclerview
    private lateinit var noteViewModel: NoteViewModel

    private val newWordActivityRequestCode = 1
    private val EditActivityRequestCode = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notes_activity)

        //recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = LineAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        //view Model
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe( this, { notes ->
            notes?.let { adapter.setNotes(it) }
        })

        //SWIPE TO DELETE
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteViewModel.deleteNote(adapter.getNoteAt(viewHolder.layoutPosition))
                Toast.makeText(applicationContext,R.string.NoteDel, Toast.LENGTH_LONG).show()
            }
        }).attachToRecyclerView(recyclerView)

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
                val intent = Intent(this@NotesActivity, AddNote::class.java)
                startActivityForResult(intent,newWordActivityRequestCode)
                true
            }
            R.id.opt2->{
                noteViewModel.deleteAll()
                Toast.makeText(applicationContext,R.string.AllDel, Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //ADICIONAR NOTA
        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val titulo = data?.getStringExtra(AddNote.EXTRA_REPLY)
            val conteudo = data?.getStringExtra(AddNote.EXTRA_REPLY2)

            if (titulo != null && conteudo != null) {
                val note = Note(title = titulo, note = conteudo)
                noteViewModel.insert(note)
            }
        } else if(requestCode == newWordActivityRequestCode){
            Toast.makeText(
                    applicationContext,
                R.string.Empty,
                    Toast.LENGTH_LONG).show()
        }

        //EDITAR NOTA
        if (requestCode == EditActivityRequestCode && resultCode == RESULT_OK) {
            val id = data?.getIntExtra( EditNote.EXTRA_ID, -1 )

            val titulo = data?.getStringExtra( EditNote.EXTRA_REPLY ).toString()
            val conteudo = data?.getStringExtra( EditNote.EXTRA_REPLY2 ).toString()
            val note = Note(id,titulo,conteudo)

            noteViewModel.editNote(note)
            Toast.makeText(applicationContext,R.string.NoteEdit, Toast.LENGTH_LONG).show()
        }
        else if(requestCode == EditActivityRequestCode) {
            Toast.makeText(applicationContext,R.string.Empty, Toast.LENGTH_LONG).show()
        }
    }

    //clickListener
    override fun onItemClick(note: Note) {
        val intent = Intent( this, EditNote::class.java)
        intent.putExtra(EditNote.EXTRA_ID, note.id)
        intent.putExtra(EditNote.EXTRA_REPLY, note.title)
        intent.putExtra(EditNote.EXTRA_REPLY2, note.note)
        startActivityForResult(intent, EditActivityRequestCode)
    }
}