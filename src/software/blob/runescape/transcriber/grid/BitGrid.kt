package software.blob.runescape.transcriber.grid

import java.util.*

/**
 * A grid of boolean values
 * This intentionally does not use a [BitSet] to increase read/write speeds at the cost of using more memory
 * @param width Width of the grid
 * @param height Height of the grid
 */
open class BitGrid(val width: Int, val height: Int) {

    // The underlying boolean array
    protected val set = BooleanArray(width * height)

    // The size of the entire grid (width * height)
    private val size = set.size

    /**
     * Get the boolean value at the given index
     * @param index Index to read
     * @return True or false
     */
    operator fun get(index: Int) = index in 0 until size && set[index]

    /**
     * Get the boolean value at the given coordinate
     * @param x X position
     * @param y Y position
     * @return True or false
     */
    operator fun get(x: Int, y: Int) = x >= 0 && y >= 0 && x < width && y < height && this[x + y * width]

    /**
     * Set the boolean value at the given index
     * @param index Index to set
     * @param value True or false
     */
    operator fun set(index: Int, value: Boolean) {
        if (index in 0 until size) set[index] = value
    }

    /**
     * Set the boolean value at the given coordinate
     * @param x X position
     * @param y Y position
     * @param value True or false
     */
    operator fun set(x: Int, y: Int, value: Boolean) {
        if (x >= 0 && y >= 0 && x < width && y < height) this[x + y * width] = value
    }
}