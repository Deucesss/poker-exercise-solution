import kotlin.math.pow
import kotlin.math.roundToInt

sealed class PokerHand(val hand: List<Card>) : Comparable<PokerHand> {

    abstract val rank: Int

    /**
     * @return used to compare with opponent with the same rank
     */
    abstract fun computeWeight(): Int

    init {
        if (hand.size != HAND_COUNT) {
            throw Throwable("Invalid Hand: $hand")
        }
    }

    override fun toString(): String =
        "Hand: ${hand.sortedBy { it.value }} Type: ${this.javaClass.simpleName} Weight: ${computeWeight()}"

    override fun compareTo(other: PokerHand): Int =
        if (rank != other.rank) {
            rank - other.rank
        } else {
            computeWeight() - other.computeWeight()
        }

    /**
     * Inline class to represent a single card e.g. "AC"
     */
    @JvmInline
    value class Card(private val cardStr: String) {

        val suit: Char
            get() = cardStr[1]

        val value: Int
            get() = when (cardStr[0]) {
                'T' -> 10
                'J' -> 11
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> cardStr[0].toString().toInt()
            }

        override fun toString(): String =
            value.toString() + suit.toString()
    }

    companion object {
        const val HAND_COUNT = 5

        @JvmStatic
        fun determine(hand: String): PokerHand {
            val cards = hand.split(" ").map { Card(it) }
            return when {
                RoyalFlush.check(cards) -> RoyalFlush(cards)
                StraightFlush.check(cards) -> StraightFlush(cards)
                Flush.check(cards) -> Flush(cards)
                Straight.check(cards) -> Straight(cards)
                FourOfAKind.check(cards) -> FourOfAKind(cards)
                FullHouse.check(cards) -> FullHouse(cards)
                ThreeOfAKind.check(cards) -> ThreeOfAKind(cards)
                TwoPairs.check(cards) -> TwoPairs(cards)
                Pair.check(cards) -> Pair(cards)
                HighCard.check(cards) -> HighCard(cards)
                else -> throw Throwable("Invalid Cards: $hand")
            }
        }
    }
}

class RoyalFlush(hand: List<Card>) : PokerHand(hand) {

    override val rank: Int = 10

    override fun computeWeight() = 0

    companion object {
        @JvmStatic
        fun check(cards: List<Card>): Boolean =
            // all cards have different value and same suit and the max value is 'A' and are in sequence
            cards.groupBy { it.value }.size == HAND_COUNT &&
                    cards.groupBy { it.suit }.size == 1 &&
                    cards.maxOf { it.value } == 14 &&
                    cards.maxOf { it.value } - cards.minOf { it.value } == HAND_COUNT - 1
    }
}

class StraightFlush(hand: List<Card>) : PokerHand(hand) {

    override val rank: Int = 9

    override fun computeWeight(): Int =
        hand.maxOf { it.value }

    companion object {
        @JvmStatic
        fun check(cards: List<Card>): Boolean =
            // all cards have different value and same suit and are in sequence
            cards.groupBy { it.value }.size == HAND_COUNT &&
                    cards.groupBy { it.suit }.size == 1 &&
                    cards.maxOf { it.value } - cards.minOf { it.value } == HAND_COUNT - 1
    }
}

class FourOfAKind(hand: List<Card>) : PokerHand(hand) {

    private val valueGroups = hand.valueGroups()

    override val rank: Int = 8

    override fun computeWeight(): Int =
        valueGroups.entries.fold(0) { acc, entry ->
            acc + if (entry.value == 4) {
                entry.key * 100
            } else {
                entry.key
            }
        }

    companion object {
        @JvmStatic
        fun check(cards: List<Card>): Boolean =
            // one value appears 4 times
            cards.valueGroups().containsValue(4)
    }
}

class FullHouse(hand: List<Card>) : PokerHand(hand) {

    private val valueGroups = hand.valueGroups()

    override val rank: Int = 7

    override fun computeWeight(): Int =
        valueGroups.entries.fold(0) { acc, entry ->
            acc + if (entry.value == 3) {
                entry.key * 100
            } else {
                entry.key
            }
        }

