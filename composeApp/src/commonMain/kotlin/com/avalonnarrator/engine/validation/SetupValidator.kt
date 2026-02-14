package com.avalonnarrator.engine.validation

import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.SetupIssue

interface SetupValidator {
    fun validate(config: GameSetupConfig): List<SetupIssue>
}
