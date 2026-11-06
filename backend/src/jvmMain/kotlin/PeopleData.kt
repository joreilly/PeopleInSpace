import dev.johnoreilly.common.remote.Assignment

// Fallback used when The Space Devs API can't be reached (see SpaceDevsPeopleRepository).
// Snapshot taken 2026-11-06 from https://ll.thespacedevs.com/2.2.0/astronaut/?in_space=true&is_human=true
val currentPeopleInSpace = listOf(
    Assignment(
        craft = "ISS",
        name = "Anil Menon",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/255bauto255d__image_thumbnail_20240305190559.jpeg",
        personBio = "Anil Menon is a US Air Force lieutenant colonel from Minneapolis, Minnesota. He was SpaceX’s first flight surgeon, helping to launch the company’s first humans to space during NASA’s SpaceX Demo-2 mission and building a medical organization to support the human system during future missions. Prior to that, he served NASA as the crew flight surgeon for various expeditions taking astronauts to the International Space Station. Menon is an actively practicing emergency medicine physician with fellowship training in wilderness and aerospace medicine. As a physician, he was a first responder during the 2010 earthquake in Haiti, 2015 earthquake in Nepal, and the 2011 Reno Air Show accident. In the Air Force, Menon supported the 45th Space Wing as a flight surgeon and the 173rd Fighter Wing, where he logged over 100 sorties in the F-15 fighter jet and transported over 100 patients as part of the critical care air transport team.\r\n\r\nExact date of birth unknown.",
        nationality = "American",
    ),
    Assignment(
        craft = "ISS",
        name = "Anna Kikina",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/255bauto255d__image_thumbnail_20240305185904.jpeg",
        personBio = "Anna Yuryevna Kikina is a Russian engineer and test cosmonaut, selected in 2012. Kikina is assigned to Crew-5 mission which is to launch in 2022. The seat was arranged as a part of exchange agreement between Roscosmos and NASA, allowing Russian cosmonaut to fly on US vehicle while American astronaut gets a seat on Soyuz. Kikina will be the first Russian cosmonaut to fly on Crew Dragon.",
        nationality = "Russian",
    ),
    Assignment(
        craft = "ISS",
        name = "Jessica Watkins",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/255bauto255d__image_thumbnail_20240305190244.jpeg",
        personBio = "Jessica Andrea Watkins is an American geologist, former international rugby player, and a NASA astronaut of the class of 2017.",
        nationality = "American",
    ),
    Assignment(
        craft = "ISS",
        name = "Joshua Kutryk",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/255bauto255d__image_thumbnail_20240305185947.jpeg",
        personBio = "Joshua Kutryk, a Royal Canadian Air Force lieutenant colonel, is from Beauvallon, Alberta. He has a bachelor’s degree in mechanical engineering, as well as master’s degrees in space studies, flight test engineering, and defense studies. Prior to joining CSA, Kutryk worked as an experimental test pilot and a fighter pilot in Cold Lake, Alberta, where he led the unit responsible for the operational flight-testing of fighter aircraft in Canada.",
        nationality = "Canadian",
    ),
    Assignment(
        craft = "ISS",
        name = "Luke Delaney",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/255bauto255d__image_thumbnail_20240305190723.jpeg",
        personBio = "Luke Delaney is a retired US Marine Corps major who grew up in Debary, Florida. He holds a degree in mechanical engineering from University of North Florida and a master’s degree in aerospace engineering from the Naval Postgraduate School. He is a distinguished naval aviator who participated in exercises throughout the Asia Pacific region and conducted combat missions in support of Operation Enduring Freedom. As a test pilot, he executed numerous flights evaluating weapon systems integration, and he served as a test pilot instructor. Delaney most recently worked as a research pilot at NASA’s Langley Research Center, in Hampton, Virginia, where he supported airborne science missions. Including his NASA career, Delaney logged more than 3,700 flight hours on 48 models of jet, propeller, and rotary wing aircraft.\r\n\r\nExact date of birth unknown.",
        nationality = "American",
    ),
    Assignment(
        craft = "ISS",
        name = "Pyotr Dubrov",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/255bauto255d__image_thumbnail_20240305185922.jpeg",
        personBio = "Dubrov was selected by Roscosmos as a cosmonaut on 8 October 2012, as one of eight cosmonauts selected as part of Roscosmos's 2012 selection group.\r\nIn 2020 he was assigned to the backup crew of Soyuz MS-17, backing up Russian cosmonaut Sergey Kud-Sverchkov as Flight Engineer on ISS Expedition 63/64",
        nationality = "Russian",
    ),
    Assignment(
        craft = "ISS",
        name = "Sergey Teteryatnikov",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/sergey_teteryat_image_thumbnail_20260424073411.jpeg",
        personBio = "Sergey Teteryatnikov graduated from the Naval Academy, St. Petersburg, Russia, in 2011 as an engineer specializing in ship power plant operations. Before his selection as a test cosmonaut, Teteryatnikov served in various naval engineering roles, including undersea vessels and specialized engine room operations. He was selected for the Gagarin Research and Test Cosmonaut Training Center Cosmonaut Corps in 2021 and has served as a test cosmonaut since 2023.",
        nationality = "Russian",
    ),
    Assignment(
        craft = "Tiangong",
        name = "Lai Ka-ying",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/lai_ka-ying_off_image_thumbnail_20260524130423.png",
        personBio = "(Placeholder)",
        nationality = "Chinese",
    ),
    Assignment(
        craft = "Tiangong",
        name = "Zhang Zhiyuan",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/zhang_zhiyuan_o_image_thumbnail_20260524125922.png",
        personBio = "Zhang Zhiyuan is a Chinese astronaut selected as part of the Shenzhou program. He enlisted in the People's Liberation Army (PLA) Air Force in September 2006, and joined the Chinese Communist Party (CCP) in March 2011. He was a pilot at the People's Liberation Army Air Force (PLAAF) prior to joining the astronaut corps.",
        nationality = "Chinese",
    ),
    Assignment(
        craft = "Tiangong",
        name = "Zhu Yangzhu",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/255bauto255d__image_thumbnail_20240305185258.jpeg",
        personBio = "Zhu Yangzhu is a Chinese pilot and astronaut selected as part of the Shenzhou program. He enlisted in the People's Liberation Army (PLA) in September 2005, and joined the Chinese Communist Party (CCP) in December 2006.",
        nationality = "Chinese",
    ),
)
