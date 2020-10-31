package ipvc.estg.tp1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddNote : AppCompatActivity() {

    private lateinit var editContentView: EditText
    private lateinit var editTitleView: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        editContentView = findViewById(R.id.edit_word)
        editTitleView = findViewById(R.id.edit_word2)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editContentView.text) || TextUtils.isEmpty(editTitleView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                replyIntent.putExtra(EXTRA_REPLY2, editTitleView.text.toString())
                replyIntent.putExtra(EXTRA_REPLY, editContentView.text.toString())
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
        const val EXTRA_REPLY2= "com.example.android.wordlistsql.REPLY2"
    }
}