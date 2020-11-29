package ipvc.estg.tp1.api

data class LoginOutputPost(
    val success: Boolean,
    val name: String,
    val msg : String
)