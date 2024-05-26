package ch.healthwallet.repo

interface WaltIdIssuerRepository {

    suspend fun openId4VcJwtIssue(issueRequest: OpenId4VcJwtIssueRequest): Result<String>

}