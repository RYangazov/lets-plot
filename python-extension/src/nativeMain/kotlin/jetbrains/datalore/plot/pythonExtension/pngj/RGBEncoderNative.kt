/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj

import jetbrains.datalore.base.encoding.Base64
import jetbrains.datalore.vis.svg.SvgImageElementEx.RGBEncoder
import jetbrains.datalore.vis.svg.SvgUtils

class RGBEncoderNative : RGBEncoder {

    override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {
        val outputStream = OutputPngStream()
        val png = PngWriter(
            outputStream, ImageInfo(
                width,
                height,
                bitdepth = 8,
                alpha = true
            )
        )
        val iLine = ImageLineByte(png.imgInfo)

        for (row in 0 until height) {
            var p = 0
            for (col in 0 until width) {
                val pixel = argbValues[row * width + col]
                iLine.scanline[p++] = ((pixel shr 16) and 0xFF).toByte()  // Red component
                iLine.scanline[p++] = ((pixel shr 8) and 0xFF).toByte()   // Green component
                iLine.scanline[p++] = (pixel and 0xFF).toByte()           // Blue component
                iLine.scanline[p++] = ((pixel shr 24) and 0xFF).toByte()  // Alpha component
            }
            png.writeRow(iLine)
        }
        png.end()
        return SvgUtils.pngDataURI(Base64.encode(outputStream.byteArray))
    }
}
