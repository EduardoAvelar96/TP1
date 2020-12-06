package ipvc.estg.tp1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class RequestType : AppCompatActivity() {


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type)

        val button1 = findViewById<Button>(R.id.save1)
        val button2 = findViewById<Button>(R.id.save2)
        val button3 = findViewById<Button>(R.id.save3)
            button1.setOnClickListener {
                val replyIntent = Intent()
                replyIntent.putExtra(EXTRA_REPLY, 1)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        button2.setOnClickListener {
            val replyIntent = Intent()
            replyIntent.putExtra(EXTRA_REPLY, 2)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
        button3.setOnClickListener {
            val replyIntent = Intent()
            replyIntent.putExtra(EXTRA_REPLY, 3)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
    }
    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}
