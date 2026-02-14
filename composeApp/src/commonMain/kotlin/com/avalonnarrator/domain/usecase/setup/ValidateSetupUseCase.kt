package com.avalonnarrator.domain.usecase.setup

import com.avalonnarrator.domain.model.SetupValidationResult
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.SetupIssueLevel
import com.avalonnarrator.engine.validation.SetupValidator

class ValidateSetupUseCase(
    private val setupValidator: SetupValidator,
) {
    operator fun invoke(config: GameSetupConfig): SetupValidationResult {
        val allIssues = if (config.validatorsEnabled) setupValidator.validate(config) else emptyList()
        val blockingIssues = allIssues.filter { it.level == SetupIssueLevel.ERROR }
        val nonBlockingIssues = allIssues.filter { it.level != SetupIssueLevel.ERROR }
        return SetupValidationResult(
            blockingIssues = blockingIssues,
            nonBlockingIssues = nonBlockingIssues,
            allIssues = allIssues,
        )
    }
}
