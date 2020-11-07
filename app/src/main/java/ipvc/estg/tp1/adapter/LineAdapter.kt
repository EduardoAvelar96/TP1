package ipvc.estg.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.tp1.R
import ipvc.estg.tp1.entities.Note


class LineAdapter internal constructor(context: Context, val clickListener: OnNoteItemClickListener) : RecyclerView.Adapter<LineAdapter.NoteViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()

    fun getNoteAt(position: Int): Note=notes.get(position)

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val noteItemView: TextView = itemView.findViewById(R.id.textView)
        val tituloItemView: TextView = itemView.findViewById(R.id.textView2)

        //clickListener
        fun initialize(note: Note, clickListener:OnNoteItemClickListener){
            noteItemView.text = note.note
            tituloItemView.text = note.title

            itemView.setOnClickListener{
                clickListener.onItemClick(note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): NoteViewHolder{
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder,position: Int){
        val current = notes[position]
        holder.noteItemView.text = current.note
        holder.tituloItemView.text = current.title
        holder.initialize(current, clickListener)
    }

    internal fun setNotes(notes: List<Note>){
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size

}

//clickListener
interface OnNoteItemClickListener{
    fun onItemClick(note: Note)
}




