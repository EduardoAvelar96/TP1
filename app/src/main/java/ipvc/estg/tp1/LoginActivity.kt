package ipvc.estg.tp1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import ipvc.estg.tp1.api.LoginEndPoints
import ipvc.estg.tp1.api.LoginOutputPost
import ipvc.estg.tp1.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var userEditTextView: EditText
    private lateinit var passEditTextView: EditText
    private lateinit var login_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userEditTextView = findViewById(R.id.edit_user)
        passEditTextView = findViewById(R.id.edit_pass)
        login_button = findViewById(R.id.buttonLogin)


        //Chama o sharedPref
        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE )

        //Verifica se o utilizador ainda está logado
        val automatic_login = sharedPref.getBoolean(getString(R.string.automatic_login), false)

        if( automatic_login ) {
            val intent = Intent(this@LoginActivity, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun login(view: View) {

        val username = userEditTextView.text.toString()
        val password = passEditTextView.text.toString()

        //ENCRIPTAR A PASSWORD
        val secretKey: String = "662ede816988e58fb6d057d9d85605e0"
        var encryptor = AESencrypt()
        val encryptedValue: String? = encryptor.encrypt(password, secretKey)

        //Verifica se as caixas de texto têm conteudo
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, R.string.User_miss, Toast.LENGTH_LONG).show()
            return
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.Pass_miss, Toast.LENGTH_LONG).show()
            return
        } else {
            //fazer a ligaçao
            val request = ServiceBuilder.buildService(LoginEndPoints::class.java)
            val call = request.login(username, encryptedValue)
            call.enqueue(object : Callback<LoginOutputPost> {
                override fun onResponse(call: Call<LoginOutputPost>, response: Response<LoginOutputPost>) {

                    //Se a ligação ocorrer sem erros
                    if(response.isSuccessful) {
                        val p: LoginOutputPost = response.body()!!

                        //Se o valor sucess retornado for true
                        if (p.success) {
                            Toast.makeText(this@LoginActivity, R.string.Login, Toast.LENGTH_SHORT).show()

                        //Envia os valores para a shared preferences para ocorrer o login automático
                        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE )
                        with ( sharedPref.edit() ) {
                            putBoolean(getString(R.string.automatic_login), true)
                            putString(getString(R.string.username_login), username )
                            putInt(getString(R.string.id_login), p.data.ID)
                            commit()
                        }

                            //Inicia a atividade dos mapas/main activity
                            val intent = Intent(this@LoginActivity, MapsActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            //Campo sucesso retornou com valor false
                            Toast.makeText(this@LoginActivity, R.string.Login_miss, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginOutputPost>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    fun notes(view: View) {
        val intent = Intent(this@LoginActivity, NotesActivity::class.java).apply {
        }
        startActivity(intent)
    }
}
