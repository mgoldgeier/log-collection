package mgoldgeier
import io.micronaut.runtime.Micronaut.*

fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("mgoldgeier")
        .start()
}
