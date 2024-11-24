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
fun MarkdownEditText() {
    var rawText by remember { mutableStateOf("") }
    var annotatedText by remember { mutableStateOf(AnnotatedString("")) }

    val parser = remember { org.commonmark.parser.Parser.builder().build() }

    BasicTextField(
        value = rawText,
        onValueChange = { newText ->
            // 更新用户输入
//            val result = handleMarkdownInput(newText)
            rawText = newText

            // 解析 Markdown 并更新富文本
            annotatedText = parseMarkdownToAnnotatedString(parser, rawText)
        },
        textStyle = TextStyle.Default.copy(color = Color.Black),
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
        visualTransformation = MarkdownVisualTransformation(annotatedText),
        cursorBrush = SolidColor(Color.Blue), // 自定义光标颜色
        modifier = Modifier.fillMaxWidth()
    )
}


class MarkdownVisualTransformation(
    private val annotatedText: AnnotatedString
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            annotatedText,
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


fun parseMarkdownToAnnotatedString(
    parser: org.commonmark.parser.Parser,
    rawText: String
): AnnotatedString {
    val document = parser.parse(rawText)
    // swithunlog("document", document)
//    val final = getTrailingWhitespace(rawText)

    val result = buildAnnotatedString {
        appendMarkdownNodes(document)
//        append(final)
    }
    return result
}

fun swithunlog(message: String , any: Any) {
    Log.d("swithun-xxxx", "$message ${GsonBuilder().setPrettyPrinting().create().toJson(any)}")
}

fun getTrailingWhitespace(input: String): String {
    // 匹配字符串末尾的所有空白字符
    val regex = "\\s*$".toRegex()
    return regex.find(input)?.value ?: ""
}

fun AnnotatedString.Builder.appendMarkdownNodes(node: org.commonmark.node.Node) {
    var currentNode = node.firstChild
    while (currentNode != null) {
        when (currentNode) {
            is org.commonmark.node.Text -> {
               append(currentNode.literal)
            }
            is org.commonmark.node.StrongEmphasis -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                appendMarkdownNodes(currentNode)
                pop()
            }
            is org.commonmark.node.Paragraph -> {
                appendMarkdownNodes(currentNode)
            }
            is org.commonmark.node.Emphasis -> {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                appendMarkdownNodes(currentNode)
                pop()
            }
            is org.commonmark.node.BulletList -> {
                currentNode.firstChild?.let {
                    this.append("• ")
                    appendMarkdownNodes(it)
                }
            }
            is org.commonmark.node.OrderedList -> {
                var index = 1
                currentNode.firstChild?.let {
                    append("$index. ")
                    appendMarkdownNodes(it)
                    index++
                }
            }
            is org.commonmark.node.Heading -> {
                pushStyle(
                    SpanStyle(
                        fontSize = when (currentNode.level) {
                            1 -> 24.sp
                            2 -> 20.sp
                            else -> 18.sp
                        }
                    )
                )
                appendMarkdownNodes(currentNode)
                pop()
            }
            is org.commonmark.node.Code -> {
                pushStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = Color.LightGray
                    )
                )
                append(currentNode.literal)
                pop()
            }
        }
        currentNode = currentNode.next
    }
}


data class MarkdownInputResult(val text: String)

fun handleMarkdownInput(input: String): MarkdownInputResult {
    // 无序列表：检测 `- ` 的输入
    if (input.endsWith("- ")) {
        val updatedText = input.trimEnd() + "\n• "
        return MarkdownInputResult(updatedText)
    }

    // 有序列表：检测 `1.` 的输入
    if (input.matches(Regex("\\d+\\.\\s\$"))) {
        val number = input.substringBefore(".").toIntOrNull() ?: 1
        val updatedText = input.trimEnd() + "\n${number + 1}. "
        return MarkdownInputResult(updatedText)
    }

    return MarkdownInputResult(input)
}

