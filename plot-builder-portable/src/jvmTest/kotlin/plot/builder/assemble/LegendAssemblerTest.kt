/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat

class LegendAssemblerTest {

    @Test
    fun skipWrappingForShortText() {
        assertThat(LegendAssembler.wrap("abc", 7))
            .isEqualTo("abc")
        assertThat(LegendAssembler.wrap("abcdefg", 7))
            .isEqualTo("abcdefg")
        assertThat(LegendAssembler.wrap("ab cd e", 7))
            .isEqualTo("ab cd e")
    }

    @Test
    fun skipWrappingIfTextHaveDividers() {
        assertThat(
            LegendAssembler.wrap(
                "Lorem ipsum dolor sit amet,\n"
                        + "consectetur adipiscing elit.", 7
            )
        )
            .isEqualTo("Lorem ipsum dolor sit amet,\nconsectetur adipiscing elit.")
    }

    @Test
    fun wrapWordsSimple() {
        assertThat(LegendAssembler.wrap("Lorem ipsum dolor sit amet", 10))
            .isEqualTo(
                """
                |Lorem
                |ipsum
                |dolor sit
                |amet
                """.trimMargin()
            )
        assertThat(LegendAssembler.wrap("Lorem ipsum dolor sit amet, consectetur adipiscing elit", 15))
            .isEqualTo(
                """
                |Lorem ipsum
                |dolor sit amet,
                |consectetur
                |adipiscing elit
                """.trimMargin()
            )
    }

    @Test
    fun wrapWithoutSpaces() {
        assertThat(LegendAssembler.wrap("abcdefghijklmnopqrstuvwxyz", 7, 4))
            .isEqualTo(
                """
                |abcdefg
                |hijklmn
                |opqrstu
                |vwxyz
                """.trimMargin()
            )
    }

    @Test
    fun wrapWithLimitOverflow() {
        assertThat(LegendAssembler.wrap("abcdefghijklmnopqrstuvwxyz", 7, 3))
            .isEqualTo(
                """
                |abcdefg
                |hijklmn
                |opqrstu
                |...
                """.trimMargin()
            )
        assertThat(LegendAssembler.wrap("abcde fghijk lmno pqrstuvwxyz", 7, 3))
            .isEqualTo(
                """
                |abcde
                |fghijk
                |lmno
                |...
                """.trimMargin()
            )
    }

    @Test
    fun wrapLongWords() {
        assertThat(LegendAssembler.wrap("amet, consectetur adipiscing elit", 7))
            .isEqualTo(
                """
                |amet,
                |consect
                |etur
                |adipisc
                |ing
                |elit
                """.trimMargin()
            )

        //If long word stay after space character, what have position index greater than string.length / 3,
        //then long word will be wrap at new line
        assertThat(LegendAssembler.wrap("Lorem ipsum abcdefghijklmnopqrstuvwxyz, abcdefghijklmnopqrstuvwxyz elit.", 20))
            .isEqualTo(
                """
                |Lorem ipsum
                |abcdefghijklmnopqrst
                |uvwxyz,
                |abcdefghijklmnopqrst
                |uvwxyz elit.
                """.trimMargin()
            )
        //If long word stay after space character, what have position index lower than string.length / 3,
        //then long word will be cut and wrap at current line
        assertThat(LegendAssembler.wrap("Lorem abcdefghijklmnopqrstuvwxyz, abcdefghijklmnopqrstuvwxyz elit.", 20))
            .isEqualTo(
                """
                |Lorem abcdefghijklmn
                |opqrstuvwxyz,
                |abcdefghijklmnopqrst
                |uvwxyz elit.
                """.trimMargin()
            )

    }

}