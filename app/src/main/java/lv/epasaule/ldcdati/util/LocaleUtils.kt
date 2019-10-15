package lv.epasaule.ldcdati.util

import android.content.Context
import androidx.core.os.ConfigurationCompat
import java.util.*


fun Context.primaryLocale(): Locale =
        ConfigurationCompat.getLocales(resources.configuration).get(0)