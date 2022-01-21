package software.blob.runescape.transcriber.grid

import software.blob.runescape.transcriber.grid.filter.ColorGridFilter
import java.awt.image.BufferedImage

/**
 * Image bit grid for a font character
 * @param char Font character
 * @param image Corresponding character image
 */
class CharGrid(val char: Char, image: BufferedImage) : ImageGrid(image, ColorGridFilter(0, 0, 0, 0)) {

    /**
     * Show the character instead of the default [toString]
     * @return Character as a string
     */
    override fun toString() = char.toString()
}