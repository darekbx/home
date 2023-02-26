package com.darekbx.hejto.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.hejto.utils.LinkParser
import kotlin.math.min

/**
 *
 * 1. Parse links
 * val links = LinkParser.extractLinks(content)
 *
 * 2. Remove links from content
 * var result = content
 * links.forEach { link -> result = result.replace(link.source, link.label) }
 *
 * @param content Text without links
 * @param links parsed links
 *
 */
@Composable
fun LinkedText(
    content: String,
    links: List<LinkParser.Link>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current
) {
    val localUriHandler = LocalUriHandler.current
    val linkStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.W400
    )
    val annotatedString = buildAnnotatedString {
        append(content)
        var offset = 0
        links.forEach { link ->
                val start = link.start - offset
                val end = min(content.length,  start + link.label.length)
                addStyle(style = linkStyle, start = start, end = end)
                addStringAnnotation(tag = "tag", annotation = link.url, start = start, end = end)
                offset += link.url.length + 4
        }
    }

    ClickableText(
        modifier = modifier,
        text = annotatedString,
        style = style,
        color = color,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                start = offset,
                end = offset,
            ).firstOrNull()?.let { result ->
                localUriHandler.openUri(result.item)
            }
        }
    )
}

/**
 * Modified version from Compose SDK
 */
@Composable
fun ClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onClick: (Int) -> Unit
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                onClick(layoutResult.getOffsetForPosition(pos))
            }
        }
    }

    Text(
        text = text,
        modifier = modifier.then(pressIndicator),
        style = style,
        color = color,
        onTextLayout = { layoutResult.value = it }
    )
}

@Preview
@Composable
fun LinkedTextPreview() {
    HomeTheme {
        val content = "Tego dnia w Rzymie\n\nTego dnia, 41 n.e. – [Klaudiusz](https://imperiumromanum.pl/biografie/klaudiusz/) został cesarzem rzymskim. Po zamordowaniu [Kaliguli](https://imperiumromanum.pl/biografie/cesarz-kaligula/) w powstałym zamieszaniu część żołnierzy gwardii pretoriańskiej zdecydowała się na obwołanie cesarzem Klaudiusza, Ponadto był ostatnim znanym człowiekiem władającym językiem etruskim. [#liganauki](/tag/liganauki)\n#antycznyrzym #imperiumromanum #tegodniawrzymie #wydarzenia #gruparatowaniapoziomu #historia #tegodnia #tegodniawrzymie #ancientrome #rzym #ciekawostki #venividivici #liganauki"

        val links = LinkParser.extractLinks(content)
        var result = content
        links.forEach { link -> result = result.replace(link.source, link.label) }
        LinkedText(
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp),
            content = result,
            links = links,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
