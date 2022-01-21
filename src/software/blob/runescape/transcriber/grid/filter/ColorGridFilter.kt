package software.blob.runescape.transcriber.grid.filter

import kotlin.math.abs

/**
 * Filters out pixels that aren't equal to the given color in a given threshold
 * @param r Red channel
 * @param g Green channel
 * @param b Blue channel
 * @param thresh Difference threshold (0 = must be exact same color)
 */
class ColorGridFilter(
        private val r: Int,
        private val g: Int,
        private val b: Int,
        private val thresh: Int = 10)
    : ImageGridFilter {

    /**
     * Check if this pixel passes the color filter
     * @param r Red channel
     * @param g Green channel
     * @param b Blue channel
     * @param a Alpha channel
     * @return True if the pixel is accepted
     */
    override fun accept(r: Int, g: Int, b: Int, a: Int): Boolean {
        if (a != 255) return false
        val rt = abs(this.r - r)
        val gt = abs(this.g - g)
        val bt = abs(this.b - b)
        return rt <= thresh && gt <= thresh && bt <= thresh
    }
}