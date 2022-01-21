package software.blob.runescape.transcriber

import software.blob.runescape.transcriber.font.FontChars
import software.blob.runescape.transcriber.grid.BitGrid
import software.blob.runescape.transcriber.grid.CharGrid
import software.blob.runescape.transcriber.grid.ImageGrid
import software.blob.runescape.transcriber.grid.filter.ColorGridFilter
import software.blob.runescape.transcriber.grid.filter.ImageGridFilter
import software.blob.runescape.transcriber.grid.filter.MultiGridFilter
import software.blob.runescape.transcriber.grid.filter.ThresholdGridFilter
import java.awt.image.BufferedImage
import java.util.HashSet

// Characters that are simple enough to be detected as false positives
private val simpleChars = HashSet(listOf('.', '-', '_', '|', 'u', 'c', 'l'))

// Chat colors (non-exhaustive)
private val colors = intArrayOf(
        255, 255, 255,  // White
        255, 0, 0,      // Red
        0, 0, 255,      // Blue
        0, 0, 128,      // Dark Blue
        8, 17, 144,     // Dark Blue 2
        0, 255, 0,      // Green
        255, 255, 0,    // Yellow
        0, 255, 255,    // Cyan
        255, 0, 255,    // Magenta
        126, 50, 0,     // Global
        128, 0, 0,      // CC
        122, 16, 229,   // Violet
        128, 0, 128     // Indigo
)

/**
 * Reads characters from an image
 */
class CharReader {

    /**
     * Read characters from an image into a character assembler
     * @param image Image to read from
     * @param chars Font characters to use for reading
     * @param lossy True if the image has lossy compression (JPEG)
     * @return Character assembler containing characters at their read position
     */
    fun read(image: BufferedImage, chars: FontChars, lossy: Boolean = false): CharAssembler {

        // Create chat color filters
        val filters = Array<ImageGridFilter>(colors.size / 3) {
            val ci = it * 3
            ColorGridFilter(colors[ci], colors[ci + 1], colors[ci + 2])
        }

        // Create image grids
        val images = if (lossy) {
            // Apply a simple threshold filter to deal with lossy compression
            arrayOf(ImageGrid(image, ThresholdGridFilter()))
        } else {
            // Apply black and chat color filters to do multiple passes
            val imgBlack = ImageGrid(image, ColorGridFilter(0, 0, 0))
            val imgColors = ImageGrid(image, MultiGridFilter(filters))
            arrayOf(imgColors, imgBlack)
        }

        // DEBUG - Save output images to working directory
        //for (i in images.indices) images[i].saveToFile(File("debug_out_$i.png"))

        // Track which characters are at which position
        val assembler = CharAssembler()
        val occupied = BitGrid(image.width, image.height)

        // For each row in the image
        var ix = 0
        var iy = 0
        while (iy < image.height) {

            // For each column in this row
            while (ix < image.width) {

                // Check if a character has already been placed in this spot
                if (occupied[ix, iy]) {
                    ix++
                    continue
                }

                var match: CharGrid? = null

                // For each image threshold
                for (img in images) {

                    // Check if this pixel is filled in
                    if (img[ix, iy]) {

                        // Find the largest character match
                        for (c in chars) {

                            // Ignore character that is smaller than the current matched height
                            if (match != null && c.contentHeight <= match.contentHeight) continue

                            val yPad = if (simpleChars.contains(c.char)) 1 else 0
                            if (img.equals(c, ix - c.firstX, iy, yPad))
                                match = c
                        }
                    }
                }

                // Check for largest character match
                if (match != null) {

                    // Add the character to the list
                    val mx = ix - match.firstX
                    val my = iy - match.firstY
                    assembler.addChar(match, mx, my)

                    // Flag this area as occupied so other characters can't use this space
                    for (y in 0 until match.contentHeight) {
                        for (x in 0 until match.width)
                            occupied[mx + x, iy + y] = true
                    }

                    // Increment past the character size
                    ix = mx + match.width
                    continue
                }

                // Increment column
                ix++
            }

            // Increment row and reset start column
            iy++
            ix = 0
        }

        return assembler
    }
}