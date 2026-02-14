package com.avalonnarrator.engine.rules

import kotlin.test.Test
import kotlin.test.assertTrue

class StandardNarrationRulesTest {

    @Test
    fun `rule set defines deterministic order`() {
        val sorted = StandardNarrationRules.steps.sortedWith(compareBy({ it.phase.sortOrder }, { it.order }))
        assertTrue(StandardNarrationRules.steps.isNotEmpty())
        assertTrue(sorted.first().id == "intro")
        assertTrue(sorted.last().id == "closing")
    }
}
