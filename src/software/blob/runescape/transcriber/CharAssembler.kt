package software.blob.runescape.transcriber

import software.blob.runescape.transcriber.grid.CharGrid
import java.lang.StringBuilder
import java.util.ArrayList

/**
 * Takes characters at a set of positions and assembles them into readable strings
 */
class CharAssembler {

    /**
     * Holds character data at a given position
     * @param char Character grid data
     * @param x X position of the character
     * @param y Y position of the character
     */
    private class CharPos(val char: CharGrid, val x: Int, val y: Int) {

        override fun toString() = "$char [$x, $y]"
    }

    /**
     * The list of characters to assemble together
     */
    private val chars = ArrayList<CharPos>()

    /**
     * Add a character to the assembler
     * @param char Character data to add
     * @param x X position of the character
     * @param y Y position of the character
     */
    fun addChar(char: CharGrid, x: Int, y: Int) {
        chars += CharPos(char, x, y)
    }

    /**
     * Assemble all the characters into lines of text
     * @return Each line of text
     */
    fun assemble(): Array<String> {
        // Sort character positions by y-major x-minor
        chars.sortWith { o1, o2 ->
            val yComp = o1.y.compareTo(o2.y)
            if (yComp == 0) o1.x.compareTo(o2.x) else yComp
        }

        // Convert character positions to strings
        val sb = StringBuilder()
        val lines = ArrayList<String>()
        var lastPos: CharPos? = null
        for (c in chars) {
            if (lastPos != null) {
                if (lastPos.y < c.y) {
                    // Clean up line of text and add it to the list
                    val str = trimGarbage(sb.toString())
                    if (isValid(str)) lines += str
                    sb.clear()
                } else if (c.x - lastPos.x > lastPos.char.width + 1) {
                    // Space or tab
                    sb.append(if (c.x - lastPos.x >= 21) '\t' else ' ')
                }
            }
            sb.append(c.char)
            lastPos = c
        }

        // Last string
        val str = trimGarbage(sb.toString())
        if (isValid(str)) lines += str

        return lines.toTypedArray()
    }

    /**
     * Check if a character is considered valid/readable for transcription purposes
     * @param char Character to check
     */
    private fun isValid(char: Char) = char.isLetterOrDigit()

    /**
     * Check if a string contains valid/readable characters for transcription purposes
     * @param str String to check
     */
    private fun isValid(str: String): Boolean {
        for (c in str) if (isValid(c)) return true
        return false
    }

    /**
     * Trim out garbage from a transcribed string
     * @param input Input string
     * @return Cleaned up string
     */
    private fun trimGarbage(input: String): String {

        // First split up the string into chunks based on tabs
        val chunks = input.split("\t")

        val sb = StringBuilder()
        for (chunk in chunks) {

            // Throw out invalid chunks (chunk doesn't contain any alpha-numeric characters)
            if (!isValid(chunk)) continue

            // Trim out invalid starting characters
            val cleaned = chunk.trimStart('_', '.', '-', '|', ' ')

            // Ignore chunks that are too short
            if (cleaned.length <= 2) continue

            // Ignore the "Username: *" chunk
            if (cleaned.length <= 15 && cleaned.endsWith(": *")) continue

            // Append the tab and cleaned up chunk
            if (sb.isNotEmpty()) sb.append('\t')
            sb.append(cleaned)
        }

        return sb.toString()
    }
}