package com.yuanm.funneytest.utils

object PermissionUtil {


    /**
     * 给你两个字符串 word1 和 word2 。请你从 word1 开始，通过交替添加字母来合并字符串。
     * 如果一个字符串比另一个字符串长，就将多出来的字母追加到合并后字符串的末尾。
     * 返回合并后的字符串。
     */
    fun mergeAlternately(word1: String, word2: String): String {
        val shortSize = word1.length.coerceAtMost(word2.length)
        var result = ""
        for (i in 0 until shortSize) {
            result = result + word1[i] + word2[i]
            while (i == shortSize - 1) {
                result += if (i == word1.length - 1) {
                    word2.subSequence(i+1, word2.length)
                } else {
                    word1.subSequence(i+1, word1.length)
                }
                break
            }
        }
        return result
    }


}