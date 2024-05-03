class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello SmartHealthWallet, ${platform.name}!"
    }
}