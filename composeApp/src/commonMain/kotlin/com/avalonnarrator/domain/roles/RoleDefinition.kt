package com.avalonnarrator.domain.roles

import com.avalonnarrator.domain.setup.GameModule

data class RoleDefinition(
    val id: RoleId,
    val name: String,
    val alignment: Alignment,
    val imageKey: String,
    val mechanicSummary: String,
    val modules: Set<GameModule> = emptySet(),
    val tags: Set<String> = emptySet(),
)
