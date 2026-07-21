package com.example.healthtracker.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.core.content.ContextCompat
import com.example.healthtracker.R
import java.time.LocalDate
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Dp
import com.example.healthtracker.MainActivity

data class WidgetSizes(
    val minWidthMedium: Dp,
    val minHeightLarge: Dp,
    val textSmall: TextUnit,
    val textLarge: TextUnit,
    val textXLarge: TextUnit,
    val textXXLarge: TextUnit
)

class CalorieWidget : GlanceAppWidget() {

    companion object {
        val TARGET_KEY = intPreferencesKey("targetCalories")
        val CONSUMED_KEY = intPreferencesKey("consumedCalories")
        val BURNED_KEY = intPreferencesKey("burnedCalories")
        val TDEE_KEY = intPreferencesKey("tdee")
        val DATE_KEY = androidx.datastore.preferences.core.stringPreferencesKey("savedDate")
    }

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(110.dp, 110.dp), // 2x2 (Small)
            DpSize(250.dp, 110.dp), // 4x2 (Wide)
            DpSize(250.dp, 250.dp)  // 4x4 (Large)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        fun getDp(resId: Int): Dp = (context.resources.getDimension(resId) / context.resources.displayMetrics.density).dp
        fun getSp(resId: Int): TextUnit = (context.resources.getDimension(resId) / context.resources.displayMetrics.scaledDensity).sp

        val sizes = WidgetSizes(
            minWidthMedium = getDp(R.dimen.widget_breakpoint_width_medium),
            minHeightLarge = getDp(R.dimen.widget_breakpoint_height_large),
            textSmall = getSp(R.dimen.widget_text_small),
            textLarge = getSp(R.dimen.widget_text_large),
            textXLarge = getSp(R.dimen.widget_text_xlarge),
            textXXLarge = getSp(R.dimen.widget_text_xxlarge)
        )

        provideContent {
            val prefs = currentState<Preferences>()
            val savedDateStr = prefs[DATE_KEY]
            val todayStr = LocalDate.now().toString()

            val target = prefs[TARGET_KEY] ?: 2000
            val tdee = prefs[TDEE_KEY] ?: 0

            // If the saved date is not today, display 0 for consumed and burned
            val isToday = savedDateStr == todayStr
            val consumed = if (isToday) prefs[CONSUMED_KEY] ?: 0 else 0
            val burned = if (isToday) prefs[BURNED_KEY] ?: 0 else 0

            WidgetContent(target, consumed, burned, tdee, sizes)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun WidgetContent(target: Int, consumed: Int, burned: Int, tdee: Int, sizes: WidgetSizes) {
        val context = LocalContext.current
        val size = LocalSize.current
        
        val netCalories = consumed - burned
        val progress = if (target > 0) kotlin.math.min(1f, netCalories.toFloat() / target.toFloat()).coerceAtLeast(0f) else 0f

        val isSmall = size.width < sizes.minWidthMedium
        val isLarge = size.width >= sizes.minWidthMedium && size.height >= sizes.minHeightLarge

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(R.color.widget_background))
                .padding(R.dimen.widget_padding)
                .cornerRadius(R.dimen.widget_corner_radius)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSmall) {
                // Layout 2x2
                Box(contentAlignment = Alignment.Center, modifier = GlanceModifier.fillMaxSize()) {
                    Image(
                        provider = ImageProvider(createCircularProgressBitmap(context, progress, 200)),
                        contentDescription = "Progress",
                        modifier = GlanceModifier.size(R.dimen.widget_progress_size_small)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = netCalories.toString(),
                            style = TextStyle(color = ColorProvider(R.color.white), fontWeight = FontWeight.Bold, fontSize = sizes.textXLarge)
                        )
                        Text(
                            text = "/ $target kcal",
                            style = TextStyle(color = ColorProvider(R.color.widget_text_secondary), fontSize = sizes.textSmall)
                        )
                    }
                }
            } else {
                // Layout 4x2 (Medium) and 4x4 (Large)
                Row(modifier = GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            provider = ImageProvider(createCircularProgressBitmap(context, progress, 240)),
                            contentDescription = "Progress",
                            modifier = GlanceModifier.size(R.dimen.widget_progress_size_large)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = netCalories.toString(),
                                style = TextStyle(color = ColorProvider(R.color.white), fontWeight = FontWeight.Bold, fontSize = sizes.textXXLarge)
                            )
                            Text(
                                text = "/ $target kcal",
                                style = TextStyle(color = ColorProvider(R.color.widget_text_secondary), fontSize = sizes.textSmall)
                            )
                        }
                    }

                    Spacer(modifier = GlanceModifier.width(R.dimen.widget_spacing_large))

                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = context.getString(R.string.dashboard_consumed),
                            style = TextStyle(color = ColorProvider(R.color.widget_text_secondary), fontSize = sizes.textSmall)
                        )
                        Text(
                            text = "$consumed",
                            style = TextStyle(color = ColorProvider(R.color.widget_consumed), fontWeight = FontWeight.Bold, fontSize = sizes.textLarge)
                        )

                        Spacer(modifier = GlanceModifier.height(R.dimen.widget_spacing_small))

                        Text(
                            text = context.getString(R.string.dashboard_burned),
                            style = TextStyle(color = ColorProvider(R.color.widget_text_secondary), fontSize = sizes.textSmall)
                        )
                        Text(
                            text = "$burned",
                            style = TextStyle(color = ColorProvider(R.color.widget_burned), fontWeight = FontWeight.Bold, fontSize = sizes.textLarge)
                        )

                        if (isLarge) {
                            Spacer(modifier = GlanceModifier.height(R.dimen.widget_spacing_small))
                            
                            Text(
                                text = context.getString(R.string.dash_label_tdee),
                                style = TextStyle(color = ColorProvider(R.color.widget_text_secondary), fontSize = sizes.textSmall)
                            )
                            Text(
                                text = tdee.toString(),
                                style = TextStyle(color = ColorProvider(R.color.widget_accent), fontWeight = FontWeight.Bold, fontSize = sizes.textLarge)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createCircularProgressBitmap(context: Context, progress: Float, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val strokeWidth = size * 0.1f

        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        val offset = strokeWidth / 2f
        val rect = RectF(offset, offset, size - offset, size - offset)

       
        paint.color = ContextCompat.getColor(context, R.color.widget_track)
        canvas.drawArc(rect, 0f, 360f, false, paint)

        paint.color = ContextCompat.getColor(context, R.color.widget_accent)
        canvas.drawArc(rect, -90f, 360f * progress, false, paint)

        return bitmap
    }
}
