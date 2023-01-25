package com.darekbx.hejto.ui

import androidx.compose.foundation.background
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
fun HejtoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(73, 147, 236),
            background = Color.Black,
            onBackground = Color(24, 24, 24),
            surface = Color(15, 25, 29),
            primaryContainer = Color(20, 30, 34),
            secondaryContainer = Color(19, 31, 37),
            onPrimary = Color(239, 239, 239),
            onSurface = Color(189, 189, 189)
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
            titleSmall = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W700,
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
            headlineMedium = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.5.sp
            )
        ),
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(12.dp)
        ),
        content = content
    )
}

@Preview(showSystemUi = true)
@Composable
private fun TypographyPreview() {
    HejtoTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onBackground)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "titleMedium",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "labelMedium",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "labelSmall",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Text(
                        text = "6h",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )

                }
            }
        }
    }
}
