package software.blob.runescape.transcriber.grid

import software.blob.runescape.transcriber.grid.filter.ImageGridFilter
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

/**
 * Converts an image to a bit grid where each pixel is either "on" or "off"
 * @param image Image to read
 * @param filter Filter that determines which pixels are on or off
 */
open class ImageGrid(image: BufferedImage, filter: ImageGridFilter) : BitGrid(image.width, image.height) {

    // The first and last "on" pixels
    val firstX: Int
    val firstY: Int
    private val lastX: Int
    private val lastY: Int

    // The height of the character excluding empty space
    val contentHeight: Int

    init {

        // Get image pixels as an integer array
        val w = image.width
        val h = image.height
        val c = image.raster.numBands
        val p = IntArray(w * h * c)
        image.raster.getPixels(0, 0, w, h, p)

        // Compute which pixels are on or off
        var i = 0
        var j = 0
        var first = 0
        var last = 0
        while (i < p.size) {
            val on = when (c) {
                1 -> filter.accept(p[i], p[i], p[i], 255)
                3 -> filter.accept(p[i], p[i + 1], p[i + 2], 255)
                4 -> filter.accept(p[i], p[i + 1], p[i + 2], p[i + 3])
                else -> false
            }
            if (on) {
                if (first == 0) first = j
                last = j
            }
            this.set[j++] = on
            i += c
        }

        // Calculate bounds of pixels that are filled in
        this.firstX = first % w
        this.firstY = first / w
        this.lastX = last % w
        this.lastY = last / w
        this.contentHeight = (this.lastY - this.firstY) + 1
    }

    /**
     * Check if the bit grid chunk at the given position is equal to another bit grid
     * @param other Bit grid to check
     * @param thisX The X position of this grid to check equality
     * @param thisY The Y position of this grid to check equality
     * @param yPad Top and bottom padding to use for equality checking
     */
    fun equals(other: ImageGrid, thisX: Int = 0, thisY: Int = 0, yPad: Int = 0): Boolean {
        val ow = other.width
        val startY = max(other.firstY - yPad, 0)
        val endY = min(other.lastY + yPad, other.height - 1)
        val oh = (endY - startY) + 1
        for (y in 0 until oh) {
            for (x in 0 until ow) {
                val oy = y + startY
                val ix = x + thisX
                val iy = y + thisY - yPad
                if (other[x, oy] != this[ix, iy])
                    return false
            }
        }
        return true
    }

    /**
     * Save this bit grid to a file where filled pixels are white and everything else is black
     * @param file File to save to
     */
    fun saveToFile(file: File) {
        var p = 0
        val pixels = IntArray(width * height * 3)
        for (solid in set) {
            val v = if (solid) 255 else 0
            pixels[p++] = v
            pixels[p++] = v
            pixels[p++] = v
        }
        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        img.raster.setPixels(0, 0, width, height, pixels)
        ImageIO.write(img, "png", file)
    }
}