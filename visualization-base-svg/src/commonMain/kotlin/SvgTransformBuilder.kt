package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.geometry.DoubleVector

import jetbrains.datalore.visualization.base.svg.SvgTransform.MATRIX
import jetbrains.datalore.visualization.base.svg.SvgTransform.ROTATE
import jetbrains.datalore.visualization.base.svg.SvgTransform.SCALE
import jetbrains.datalore.visualization.base.svg.SvgTransform.SKEW_X
import jetbrains.datalore.visualization.base.svg.SvgTransform.SKEW_Y
import jetbrains.datalore.visualization.base.svg.SvgTransform.TRANSLATE

class SvgTransformBuilder {
    private val myStringBuilder = StringBuilder()

    fun build(): SvgTransform {
        return SvgTransform(myStringBuilder.toString())
    }

    private fun addTransformation(name: String, vararg values: Double): SvgTransformBuilder {
        myStringBuilder.append(name).append('(')
        for (`val` in values) {
            myStringBuilder.append(`val`).append(' ')
        }
        myStringBuilder.append(") ")
        return this
    }

    fun matrix(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double): SvgTransformBuilder {
        return addTransformation(MATRIX, a, b, c, d, e, f)
    }

    fun translate(x: Double, y: Double): SvgTransformBuilder {
        return addTransformation(TRANSLATE, x, y)
    }

    fun translate(vector: DoubleVector): SvgTransformBuilder {
        return translate(vector.x, vector.y)
    }

    fun translate(x: Double): SvgTransformBuilder {
        return addTransformation(TRANSLATE, x)
    }

    fun scale(x: Double, y: Double): SvgTransformBuilder {
        return addTransformation(SCALE, x, y)
    }

    fun scale(x: Double): SvgTransformBuilder {
        return addTransformation(SCALE, x)
    }

    fun rotate(a: Double, x: Double, y: Double): SvgTransformBuilder {
        return addTransformation(ROTATE, a, x, y)
    }

    fun rotate(a: Double, origin: DoubleVector): SvgTransformBuilder {
        return rotate(a, origin.x, origin.y)
    }

    fun rotate(a: Double): SvgTransformBuilder {
        return addTransformation(ROTATE, a)
    }

    fun skewX(a: Double): SvgTransformBuilder {
        return addTransformation(SKEW_X, a)
    }

    fun skewY(a: Double): SvgTransformBuilder {
        return addTransformation(SKEW_Y, a)
    }
}