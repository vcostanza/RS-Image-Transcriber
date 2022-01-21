package software.blob.runescape.transcriber.grid.filter

/**
 * Filters pixels whose luminance are under a given threshold
 * @param thresh Maximum luminance value
 */
class ThresholdGridFilter(private val thresh: Int = 110) : ImageGridFilter {

    /**
     * Check if this pixel passes this filter
     * @param r Red channel
     * @param g Green channel
     * @param b Blue channel
     * @param a Alpha channel
     * @return True if the pixel passes
     */
    override fun accept(r: Int, g: Int, b: Int, a: Int): Boolean {
        if (a != 255) return false
        return (r + g + b) / 3 <= thresh
    }
}