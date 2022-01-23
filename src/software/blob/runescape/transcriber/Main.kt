package software.blob.runescape.transcriber

import software.blob.runescape.transcriber.font.FontChars
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileWriter
import javax.imageio.ImageIO

/**
 * Main runner class for the image transcriber
 */
class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            if (args.isEmpty()) {
                println("Usage: RSImageTranscriber <image file> <output text file>")
                return
            }

            val inPath = args[0]
            val inFile = File(inPath)

            // Make sure the input file exists
            if (!inFile.exists()) {
                error("File does not exist: $inPath")
                return
            }

            // Read input image
            val inImage: BufferedImage
            try {
                inImage = ImageIO.read(inFile)
            } catch (e: Exception) {
                error("Unable to read file as image: $inPath")
                return
            }

            println("Scanning ${inFile.name} for text")

            // Load RS chat font
            val chars = FontChars("RuneScape-Plain-12.ttf", 16f)

            // Read characters from the image
            val reader = CharReader()
            val assembler = reader.read(inImage, chars, inFile.name.toLowerCase().endsWith(".jpg"))

            // Get the lines of text from the list of characters
            val lines = assembler.assemble()

            // No text found
            if (lines.isEmpty()) {
                println("No text found in $inPath")
                return
            }

            if (args.size > 1) {
                // Write lines to file
                val outPath = args[1]
                val outFile = File(outPath)
                try {
                    FileWriter(outFile).use { for (line in lines) it.write("$line\n") }
                    println("Wrote lines to file: $outPath")
                } catch (e: Exception) {
                    error("Failed to write to file: $outPath")
                    e.printStackTrace()
                }
            } else {
                // Print lines to console
                println()
                for (line in lines) println(line)
            }
        }

        /**
         * Log error to console
         * @param str Error string
         */
        private fun error(str: String) = System.err.println(str)
    }
}