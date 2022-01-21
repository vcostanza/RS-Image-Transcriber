package software.blob.runescape.transcriber.grid.filter

/**
 * Interface for filtering image pixels
 */
interface ImageGridFilter {

    /**
     * Check if this pixel passes this filter
     * @param r Red channel
     * @param g Green channel
     * @param b Blue channel
     * @param a Alpha channel
     * @return True if the pixel passes
     */
    fun accept(r: Int, g: Int, b: Int, a: Int): Boolean
}