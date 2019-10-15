package lv.epasaule.ldcdati.util

import rx.Observable

val <T> T?.opt: Opt<T?> get() = Opt.optOf(this)

class Opt<T> private constructor(val value: T?) {

    companion object Factory {
        private val EMPTY: Opt<*> = Opt(null)

        @Suppress("UNCHECKED_CAST")
        fun <T> empty(): Opt<T?> = EMPTY as Opt<T?>

        fun <T> optOf(value: T?): Opt<T?> = if (value == null) empty() else Opt(value)
    }

    val isPresent: Boolean
        get() = value != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Opt<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Opt(value=$value)"
    }

}

val <T> Observable<Opt<T?>>.pickValue: Observable<T>
    get() = filter { it.isPresent }.map { it.value }