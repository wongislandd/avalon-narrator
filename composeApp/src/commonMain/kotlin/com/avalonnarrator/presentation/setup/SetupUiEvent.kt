package com.avalonnarrator.presentation.setup

import com.avalonnarrator.domain.roles.RoleDefinition
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule

sealed interface SetupUiEvent {
    data class ToggleRole(val roleId: RoleId) : SetupUiEvent
    data class ToggleModule(val module: GameModule) : SetupUiEvent
    data class ShowModulePreview(val module: GameModule) : SetupUiEvent
    data object HideModulePreview : SetupUiEvent
    data class ToggleBaseRole(val roleId: RoleId) : SetupUiEvent
    data class IncreaseBaseRole(val roleId: RoleId) : SetupUiEvent
    data class DecreaseBaseRole(val roleId: RoleId) : SetupUiEvent
    data class ShowRolePreview(val role: RoleDefinition) : SetupUiEvent
    data object HideRolePreview : SetupUiEvent
    data class OpenRoleCategoryInfo(val category: SetupRoleCategory) : SetupUiEvent
    data object CloseRoleCategoryInfo : SetupUiEvent
    data object OpenModuleInfo : SetupUiEvent
    data object CloseModuleInfo : SetupUiEvent
    data object OpenSettings : SetupUiEvent
    data object OpenLineupGuide : SetupUiEvent
    data object StartNarration : SetupUiEvent
}
