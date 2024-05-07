class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello HealthWallet, ${platform.name}!"
    }
}