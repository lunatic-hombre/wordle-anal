fun main() {
    val counts = IntArray(26)

    // count letters and read relevant words to list
    val words = lines("/usr/share/dict/words").map(String::lowercase).map(String::trim).filter { word ->
        word.length == 5 && word.all { it in 'a'..'z' }
    }.onEach { word ->
        for (i in 0..4)
            counts[word[i] - 'a']++
    }.toList()

    // find the best pair of words
    println("calculating with ${words.size} words...")
    val start = now()
    var best = WordScore.empty
    for (i in words.indices)
        for (j in i + 1 until words.size)
            best = best.max(counts.score(words[i], words[j]))

    // print result
    val (word1, word2, score) = best
    println("""
        
        first word:  $word1
        second word: $word2
        score:       $score
        time taken:  ${(now() - start) / 1000}s
        
    """.trimIndent())
}

fun IntArray.score(word1: String, word2: String): WordScore {
    val letters = HashSet<Char>()
    var score = 0
    for (i in 0..4) {
        val letter = word1[i] to word2[i]
        if (!letters.add(letter.first) || !letters.add(letter.second))
            return WordScore.empty
        score += this[letter.first - 'a'] + this[letter.second - 'a']
    }
    return WordScore(word1, word2, score)
}

data class WordScore(val word1: String, val word2: String, val score: Int) {
    companion object {
        val empty = WordScore("", "", 0)
    }

    fun max(other: WordScore) = if (other.score > score) other else this
}