import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
    println("Before")
    val user = requestUser()
    println("User: ${user.name}")

    println("After")
}

suspend fun requestUser(): User = suspendCoroutine<User> { cont ->
    getUser { user ->
        cont.resume(user)
    }
}

fun getUser(callback: (User) -> Unit) {
    callback(User("test"))
}


data class User(val name: String)
