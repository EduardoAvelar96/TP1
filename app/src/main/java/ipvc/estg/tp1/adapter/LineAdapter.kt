package ipvc.estg.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.tp1.R
import ipvc.estg.tp1.entities.Note


class LineAdapter internal constructor(context: Context, var clickListener: OnNoteItemClickListener) : RecyclerView.Adapter<LineAdapter.NoteViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val noteItemView: TextView = itemView.findViewById(R.id.textView)
        val tituloItemView: TextView = itemView.findViewById(R.id.textView2)

        //clickListener
        fun initialize(notes: Note, action:OnNoteItemClickListener){
            noteItemView.text = notes.note
            tituloItemView.text = notes.title

            itemView.setOnClickListener{
                action.onItemClick(notes,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): NoteViewHolder{
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder,position: Int){
    //    val current = notes[position]
    //    holder.noteItemView.text = current.note
    //    holder.tituloItemView.text = current.title
        holder.initialize(notes[position],clickListener)
    }

    internal fun setNotes(notes: List<Note>){
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size

}

//clickListener
interface OnNoteItemClickListener{
    fun onItemClick(notes: Note, position: Int)
}




