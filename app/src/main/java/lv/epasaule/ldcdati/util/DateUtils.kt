package lv.epasaule.ldcdati.util

import android.content.Context
import android.text.format.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

val String.parseIsoDate: Long? get() =
    try { dateFormat.parse(this) }
    catch (e: ParseException) { null }
            ?.time

val Long.isoDateStr: String get() = dateFormat.format(Date(this))

fun todayLongDateStr(context: Context): String? = Date().time.longDateStr(context)

fun Long?.longDateStr(context: Context) = toBestDateTimeStr(
        context, "dMMMMyyyyHHmm"
)

private val dateFormat: SimpleDateFormat
    get() {
        val pattern = "yyyy-MM-dd'T'HH:mm:ss"
        return SimpleDateFormat(pattern, Locale.getDefault())
    }

fun Long?.toBestDateTimeStr(
        context: Context,
        skeleton: String
): String? = this?.let {
    val newSkeleton = if (!DateFormat.is24HourFormat(context)) {
        skeleton.replace('H', 'h')
    } else skeleton
    val pattern = DateFormat.getBestDateTimePattern(context.primaryLocale(), newSkeleton)
    SimpleDateFormat(pattern, context.primaryLocale()).format(Date(this))
}