package com.avalonnarrator.domain.recommendation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecommendedLineupCatalogTest {

    @Test
    fun `contains at least two lineups for each player count between 5 and 10`() {
        (5..10).forEach { playerCount ->
            val options = RecommendedLineupCatalog.forPlayerCount(playerCount)
            assertTrue(options.size >= 2, "Expected at least two lineups for $playerCount players.")
        }
    }

    @Test
    fun `all lineups assign exactly declared player count`() {
        RecommendedLineupCatalog.all().forEach { lineup ->
            val total = lineup.specialRoles.size + lineup.loyalServants + lineup.minions
            assertEquals(lineup.playerCount, total, "Lineup ${lineup.id} has invalid total.")
        }
    }
}
