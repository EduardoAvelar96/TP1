package ipvc.estg.tp1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EditNote : AppCompatActivity() {

    private lateinit var editContentView: EditText
    private lateinit var editTitleView: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        editContentView = findViewById(R.id.edit_word)
        editTitleView = findViewById(R.id.edit_word2)

        val intent = intent
        editContentView.setText( intent.getStringExtra(EXTRA_REPLY) )
        editTitleView.setText( intent.getStringExtra(EXTRA_REPLY2) )

        val id = intent.getIntExtra( EXTRA_ID , -1)
        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()

            val titulo = editTitleView.text.toString()
            val conteudo = editContentView.text.toString()
            if( id != -1 ) {
                replyIntent.putExtra(EXTRA_ID, id)
            }
            replyIntent.putExtra(EXTRA_REPLY2, titulo)
            replyIntent.putExtra(EXTRA_REPLY, conteudo)
            setResult(Activity.RESULT_OK, replyIntent)

            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
        const val EXTRA_REPLY2 = "com.example.android.wordlistsql.REPLY2"
        const val EXTRA_ID = "com.example.android.wordlistsql.ID"
    }
}