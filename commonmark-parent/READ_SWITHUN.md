```kotlinh
        private fun isSpaceTabOrEnd(line: CharSequence, index: Int): Boolean {
            return if (index < line.length) {
                when (line[index]) {
                    ' ', '\t' -> true
                    else -> false
                }
            } else {
                false // 应该是false
            }
        }
```
