/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj.chunks

import jetbrains.datalore.plot.pythonExtension.pngj.ImageInfo
import jetbrains.datalore.plot.pythonExtension.pngj.PngjException

/**
 * tEXt chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11tEXt
 */
internal class PngChunkTEXT(info: ImageInfo?) : PngChunkTextVar(ID, info) {

    override fun createRawChunk(): ChunkRaw {
        if (key == null || key!!.trim { it <= ' ' }.isEmpty()) throw PngjException("Text chunk key must be non empty")
        val b: ByteArray = ChunkHelper.toBytesLatin1(key + "\u0000" + value)
        val chunk: ChunkRaw = createEmptyChunk(b.size, false)
        chunk.data = b
        return chunk
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        var i = 0
        while (i < chunk.data!!.size) {
            if (chunk.data!![i].toInt() == 0) break
            i++
        }
        key = ChunkHelper.toStringLatin1(chunk.data!!, 0, i)
        i++
        value = if (i < chunk.data!!.size) ChunkHelper.toStringLatin1(chunk.data!!, i, chunk.data!!.size - i) else ""
    }

    companion object {
        const val ID: String = ChunkHelper.tEXt
    }
}