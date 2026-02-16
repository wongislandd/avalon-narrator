package com.avalonnarrator.domain.recommendation

import com.avalonnarrator.domain.setup.GameModule
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

    @Test
    fun `lady of the lake is only recommended for lineups with 7 or more players`() {
        RecommendedLineupCatalog.all().forEach { lineup ->
            if (GameModule.LADY_OF_LAKE in lineup.recommendedModules) {
                assertTrue(
                    lineup.playerCount >= 7,
                    "Lineup ${lineup.id} recommends Lady of the Lake below 7 players.",
                )
            }
        }
    }

    @Test
    fun `catalog includes at least one recommendation for each non-lancelot optional module`() {
        val recommendedModules = RecommendedLineupCatalog.all()
            .flatMap { it.recommendedModules }
            .toSet()

        assertTrue(GameModule.EXCALIBUR in recommendedModules, "Expected at least one Excalibur recommendation.")
        assertTrue(GameModule.LADY_OF_LAKE in recommendedModules, "Expected at least one Lady of the Lake recommendation.")
        assertTrue(GameModule.LANCELOT !in recommendedModules, "Lancelot module should be inferred from lineup roles.")
    }
}
