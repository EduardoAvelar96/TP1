package ipvc.estg.tp1.NoteDao

import androidx.lifecycle.LiveData
import androidx.room.*
import ipvc.estg.tp1.entities.Note


@Dao
interface NoteDao {

    @Query("Select * from note_table ORDER BY id ASC")
    fun getAlphabetizedNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(Title: Note)

    @Query("DELETE FROM note_table")
    suspend fun deleteAll()

}