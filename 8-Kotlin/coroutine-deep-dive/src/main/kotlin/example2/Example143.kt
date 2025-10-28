package example2

import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext


// pg.143
suspend fun main() {
    val analyticsScope = CoroutineScope(SupervisorJob())
    val usecase = ShowUserDataUseCase(UserDataRepository, UserDataView, analyticsScope)
    usecase.showUserData()
}


class ShowUserDataUseCase(
    private val repo: UserDataRepository,
    private val view: UserDataView,
    private val analyticsScope: CoroutineScope,
) {
    suspend fun showUserData() = coroutineScope {
        val name = async { repo.getName() }
        val friends = async { repo.getFriends() }
        val profile = async { repo.getProfile() }

        val user = User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await(),
        )
        view.show(user)

        val job = analyticsScope.launch { repo.notifyProfileShown() }
        job.join() // join 을 안하면 안되지 않나?? 예시랑은 다른부분.
    }
}


object UserDataRepository {
    fun getName() = "test"
    fun getFriends() = 4
    fun getProfile() = "test-profile"
    suspend fun notifyProfileShown() {
        delay(1000)
        println("profile shown")
    }
}

object UserDataView {
    fun show(user: User) {
        println("user ${user.name}, ${user.friends}, ${user.profile}")
    }
}

class User(
    val name: String,
    val friends: Int,
    val profile: String,
)
