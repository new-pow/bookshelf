package example2

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

val LoomDispatcher = Executors.newVirtualThreadPerTaskExecutor()
    .asCoroutineDispatcher()