    companion object {
        @JvmStatic
        fun check(cards: List<Card>): Boolean =
            // two different values and one of them appears 2 times -> other appears three times
            cards.valueGroups().let { groups ->
                groups.size == 2 && groups.containsValue(2)
            }
    }
}

class Flush(hand: List<Card>) : PokerHand(hand) {

    override val rank: Int = 6

    override fun computeWeight(): Int =
        hand.sortedBy { it.value }
            .foldIndexed(0) { index, acc, card ->
                acc + card.value * 15.pow(index)
            }

    companion object {
        @JvmStatic
        fun check(cards: List<Card>): Boolean =
            // all cards have the same suit and not in sequence
            cards.groupBy { it.suit }.size == 1 && cards.maxOf { it.value } - cards.minOf { it.value } > 4
    }
}

class Straight(hand: List<Card>) : PokerHand(hand) {

    override val rank: Int = 5

    override fun computeWeight(): Int =
        hand.maxOf { it.value }

    companion object {
        @JvmStatic
        fun check(cards: List<Card>): Boolean =
            // all cards are distinct and all cards are in sequence and not the same suit
            cards.valueGroups().size == HAND_COUNT &&
                    cards.maxOf { it.value } - cards.minOf { it.value } == HAND_COUNT - 1 &&
                    cards.groupBy { it.suit }.size > 1
    }
}

class ThreeOfAKind(hand: List<Card>) : PokerHand(hand) {

    private val valueGroups = hand.valueGroups()

    override val rank: Int = 4

    override fun computeWeight(): Int =
        valueGroups.entries.sortedBy { it.key }.foldIndexed(0) { index, acc, entry ->
            acc + if (entry.value == 3) {
                entry.key * 10000
            } else {
                entry.key * 15.pow(index)
            }
        }

    companion object {
        @JvmStatic
        fun check(cards: List<Card>): Boolean =
            cards.valueGroups().let { groups ->
                // three different values and one of the groups is a triplet
                groups.size == 3 && groups.containsValue(3)
            }
    }
}

class TwoPairs(hand: List<Card>) : PokerHand(hand) {

    val valueGroups = hand.valueGroups()

    override val rank: Int = 3

    override fun computeWeight(): Int =
        hand.valueGroups().entries.sortedBy { it.key }.foldIndexed(0) { index, acc, entry ->
            acc + if (entry.value == 2) {
                entry.key * 15.pow(index) * 100
            } else {
                entry.key
            }
        }

    companion object {
        @JvmStatic
        fun check(cards: List<Card>): Boolean =
            cards.valueGroups().let { groups ->
                // three different card values and any group is a pair -> other two groups have 1 and 2 cards respectively
                groups.size == 3 && groups.containsValue(2)
            }
    }
}

class Pair(hand: List<Card>) : PokerHand(hand) {

    private val valueGroups = hand.valueGroups()

    override val rank: Int = 2

    override fun computeWeight(): Int =
        valueGroups.entries.sortedBy { it.key }.foldIndexed(0) { index, acc, entry ->
            acc + if (entry.value == 2) {
                entry.key * 15.pow(5) * 100
            } else {
                entry.key * 15.pow(index)
            }
        }

    companion object {
        @JvmStatic
        fun check(cards: List<Card>): Boolean =
            cards.valueGroups().size == 4
    }
}

class HighCard(hand: List<Card>) : PokerHand(hand) {

    override val rank: Int = 1

    override fun computeWeight(): Int =
        hand.sortedBy { it.value }.foldIndexed(0) { index, acc, entry ->
            acc + entry.value * 15.pow(index)
        }

    companion object {
        @JvmStatic
        fun check(cards: List<Card>): Boolean =
            cards.valueGroups().size == HAND_COUNT
    }
}

/**
 * @return a map of card value to count
 */
private fun List<PokerHand.Card>.valueGroups() =
    groupingBy { it.value }.eachCount().toSortedMap()

private fun Int.pow(power: Int) =
    toDouble().pow(power).roundToInt()