package ipvc.estg.tp1.api

data class LoginOutputPost(
    val success: Boolean,
    val data: data,
    val msg : String
)

data class data(
    val ID: Int,
    val username: String,
    val password: String
)
