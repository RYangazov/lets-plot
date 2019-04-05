package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.registration.Registration

/**
 * This 'element' is not a part of SVG specification.
 * During mapping process it will be mapped to svg image element where 'bitmap' is encoded as image data url
 * and set as a value of 'href' attribute.
 */
class SvgImageElementEx(x: Double, y: Double, width: Double, height: Double, private val myBitmap: Bitmap) : SvgImageElement(x, y, width, height) {

    fun href(): Property<String> {
        // Make href read-only
        // The 'href' shouldn't be present in the set returned by SvgElement#getAttributeKeys()
        val hrefProp = super.href()
        return object : Property<String>() {
            val propExpr: String
                get() = hrefProp.getPropExpr()

            fun get(): String {
                return hrefProp.get()
            }

            fun addHandler(handler: EventHandler<PropertyChangeEvent<String>>): Registration {
                return hrefProp.addHandler(handler)
            }

            fun set(value: String) {
                throw IllegalStateException("href property is read-only in " + this@SvgImageElementEx.getClass().getSimpleName())
            }
        }
    }

    fun asImageElement(encoder: RGBEncoder): SvgImageElement {
        val imageElement = SvgImageElement()
        SvgUtils.copyAttributes(this, imageElement)

        val hrefValue = encoder.toDataUrl(
                myBitmap.width,
                myBitmap.height,
                myBitmap.argbValues
        )
        imageElement.href().set(hrefValue)
        return imageElement
    }

    interface RGBEncoder {
        fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String
    }

    class Bitmap
    /**
     * @param argbValues image binary data.
     * Each element of the array represents a pixel,
     * where alpha, red, green, blue values are in the range [0..255] and are packed into four bytes.
     * The array is filled by-row.
     */
    (val width: Int, val height: Int, argbValues: IntArray) {
        val argbValues: IntArray

        init {
            this.argbValues = IntArray(argbValues.size)
            System.arraycopy(argbValues, 0, this.argbValues, 0, argbValues.size)
        }
    }
}
