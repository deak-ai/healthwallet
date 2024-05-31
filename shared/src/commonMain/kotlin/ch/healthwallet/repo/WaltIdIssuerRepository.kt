package ch.healthwallet.repo

import kotlin.Result

interface WaltIdIssuerRepository {

    suspend fun openId4VcJwtIssue(issueRequest: OpenId4VcJwtIssueRequest): Result<String>

}