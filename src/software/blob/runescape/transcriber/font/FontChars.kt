package software.blob.runescape.transcriber.font

import software.blob.runescape.transcriber.grid.CharGrid
import sun.font.FontDesignMetrics
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.util.ArrayList

/**
 * Load characters from a font to bitmaps
 * @param name Font name (including extension)
 * @param size Font size
 */
class FontChars(name: String, size: Float) : Iterable<CharGrid> {

    // Character data list
    private val chars = ArrayList<CharGrid>(128)

    init {

        // Load font from current package
        val font: Font
        javaClass.getResourceAsStream(name).use {
            font = Font.createFont(Font.TRUETYPE_FONT, it).deriveFont(size)
        }

        // Convert font characters to images and bit sets at size 16
        val metrics = FontDesignMetrics.getMetrics(font)
        for (c in 1..126) {
            val char = c.toChar()

            // Ignore empty characters
            if (Character.isWhitespace(char) || !font.canDisplay(char)) continue

            // Convert character to bitmap
            val width = metrics.stringWidth(char.toString())
            val charImg = BufferedImage(width, 16, BufferedImage.TYPE_INT_ARGB)
            val g = charImg.createGraphics()
            g.color = Color.BLACK
            g.font = font
            g.drawString(char.toString(), 0, 12)
            chars += CharGrid(char, charImg)

            // DEBUG - Save character image to file
            //ImageIO.write(image, "png", File("chars/char_$c.png"))
        }
    }

    override fun iterator() = chars.iterator()
}