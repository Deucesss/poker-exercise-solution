import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertTrue

class PokerHandTest {

    private val hands =
        mapOf(
            HIGH_CARD to "AC 9D TH 3S 4D",
            PAIR to "AC AD TH 3S 4D",
            TWO_PAIRS to "AC AD TH 3S 3D",
            THREE_OF_A_KIND to "AC AD AH 3S 4D",
            STRAIGHT to "9H TH JH QH KC",
            FLUSH to "6H 3H JH 7H KH",
            FULL_HOUSE to "AC AD AH 3S 3D",
            FOUR_OF_A_KIND to "AC AD AH AS 3D",
            STRAIGHT_FLUSH to "9H TH JH QH KH",
            ROYAL_FLUSH to "AH TH JH QH KH"
        )

    @Test
    fun testIsHighCard() {
        assertTrue(PokerHand.determine(hands[HIGH_CARD]!!) is HighCard)
    }

    @Test
    fun testIsPair() {
        assertTrue(PokerHand.determine(hands[PAIR]!!) is Pair)
    }

    @Test
    fun testIsTwoPairs() {
        assertTrue(PokerHand.determine(hands[TWO_PAIRS]!!) is TwoPairs)
    }

    @Test
    fun testIsThreeOfAKind() {
        assertTrue(PokerHand.determine(hands[THREE_OF_A_KIND]!!) is ThreeOfAKind)
    }

    @Test
    fun testIsStraight() {
        assertTrue(PokerHand.determine(hands[STRAIGHT]!!) is Straight)
    }

    @Test
    fun testIsFlush() {
        assertTrue(PokerHand.determine(hands[FLUSH]!!) is Flush)
    }

    @Test
    fun testIsFullHouse() {
        assertTrue(PokerHand.determine(hands[FULL_HOUSE]!!) is FullHouse)
    }

    @Test
    fun testIsFourOfAKind() {
        assertTrue(PokerHand.determine(hands[FOUR_OF_A_KIND]!!) is FourOfAKind)
    }

    @Test
    fun testIsStraightFlush() {
        assertTrue(PokerHand.determine(hands[STRAIGHT_FLUSH]!!) is StraightFlush)
    }

    @Test
    fun testIsRoyalFlush() {
        assertTrue(PokerHand.determine(hands[ROYAL_FLUSH]!!) is RoyalFlush)
    }

    @Test
    fun testDifferentTypeRank() {
        assertAll(
            { assertTrue(PokerHand.determine(hands[HIGH_CARD]!!) < PokerHand.determine(hands[PAIR]!!)) },
            { assertTrue(PokerHand.determine(hands[PAIR]!!) < PokerHand.determine(hands[TWO_PAIRS]!!)) },
            { assertTrue(PokerHand.determine(hands[TWO_PAIRS]!!) < PokerHand.determine(hands[THREE_OF_A_KIND]!!)) },
            { assertTrue(PokerHand.determine(hands[THREE_OF_A_KIND]!!) < PokerHand.determine(hands[STRAIGHT]!!)) },
            { assertTrue(PokerHand.determine(hands[STRAIGHT]!!) < PokerHand.determine(hands[FLUSH]!!)) },
            { assertTrue(PokerHand.determine(hands[FLUSH]!!) < PokerHand.determine(hands[FULL_HOUSE]!!)) },
            { assertTrue(PokerHand.determine(hands[FULL_HOUSE]!!) < PokerHand.determine(hands[FOUR_OF_A_KIND]!!)) },
            { assertTrue(PokerHand.determine(hands[FOUR_OF_A_KIND]!!) < PokerHand.determine(hands[STRAIGHT_FLUSH]!!)) },
            { assertTrue(PokerHand.determine(hands[STRAIGHT_FLUSH]!!) < PokerHand.determine(hands[ROYAL_FLUSH]!!)) }
        )
    }

    @Test
    fun testHighCardComparison() {
        val handA = "2C 3C 8C 9H TD"
        val handB = "5D 6D 7D 9D TH"
        val handC = "AD QD 2H 3H 4H"
        val handD = "AH JD TD 9D 8D"
        assertAll(
            { assertTrue(PokerHand.determine(handA) is HighCard) },
            { assertTrue(PokerHand.determine(handB) is HighCard) },
            { assertTrue(PokerHand.determine(handA) > PokerHand.determine("5D 6D 7D 9D TH")) },

            { assertTrue(PokerHand.determine(handC) is HighCard) },
            { assertTrue(PokerHand.determine(handD) is HighCard) },
            { assertTrue(PokerHand.determine(handC) > PokerHand.determine(handD)) }
        )
    }

