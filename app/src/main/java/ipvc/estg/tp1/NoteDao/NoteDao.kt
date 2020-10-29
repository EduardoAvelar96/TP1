package ipvc.estg.tp1.NoteDao

import android.icu.text.CaseMap
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ipvc.estg.tp1.entities.Note


@Dao
interface NoteDao {

    @Query("Select * from note_table ORDER BY Title ASC")
    fun getAlphabetizedNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(Title: Note)

    @Query("DELETE FROM note_table")
    suspend fun deleteAll()
}