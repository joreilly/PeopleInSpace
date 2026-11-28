/**
 * Bios for people whose The Space Devs entry has none, or only a stand-in such as
 * "(Placeholder)" (new crew are often listed before their bio is written), keyed by name as
 * The Space Devs spells it. Remove an entry once The Space Devs has a real bio.
 */
internal val supplementalBios = mapOf(
    // Sources: Hong Kong government and university announcements, 23 May 2026.
    "Lai Ka-ying" to "Lai Ka-ying is Hong Kong's first astronaut and China's fourth female astronaut. " +
        "A superintendent in the Hong Kong Police Force with a doctorate in computer forensics, she " +
        "was selected as a payload specialist in China's fourth group of astronauts in 2024 and " +
        "launched to the Tiangong space station on Shenzhou-23 in May 2026.",
)

private val placeholderBios = setOf("", "(placeholder)", "placeholder", "tbd", "n/a")

/** [bio] if it's a real one, otherwise the supplemental bio for [name], or "" if there's none. */
internal fun bioFor(name: String, bio: String?): String =
    bio?.takeUnless { it.trim().lowercase() in placeholderBios } ?: supplementalBios[name].orEmpty()
