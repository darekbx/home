package com.darekbx.stocks.widget

import android.content.Context
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.*
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.darekbx.stocks.R

/**
 * Widget is based on
 * https://github.com/android/user-interface-samples/blob/main/AppWidget/app/src/main/java/com/example/android/appwidget/glance/weather"
 */
class StocksWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override val stateDefinition = StocksInfoStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            when (val stocksInfo = currentState<StocksInfo>()) {
                StocksInfo.Loading -> Loading()
                is StocksInfo.Unavailable -> Unavailable()
                is StocksInfo.Available -> StockContent(stocksInfo)
            }
        }
    }
}

@Composable
private fun Unavailable() {
    Box(
        modifier = appWidgetBackgroundModifier(),
        contentAlignment = Alignment.Center
    ) {
        Text("Data not available")
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = appWidgetBackgroundModifier(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun StockContent(info: StocksInfo.Available) {
    Column(
        modifier = appWidgetBackgroundModifier()
            .clickable(actionRunCallback<UpdateStocksAction>()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = LocalContext.current.getString(R.string.pln_usd),
            style = fromCompose(
                MaterialTheme.typography.labelSmall,
                ColorProvider(MaterialTheme.colorScheme.secondary)
            )
        )
        Text(
            text = "$%.2f".format(info.plnUsd),
            style = fromCompose(
                MaterialTheme.typography.bodyLarge,
                ColorProvider(MaterialTheme.colorScheme.primary),
                FontWeight.Bold
            )
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(
            text = LocalContext.current.getString(R.string.pln_eur),
            style = fromCompose(
                MaterialTheme.typography.labelSmall,
                ColorProvider(MaterialTheme.colorScheme.secondary)
            )
        )
        Text(
            text = "€%.2f".format(info.plnEur),
            style = fromCompose(
                MaterialTheme.typography.bodyLarge,
                ColorProvider(MaterialTheme.colorScheme.primary),
                FontWeight.Bold
            )
        )
    }
}

private fun fromCompose(
    textStyle: androidx.compose.ui.text.TextStyle,
    color: ColorProvider,
    weight: FontWeight? = null
) = TextStyle(
    color = color,
    fontStyle = when (textStyle.fontStyle ?: androidx.compose.ui.text.font.FontStyle.Normal) {
        androidx.compose.ui.text.font.FontStyle.Italic -> FontStyle.Italic
        else -> FontStyle.Normal
    },
    fontWeight = weight,
    fontSize = textStyle.fontSize
)

@Composable
private fun appWidgetBackgroundModifier() = GlanceModifier
    .fillMaxSize()
    .padding(8.dp)
    .appWidgetBackground()
    .background(MaterialTheme.colorScheme.background)
    .appWidgetInnerCornerRadius()

private fun GlanceModifier.appWidgetInnerCornerRadius(): GlanceModifier {
    if (Build.VERSION.SDK_INT >= 31) {
        cornerRadius(android.R.dimen.system_app_widget_inner_radius)
    } else {
        cornerRadius(8.dp)
    }
    return this
}

class UpdateStocksAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        StocksWorker.enqueue(context = context, force = true)
    }
}
