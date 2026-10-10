import dev.johnoreilly.common.remote.Assignment


// from https://ll.thespacedevs.com/2.2.0/astronaut/?format=json&in_space=true&is_human=true
val currentPeopleInSpace = listOf(
    Assignment(
        craft = "ISS",
        name = "Christopher Williams",
        personImageUrl ="https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/255bauto255d__image_thumbnail_20240305190450.jpeg",
        personBio = "Christopher Williams grew up in Potomac, Maryland and graduated from Stanford University in 2005 with a bachelor’s degree in physics and a doctorate in physics from MIT in 2012, where his research was in astrophysics. Williams is a board-certified medical physicist, completing his residency training at Harvard Medical School before joining the faculty as a clinical physicist and researcher. He most recently worked as a medical physicist in the Radiation Oncology Department at the Brigham and Women's Hospital and Dana-Farber Cancer Institute in Boston. He was the lead physicist for the Institute’s MRI-guided adaptive radiation therapy program. His research focused on developing image guidance techniques for cancer treatments.",
        nationality = "American",
    ),
    Assignment(
        craft = "ISS",
        name = "Sergey Kud-Sverchkov",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/255bauto255d__image_thumbnail_20240305191157.jpeg",
        personBio = "Sergey Vladimirvich Kud-Sverchkov Is a Kazakh-Russian engineer and cosmonaut selected in 2010.",
        nationality = "Russian",
    ),
    Assignment(
        craft = "ISS",
        name = "Sergey Mikayev",
        personImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Roscosmos_cosmonaut_Sergey_Mikaev_poses_for_a_portrait_at_NASA%27s_Johnson_Space_Center_%28jsc2024e077064_alt%29.jpg/960px-Roscosmos_cosmonaut_Sergey_Mikaev_poses_for_a_portrait_at_NASA%27s_Johnson_Space_Center_%28jsc2024e077064_alt%29.jpg",
        personBio = "Sergey Mikayev is a Russian cosmonaut affiliated with Roscosmos",
        nationality = "Russian",
    ),

    Assignment(
        craft = "ISS",
        name = "Jessica Meir",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/jessica_meir_image_20200417064900.jpeg",
        personBio = "Jessica Ulrika Meir is Assistant Professor of Anesthesia at Harvard Medical School, Massachusetts General Hospital, Boston, following postdoctoral research in comparative physiology at the University of British Columbia. She has studied the diving physiology and behavior of emperor penguins in Antarctica, and the physiology of bar-headed geese, which are able to migrate over the Himalayas. In 2000, Meir graduated with a Master of Space Studies from the International Space University in Strasbourg, France. In September 2002, Meir served as an aquanaut on the NASA Extreme Environment Mission Operations 4 (NEEMO 4) crew. In June 2013 she was named an astronaut candidate by NASA, becoming one of the eight members of NASA Astronaut Group 21. She is from Caribou, Maine.",
        nationality = "American",
    ),
    Assignment(
        craft = "ISS",
        name = "Jack Hathaway",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/jack_hathaway_image_20211206185407.jpg",
        personBio = "Jack Hathaway is a US Navy commander from Connecticut. He earned bachelors’ degrees in physics and history from the U.S. Naval Academy and completed graduate studies at Cranfield University in England and the U.S. Naval War College. A distinguished naval aviator, Hathaway flew and deployed with Navy’s Strike Fighter Squadron 14 aboard the USS Nimitz and Strike Fighter Squadron 136 aboard the USS Truman. He graduated from Empire Test Pilots’ School, supported the Joint Chiefs of Staff at the Pentagon, and was most recently assigned as the prospective executive officer for Strike Fighter Squadron 81. He has more than 2,500 flight hours in 30 types of aircraft, more than 500 carrier arrested landings, and flew 39 combat missions.",
        nationality = "American",
    ),
    Assignment(
        craft = "ISS",
        name = "Sophie Adenot",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/sophie_adenot_image_20221123150954.jpg",
        personBio = "Between 2001 and 2003, Sophie studied engineering and graduated from ISAE-SUPAERO in Toulouse, France, where she specialised in spacecraft and aircraft flight dynamics. She then completed a Master of Science in human factors engineering at MIT – Massachusetts Institute of Technology in Boston, USA, in 2004.\\r\\nIn 2005, Sophie joined the French Air Force for her basic military training and initial flying training to become a helicopter pilot.  \\r\\n\\r\\nIn 2018, she graduated with honours as a helicopter test pilot from the Empire Test Pilots’ School in Boscombe Down, UK. She was awarded the Mac Kenna trophy and the Patuxtent Shield.\\r\\nBetween 2019 and 2022, Sophie worked as a helicopter experimental test pilot in Cazaux Flight Test Center with DGA (Direction Générale de l’Armement – the French Defence Procurement Agency). She has logged 3000 hours and flew on 22 different helicopter types. She also holds a military parachute license, a light aircraft pilot license and a glider pilot license.",
        nationality = "French",
    ),
    Assignment(
        craft = "ISS",
        name = "Andrei Fedyaev",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/andrei_fedyaev_image_20230207203113.jpg",
        personBio = "Andrei Valerievich Fedyaev (Андрей Валерьевич Федяев) is a Russian cosmonaut. He was a military pilot prior to retiring from Air Force in 2013, and was selected as a cosmonaut in 2012. \\r\\n\\r\\nISS-69 expedition will be Fedyaev's first spaceflight.",
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
        name = "Luke Delaney",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/255bauto255d__image_thumbnail_20240305190723.jpeg",
        personBio = "Luke Delaney is a retired US Marine Corps major who grew up in Debary, Florida. He holds a degree in mechanical engineering from University of North Florida and a master’s degree in aerospace engineering from the Naval Postgraduate School. He is a distinguished naval aviator who participated in exercises throughout the Asia Pacific region and conducted combat missions in support of Operation Enduring Freedom. As a test pilot, he executed numerous flights evaluating weapon systems integration, and he served as a test pilot instructor. Delaney most recently worked as a research pilot at NASA’s Langley Research Center, in Hampton, Virginia, where he supported airborne science missions. Including his NASA career, Delaney logged more than 3,700 flight hours on 48 models of jet, propeller, and rotary wing aircraft.",
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
        name = "Sergey Teteryatnikov",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/sergey_teteryat_image_thumbnail_20260424073411.jpeg",
        personBio = "Sergey Teteryatnikov graduated from the Naval Academy, St. Petersburg, Russia, in 2011 as an engineer specializing in ship power plant operations. Before his selection as a test cosmonaut, Teteryatnikov served in various naval engineering roles, including undersea vessels and specialized engine room operations. He was selected for the Gagarin Research and Test Cosmonaut Training Center Cosmonaut Corps in 2021 and has served as a test cosmonaut since 2023.",
        nationality = "Russian",
    ),


    Assignment(
        craft = "Tiangong",
        name = "Zhang Lu",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/zhang_lu_offici_image_thumbnail_20251030081918.jpeg",
        personBio = "Zhang Lu is a Chinese pilot selected in 2010 to be an astronaut.",
        nationality = "Chinese",
    ),
    Assignment(
        craft = "Tiangong",
        name = "Wu Fei",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/fei_wu_image_thumbnail_20251030081821.jpeg",
        personBio = "Wu Fei is a Chinese astronaut selected as part of the Shenzhou program. He enlisted in the People's Liberation Army (PLA) Air Force in January 2021, and joined the Chinese Communist Party (CCP) in October 2015. He was a engineer at the China Academy of Space Technology (CAST) prior to joining the astronaut corps.",
        nationality = "Chinese",
    ),
    Assignment(
        craft = "Tiangong",
        name = "Hongzhang Zhang",
        personImageUrl = "https://thespacedevs-prod.nyc3.digitaloceanspaces.com/media/images/hongzhang_zhang_image_thumbnail_20251030081806.jpeg",
        personBio = "Zhang Hongzhang is a Chinese astronaut selected as a payload specialist as part of the Shenzhou program. He joined the Chinese Communist Party (CCP) in August 2004. He was a researcher on battery technology at the Chinese Academy of Sciences (CAS) Dalian Institute of Chemical Physics prior to joining the astronaut corps.",
        nationality = "Chinese",
    ),

)