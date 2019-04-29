package jetbrains.datalore.base.enums

import jetbrains.datalore.base.enums.EnumInfoTest.Scope.*
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class EnumInfoTest {

    @Test
    fun test() {
        checkValid(TEST, "TEST")
    }

    @Test
    fun lower() {
        checkValid(TEST, "test")
    }

    @Test
    fun invalid() {
        checkInvalid("method")
    }

    @Test
    fun nullCase() {
        checkInvalid(null)
    }

    @Test
    fun empty() {
        checkInvalid("")
    }

    @Test
    fun suiteCase() {
        checkValid(SUITE, "suite")
    }

    @Test
    fun classCase() {
        checkValid(CLASS, "class")
    }

    @Test(expected = IllegalArgumentException::class)
    fun duplicateValues() {
        checkEnumConstant(EnumWithDuplicates.TEST, "TEST", "TEST")
        checkEnumConstant(EnumWithDuplicates.CLASS, "CLASS", "test")
        EnumInfoFactory.createEnumInfo(EnumWithDuplicates::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun unsafeValueOf() {
        Scope.unsafeValueOf("method")
    }

    @Test
    fun originalNames() {
        assertEquals(Arrays.asList("TEST", "CLASS", "SUITE"), Scope.originalNames)
    }

    private fun checkValid(expected: Scope, text: String) {
        assertTrue(Scope.hasValue(text))
        assertEquals(expected, Scope.unsafeValueOf(text))
        assertEquals(expected, Scope.safeValueOf(text))
        assertEquals(expected, Scope.safeValueOf(text, SUITE))
    }

    private fun checkInvalid(text: String?) {
        assertFalse(Scope.hasValue(text))
        assertNull(Scope.safeValueOf(text))
        assertEquals(SUITE, Scope.safeValueOf(text, SUITE))
    }

    private fun <EnumT : Enum<EnumT>> checkEnumConstant(enumConstant: EnumT, expectedName: String, expectedString: String) {
        assertEquals(expectedName, enumConstant.name)
        assertEquals(expectedString, enumConstant.toString())
    }

    internal enum class Scope {

        TEST, CLASS, SUITE;

        companion object {
            private val ENUM_INFO = EnumInfoFactory.createEnumInfo(Scope::class.java)

            internal fun hasValue(text: String?): Boolean {
                return ENUM_INFO.hasValue(text)
            }

            internal fun unsafeValueOf(text: String): Scope {
                return ENUM_INFO.unsafeValueOf(text)
            }

            internal fun safeValueOf(text: String?): Scope? {
                return ENUM_INFO.safeValueOf(text)
            }

            internal fun safeValueOf(text: String?, defaultScope: Scope): Scope {
                return ENUM_INFO.safeValueOf(text, defaultScope)
            }

            internal val originalNames: List<String>
                get() = ENUM_INFO.originalNames
        }
    }

    internal enum class EnumWithDuplicates {
        TEST,
        CLASS {
            override fun toString(): String {
                return "test"
            }
        }
    }
}
