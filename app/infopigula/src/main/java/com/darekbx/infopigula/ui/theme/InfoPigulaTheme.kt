package com.darekbx.infopigula.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.common.ui.theme.MontserratFontFamily

@Composable
fun InfoPigulaTheme(isDarkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme =
        if (isDarkTheme)
            darkColorScheme(
                primary = Color.White,
                secondary = Color.White,
                background = Color.Black,
                onBackground = Color.White,
                surface = Color.Black,
                onPrimary = Color.White,
                onSurface = Color.White,
                tertiary = LocalColors.current.darkblue
            )
        else
            lightColorScheme(
                primary = Color.Black,
                secondary = Color.Black,
                background = Color.White,
                onBackground = Color.Black,
                surface = Color.White,
                onPrimary = Color.Black,
                onSurface = Color.Black,
                tertiary = LocalColors.current.lightblue
            ),
        typography = Typography(
            labelSmall = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                letterSpacing = (-0.25).sp
            ),
            labelMedium = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                lineHeight = 14.sp
            ),
            titleMedium = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.5.sp
            ),
            titleLarge = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W500,
                fontSize = 24.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.5.sp
            ),
        ),
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(12.dp)
        ),
        content = content
    )
}

@Preview(showSystemUi = false)
@Composable
private fun TypographyLightPreview() {
    InfoPigulaTheme(isDarkTheme = false) {
        TestContent()
    }
}

@Preview(showSystemUi = false)
@Composable
private fun TypographyDarkPreview() {
    InfoPigulaTheme(isDarkTheme = true) {
        TestContent()
    }
}

@Composable
private fun TestContent() {
    Surface {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "titleMedium",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "labelMedium",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "labelSmall",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                text = "6h",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleSmall
            )

        }
    }
}