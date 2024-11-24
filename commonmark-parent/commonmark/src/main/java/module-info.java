module org.commonmark {
    requires kotlin.stdlib; // 添加对 Kotlin 标准库的0----


    exports org.commonmark;
    exports org.commonmark.node;
    exports org.commonmark.parser;
    exports org.commonmark.parser.beta;
    exports org.commonmark.parser.block;
    exports org.commonmark.parser.delimiter;
    exports org.commonmark.renderer;
    exports org.commonmark.renderer.html;
    exports org.commonmark.renderer.markdown;
    exports org.commonmark.renderer.text;
    exports org.commonmark.text;
}
