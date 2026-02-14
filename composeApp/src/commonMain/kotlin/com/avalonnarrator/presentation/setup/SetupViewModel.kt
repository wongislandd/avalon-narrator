package com.avalonnarrator.presentation.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avalonnarrator.app.di.NarrationSession
import com.avalonnarrator.app.di.SetupSession
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.RosterBuilder
import com.avalonnarrator.domain.usecase.narration.BuildNarrationPlanUseCase
import com.avalonnarrator.domain.usecase.setup.SetupMutation
import com.avalonnarrator.domain.usecase.setup.ValidateSetupUseCase
import com.avalonnarrator.navigation.AppScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetupViewModel(
    private val setupSession: SetupSession,
    private val narrationSession: NarrationSession,
    private val validateSetupUseCase: ValidateSetupUseCase,
    private val buildNarrationPlanUseCase: BuildNarrationPlanUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<SetupUiEffect>()
    val effects = _effects.asSharedFlow()

    init {
        setupSession.initialize()
        observeSetup()
    }

    fun onEvent(event: SetupUiEvent) {
        when (event) {
            is SetupUiEvent.ToggleRole -> {
                setupSession.mutate(SetupMutation.ToggleRole(event.roleId))
            }

            is SetupUiEvent.ToggleModule -> {
                setupSession.mutate(SetupMutation.ToggleModule(event.module))
            }

            is SetupUiEvent.ToggleBaseRole -> {
                when (event.roleId) {
                    RoleId.LOYAL_SERVANT -> {
                        val nextCount = if (_uiState.value.loyalServantCount > 0) 0 else 1
                        setupSession.mutate(SetupMutation.SetLoyalServantCount(nextCount))
                    }

                    RoleId.MINION -> {
                        val nextCount = if (_uiState.value.minionCount > 0) 0 else 1
                        setupSession.mutate(SetupMutation.SetMinionCount(nextCount))
                    }

                    else -> Unit
                }
            }

            is SetupUiEvent.IncreaseBaseRole -> {
                when (event.roleId) {
                    RoleId.LOYAL_SERVANT -> setupSession.mutate(
                        SetupMutation.SetLoyalServantCount(_uiState.value.loyalServantCount + 1),
                    )

                    RoleId.MINION -> setupSession.mutate(
                        SetupMutation.SetMinionCount(_uiState.value.minionCount + 1),
                    )

                    else -> Unit
                }
            }

            is SetupUiEvent.DecreaseBaseRole -> {
                when (event.roleId) {
                    RoleId.LOYAL_SERVANT -> setupSession.mutate(
                        SetupMutation.SetLoyalServantCount(_uiState.value.loyalServantCount - 1),
                    )

                    RoleId.MINION -> setupSession.mutate(
                        SetupMutation.SetMinionCount(_uiState.value.minionCount - 1),
                    )

                    else -> Unit
                }
            }

            is SetupUiEvent.ShowRolePreview -> {
                _uiState.update { it.copy(previewRole = event.role, previewModule = null) }
            }

            SetupUiEvent.HideRolePreview -> {
                _uiState.update { it.copy(previewRole = null) }
            }

            is SetupUiEvent.OpenRoleCategoryInfo -> {
                _uiState.update { it.copy(selectedInfoCategory = event.category) }
                viewModelScope.launch {
                    _effects.emit(SetupUiEffect.Navigate(AppScreen.SETUP_ROLE_INFO))
                }
            }

            SetupUiEvent.CloseRoleCategoryInfo -> {
                _uiState.update { it.copy(selectedInfoCategory = null) }
                viewModelScope.launch {
                    _effects.emit(SetupUiEffect.Navigate(AppScreen.SETUP))
                }
            }

            SetupUiEvent.OpenModuleInfo -> {
                viewModelScope.launch {
                    _effects.emit(SetupUiEffect.Navigate(AppScreen.SETUP_MODULE_INFO))
                }
            }

            SetupUiEvent.CloseModuleInfo -> {
                viewModelScope.launch {
                    _effects.emit(SetupUiEffect.Navigate(AppScreen.SETUP))
                }
            }

            is SetupUiEvent.ShowModulePreview -> {
                _uiState.update { it.copy(previewModule = event.module, previewRole = null) }
            }

            SetupUiEvent.HideModulePreview -> {
                _uiState.update { it.copy(previewModule = null) }
            }

            SetupUiEvent.OpenSettings -> {
                viewModelScope.launch {
                    _effects.emit(SetupUiEffect.Navigate(AppScreen.SETTINGS))
                }
            }

            SetupUiEvent.OpenLineupGuide -> {
                viewModelScope.launch {
                    _effects.emit(SetupUiEffect.Navigate(AppScreen.LINEUP_GUIDE))
                }
            }

            SetupUiEvent.StartNarration -> {
                startNarration()
            }
        }
    }

    private fun observeSetup() {
        viewModelScope.launch {
            combine(
                setupSession.config,
                setupSession.isInitialized,
            ) { config, initialized -> config to initialized }.collectLatest { (config, initialized) ->
                val roster = RosterBuilder.build(config)
                val validation = validateSetupUseCase(config)
                _uiState.update { current ->
                    current.copy(
                        isInitialized = initialized,
                        config = config,
                        loyalServantCount = roster.loyalServantCount,
                        minionCount = roster.minionCount,
                        selectedCardsCount = config.selectedRoles.size + roster.loyalServantCount + roster.minionCount,
                        blockingIssues = validation.blockingIssues,
                        nonBlockingIssues = validation.nonBlockingIssues,
                        canStartNarration = validation.canStart,
                    )
                }
            }
        }
    }

    private fun startNarration() {
        val config = _uiState.value.config
        val validation = validateSetupUseCase(config)
        if (!validation.canStart) {
            _uiState.update {
                it.copy(
                    blockingIssues = validation.blockingIssues,
                    nonBlockingIssues = validation.nonBlockingIssues,
                    canStartNarration = false,
                )
            }
            return
        }
        val plan = buildNarrationPlanUseCase(config)
        narrationSession.prepare(plan)
        viewModelScope.launch {
            _effects.emit(SetupUiEffect.Navigate(AppScreen.NARRATOR))
        }
    }
}
