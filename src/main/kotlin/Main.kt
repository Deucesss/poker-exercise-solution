import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun main(args: Array<String>) {
    val pathStr = args.first()

    var playerA = 0
    var playerB = 0

    try {
        Files.readAllLines(Paths.get(pathStr))
            .stream()
            .forEach {
                val allCards = it.split(" ")
                val playerACards = allCards.subList(0, 5).joinToString(" ")
                val playerBCards = allCards.subList(5, allCards.size).joinToString(" ")
                val playerAHand = PokerHand.determine(playerACards)
                val playerBHand = PokerHand.determine(playerBCards)
                if (playerAHand > playerBHand) {
                    playerA++
                } else if (playerAHand < playerBHand) {
                    playerB++
                }
            }
        println("Player 1: $playerA")
        println("Player 2: $playerB")
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

