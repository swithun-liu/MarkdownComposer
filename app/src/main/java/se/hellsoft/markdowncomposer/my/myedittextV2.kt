package se.hellsoft.markdowncomposer.my

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.GsonBuilder
import se.hellsoft.markdowncomposer.MIXED_MD


@Composable
fun MarkdownEditTextV2() {
    var rawText by remember { mutableStateOf("") }
    var annotatedText by remember { mutableStateOf(AnnotatedString("")) }

    BasicTextField(
        value = rawText,
        onValueChange = { newText ->
            rawText = newText
            annotatedText = parseLine(rawText)
        },
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                if (rawText.isEmpty()) {
                    Text(
                        text = "输入 Markdown 内容...",
                        style = TextStyle(color = Color.Gray)
                    )
                }
                innerTextField()
            }

        },
        visualTransformation = SwithunMarkdownVisualTransformation(annotatedText),
        modifier = Modifier.fillMaxWidth()
    )
}

class SwithunMarkdownVisualTransformation(
    private val annotatedText: AnnotatedString
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(annotatedText,
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return offset.coerceIn(0, annotatedText.length)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return offset.coerceIn(0, text.length)
                }
            }
        )
    }
}

fun parseLine(line: String): AnnotatedString {
    var newLine = line
    if (line.startsWith("- ")) {
        newLine = line.replaceFirst("- ", "• ")
    }
    val result = buildAnnotatedString { append(newLine) }
    return result
}
