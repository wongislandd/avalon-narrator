package com.avalonnarrator.domain.model

import com.avalonnarrator.domain.setup.SetupIssue

data class SetupValidationResult(
    val blockingIssues: List<SetupIssue>,
    val nonBlockingIssues: List<SetupIssue>,
    val allIssues: List<SetupIssue>,
) {
    val canStart: Boolean = blockingIssues.isEmpty()
}
