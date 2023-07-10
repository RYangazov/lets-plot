/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.chart.fragments

import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.livemap.chart.fragment.RegionFragmentsComponent
import jetbrains.livemap.core.ecs.EcsEntity
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import java.util.*

class RegionAssert(entity: EcsEntity?, private val myTestBase: LiveMapTestBase) :
    AbstractAssert<RegionAssert?, EcsEntity?>(entity, RegionAssert::class.java) {
    private fun fragmentEntities(vararg fragments: FragmentSpec): Collection<EcsEntity> {
        val entities: MutableList<EcsEntity> = ArrayList()
        Arrays.stream(fragments).map(FragmentSpec::name).forEach { name: String ->
            try {
                entities.add(myTestBase.getEntity(name))
            } catch (e: NoSuchElementException) {
                throw IllegalStateException("Entity $name is not found")
            }
        }
        return entities
    }

    fun rendersFragments(vararg fragments: FragmentSpec) {
        Assertions.assertThat(actual!!.get<RegionFragmentsComponent>().fragments)
            .containsExactlyInAnyOrderElementsOf(fragmentEntities(*fragments))
    }
}