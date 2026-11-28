import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SupplementalBiosTest {
    @Test
    fun keepsARealBio() {
        assertEquals("Geologist.", bioFor("Lai Ka-ying", "Geologist."))
    }

    @Test
    fun replacesAPlaceholderBioWithTheSupplementalOne() {
        val expected = supplementalBios.getValue("Lai Ka-ying")
        assertEquals(expected, bioFor("Lai Ka-ying", "(Placeholder)"))
        assertEquals(expected, bioFor("Lai Ka-ying", " (placeholder) "))
        assertEquals(expected, bioFor("Lai Ka-ying", null))
        assertEquals(expected, bioFor("Lai Ka-ying", ""))
    }

    @Test
    fun dropsAPlaceholderBioWhenThereIsNoSupplementalOne() {
        assertEquals("", bioFor("Unknown Person", "(Placeholder)"))
        assertEquals("", bioFor("Unknown Person", null))
    }

    @Test
    fun fallbackHasARealBioForEveryone() {
        assertEquals(13, currentPeopleInSpace.size)
        currentPeopleInSpace.forEach {
            val bio = it.personBio.orEmpty()
            assertTrue(bio.isNotBlank() && bio != "(Placeholder)", "${it.name} has no bio")
        }
    }
}
