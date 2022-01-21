package software.blob.runescape.transcriber.grid.filter

/**
 * A filter that runs through multiple filters in OR configuration
 * @param filters Filters to use
 */
class MultiGridFilter(private val filters: Array<ImageGridFilter>) : ImageGridFilter {

    /**
     * Check if this pixel passes any of the filters
     * @param r Red channel
     * @param g Green channel
     * @param b Blue channel
     * @param a Alpha channel
     * @return True if the pixel passes
     */
    override fun accept(r: Int, g: Int, b: Int, a: Int): Boolean {
        for (filter in filters) if (filter.accept(r, g, b, a)) return true
        return false
    }
}