    @Test
    fun testPairComparison() {
        val handA = "2C 2D KH QD JH"
        val handB = "2H 2S AD 5C 4D"
        assertAll(
            { assertTrue(PokerHand.determine(handA) is Pair) },
            { assertTrue(PokerHand.determine(handB) is Pair) },
            { assertTrue(PokerHand.determine(handA) < PokerHand.determine(handB)) }
        )
    }

    @Test
    fun testTwoPairsComparison() {
        val handA = "2C 2D KH KD JH"
        val handB = "2H 2S AD AC TD"
        val handC = "2H 2S KC KS QH"
        assertAll(
            { assertTrue(PokerHand.determine(handA) is TwoPairs) },
            { assertTrue(PokerHand.determine(handB) is TwoPairs) },
            { assertTrue(PokerHand.determine(handC) is TwoPairs) },
            { assertTrue(PokerHand.determine(handA) < PokerHand.determine(handB)) },
            { assertTrue(PokerHand.determine(handA) < PokerHand.determine(handB)) }
        )
    }

    @Test
    fun testThreeOfAKindComparison() {
        val handA = "3C 3D 3H KD AH"
        val handB = "4C 4D 4H 2C 3D"
        assertAll(
            { assertTrue(PokerHand.determine(handA) is ThreeOfAKind) },
            { assertTrue(PokerHand.determine(handB) is ThreeOfAKind) },
            { assertTrue(PokerHand.determine(handA) < PokerHand.determine(handB)) }
        )
    }

    @Test
    fun testStraightComparison() {
        val handA = "4H 5H 6H 7H 8D"
        val handB = "5D 6D 7D 8D 9H"
        assertAll(
            { assertTrue(PokerHand.determine(handA) is Straight) },
            { assertTrue(PokerHand.determine(handB) is Straight) },
            { assertTrue(PokerHand.determine(handA) < PokerHand.determine(handB)) }
        )
    }

    @Test
    fun testFlushComparison() {
        val handA = "2C 3C 8C 9C TC"
        val handB = "5D 6D 7D 9D TD"
        assertAll(
            { assertTrue(PokerHand.determine(handA) is Flush) },
            { assertTrue(PokerHand.determine(handB) is Flush) },
            { assertTrue(PokerHand.determine(handA) > PokerHand.determine(handB)) }
        )
    }

    @Test
    fun testFullHouseComparison() {
        val handA = "4C 4D 4H AC AD"
        val handB = "5D 5H 5S 9D 9H"
        assertAll(
            { assertTrue(PokerHand.determine(handA) is FullHouse) },
            { assertTrue(PokerHand.determine(handB) is FullHouse) },
            { assertTrue(PokerHand.determine(handA) < PokerHand.determine(handB)) }
        )
    }

    @Test
    fun testFourOfAKindComparison() {
        val handA = "4C 4D 4H 4S AD"
        val handB = "5D 5H 5S 5C 2H"
        assertAll(
            { assertTrue(PokerHand.determine(handA) is FourOfAKind) },
            { assertTrue(PokerHand.determine(handB) is FourOfAKind) },
            { assertTrue(PokerHand.determine(handA) < PokerHand.determine(handB)) }
        )
    }

    @Test
    fun testStraightFlushComparison() {
        val handA = "2C 3C 4C 5C 6C"
        val handB = "5D 6D 7D 8D 9D"
        assertAll(
            { assertTrue(PokerHand.determine(handA) is StraightFlush) },
            { assertTrue(PokerHand.determine(handB) is StraightFlush) },
            { assertTrue(PokerHand.determine(handA) < PokerHand.determine(handB)) }
        )
    }


    companion object {
        const val HIGH_CARD = "high card"
        const val PAIR = "pair"
        const val TWO_PAIRS = "two_pairs"
        const val THREE_OF_A_KIND = "three_of_a_kind"
        const val STRAIGHT = "straight"
        const val FLUSH = "flush"
        const val FULL_HOUSE = "full_house"
        const val FOUR_OF_A_KIND = "four of a kind"
        const val STRAIGHT_FLUSH = "straight_flush"
        const val ROYAL_FLUSH = "royal_flush"
    }
}