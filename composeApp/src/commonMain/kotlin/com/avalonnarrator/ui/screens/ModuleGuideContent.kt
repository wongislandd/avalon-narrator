package com.avalonnarrator.ui.screens

import com.avalonnarrator.domain.setup.GameModule

data class ModuleGuideContent(
    val title: String,
    val shortLabel: String,
    val description: String,
    val gameplayImpact: String,
)

fun moduleGuideContent(module: GameModule): ModuleGuideContent = when (module) {
    GameModule.EXCALIBUR -> ModuleGuideContent(
        title = "Excalibur",
        shortLabel = "Tactical disruption module",
        description = "Adds the Excalibur token and assigns it to one questing player during relevant rounds.",
        gameplayImpact = "Before quest cards are revealed, the Excalibur holder may force one quest-card switch between questing players, which can flip a quest result and create bluff pressure.",
    )

    GameModule.LADY_OF_LAKE -> ModuleGuideContent(
        title = "Lady of the Lake",
        shortLabel = "Loyalty-check module",
        description = "Adds the Lady of the Lake token and periodic alignment inspection windows.",
        gameplayImpact = "At Lady checkpoints, the holder inspects one player's alignment and then passes the token to that inspected player, creating directional information and social pressure.",
    )

    GameModule.LANCELOT -> ModuleGuideContent(
        title = "Lancelot",
        shortLabel = "Allegiance-uncertainty module",
        description = "Adds Good Lancelot and Evil Lancelot role interactions and supports allegiance-shift variants.",
        gameplayImpact = "Introduces uncertainty around team reliability and can change deduction math if allegiance-change rules are in play.",
    )
}
