package example3


val f = flow {
    emit("A")
    throw IllegalStateException("1st error")
}

fun main() {

repeat(3) {
    f.catch { e -> println("Catch[$it]: $e") }
        .collect { println("Collect[$it]: $it") }
}
}

