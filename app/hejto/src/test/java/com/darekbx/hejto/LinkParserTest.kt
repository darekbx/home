package com.darekbx.hejto

import com.darekbx.hejto.utils.LinkParser
import junit.framework.TestCase.assertEquals
import org.junit.Test

class LinkParserTest {

    @Test
    fun `link is parsed`() {
        // Given
        val linkString = "[Klaudiusz](https://imperiumromanum.pl/biografie/klaudiusz/)"

        // When
        val link = LinkParser.parseLink(linkString)

        // Then
        assertEquals("Klaudiusz", link.label)
        assertEquals("https://imperiumromanum.pl/biografie/klaudiusz/", link.url)
    }

    @Test
    fun `extracted 3 links`() {
        // Given
        val content = "Tego dnia w Rzymie\n\nTego dnia, 41 n.e. – [Klaudiusz](https://imperiumromanum.pl/biografie/klaudiusz/) został cesarzem rzymskim. Po zamordowaniu [Kaliguli](https://imperiumromanum.pl/biografie/cesarz-kaligula/) w powstałym zamieszaniu część żołnierzy gwardii pretoriańskiej zdecydowała się na obwołanie cesarzem Klaudiusza, Ponadto był ostatnim znanym człowiekiem władającym językiem etruskim.\n\n[#liganauki](/tag/liganauki)\n\n  \n\n#antycznyrzym #imperiumromanum #tegodniawrzymie #wydarzenia #gruparatowaniapoziomu #historia #tegodnia #tegodniawrzymie #ancientrome #rzym #ciekawostki #venividivici #liganauki"

        // When
        val links = LinkParser.extractLinks(content)

        // Then
        assertEquals(3, links.size)

        with(links[0]) {
            assertEquals("Klaudiusz", label)
            assertEquals("https://imperiumromanum.pl/biografie/klaudiusz/", url)
            assertEquals(41, start)
            assertEquals(101, end)
        }

        assertEquals("Kaliguli", links[1].label)
        assertEquals("https://imperiumromanum.pl/biografie/cesarz-kaligula/", links[1].url)

        assertEquals("#liganauki", links[2].label)
        assertEquals("/tag/liganauki", links[2].url)
    }

    @Test
    fun `extract long content`() {
        // Given
        val content = "[#techpigulka](https://www.hejto.pl/tag/techpigulka) (25.01.2023)\n\n  \n\n**Stoimy na progu rewolucji od której nie ma odwrotu :globe_with_meridians:**\n\n  \n\n**News:**\n\n  \n\n:large_orange_circle: [**Strava doczeka się trójwymiarowych map**](https://techcrunch.com/2023/01/24/strava-acquires-fatmap-a-3d-map-platform-for-the-great-outdoors/) \n\n  \n\nStrava oficjalnie przejęła Fatmap - europejską firmę, które buduje platformę map 3D. Szczegóły transakcji nie zostały ujawnione. W ciągu ostatniej dekady Fatmap współpracowała z przedsiębiorstwami z branży satelitarnej i lotniczej - wzbogacając swoje mapy o szczyty, rzeki, przełęcze, ścieżki, schroniska i wiele innych szczegółów. Trzeba przyznać, że jakość obrazowania, którą oferuje Fatmap wygląda naprawdę dobrze. \n\n  \n\n:large_orange_circle: [**Zapotrzebowanie na umiejętności związane z AI stale rośnie**](https://venturebeat.com/ai/demand-for-ai-skills-on-the-rise-as-fiverr-searches-spike-for-freelancers/) \n\n  \n\nSztuczna inteligencja znajduje coraz więcej zastosowań w segmencie biznesowym. Okazuje się, że zapotrzebowanie na osoby potrafiące korzystać z platform wykorzystujących AI stale rośnie. Raport Fiverr zdradza, że w ciągu ostatnich sześciu miesięcy liczba ofert dla freelancerów zaznajomionych z AI wzrosła o ponad 1400 procent. \n\n  \n\n:large_orange_circle: [**Aplikacja Xbox trafia na starsze telewizory Samsunga**](https://www.komputerswiat.pl/aktualnosci/sprzet/aplikacja-xbox-trafia-na-starsze-telewizory-samsunga/d92q46b) \n\n  \n\nKoreańczycy rozszerzają kompatybilność aplikacji od Microsoftu na swoich telewizorach. Tym razem aplikacja Xbox trafi na urządzenia wprowadzone na rynek w 2021 roku. Co to oznacza? A no to, że posiadacze abonamentu Xbox Game Pass będą mogli skorzystać z cyfrowej rozrywki na swoich smart TV bez konieczności posiadania konsoli. Wystarczy tylko kompatybilny kontroler i szybkie połączenie z siecią. \n\n  \n\n:large_orange_circle: [**Rząd rozda uczniom darmowe laptopy**](https://www.wirtualnemedia.pl/artykul/laptopy-darmowe-dla-uczniow-iv-klasa-wrzesien-2023-rok-jak-zdobyc) \n\n  \n\nUczniowe IV klasy otrzymają laptopy, które mają służyć im w kolejnych latach edukacji - sprzęty trafią nawet do 370 tysięcy osób. Cały program ma na celu wyrównanie nierówności i wspieranie talentów. Jak dla mnie to jednak nic innego, jak początek kiełbasy wyborczej. \n\n  \n\n:large_orange_circle: [**NASA i DARPA testują napęd, który pozwoli szybciej dolecieć na Marsa**](https://www.engadget.com/nasa-darpa-nuclear-thermal-engine-crewed-missions-mars-200827580.html)\n\n  \n\nNASA przy współpracy z DARPA rozpoczęła testy nowego napędu, który ma pozwolić astronautom szybciej dolecieć na Marsa. Technologia ma zostać oficjalnie zaprezentowana już w 2027 roku. Inżynierowie z nadzieją spoglądają w kierunku napędu jądrowego. Jego rozwój niesie za sobą kilka potencjalnych zagrożeń. Jeśli projekt NASA wypali, to czeka nas prawdziwa rewolucja w zakresie eksploracji tak zwanego “głębokiego” kosmosu.\n\n  \n\n**Longform:**\n\n  \n\n:large_yellow_circle: VR i AR to technologie rozwijane od lat. Dlaczego nie możemy doczekać momentu, w którym w końcu wyjdą z niszy? ([czytaj dalej](https://www.matthewball.vc/all/why-vrar-gets-farther-away-as-it-comes-into-focus))\n\n  \n\n:large_yellow_circle: Czy AGI ma szansę realnie powstać? Świetny wywiad z Gradym Boochem ([czytaj dalej](https://garymarcus.substack.com/p/agi-will-not-happen-in-your-lifetime))\n\n  \n\n**Bonus:** Fantastyczna strona z gierkami retro działająca w przeglądarce. Sugerowałbym nie odpalać w pracy, bo… nie tylko działa nostalgicznie, ale też może pożreć mnóstwo czasu. ([klik](https://playclassic.games/))\n\n  \n\n.\n\n  \n\nInteresują Cię podobne treści? Chciałbym zaprosić Cię więc do obserwacji tagu [#techpigulka](https://www.hejto.pl/tag/techpigulka) . W każdy poniedziałek, środę i piątek podsumowuję w krótkiej formie ważne wydarzenia ze świata [#technologia](https://www.hejto.pl/tag/technologia) [#biznes](https://www.hejto.pl/tag/biznes) i [#nauka](https://www.hejto.pl/tag/nauka) . Z poszanowainem dla Twojego czasu. Bez bullshitu.\n\n[#nauka](https://www.hejto.pl/tag/nauka) [#technologia](https://www.hejto.pl/tag/technologia) [#ciekawostki](https://www.hejto.pl/tag/ciekawostki) [#biznes](https://www.hejto.pl/tag/biznes)"

        // When
        val links = LinkParser.extractLinks(content)

        // Then
        assertEquals(17, links.size)
    }

    @Test
    fun `content without links`() {
        // Given
        val content = "Tego dnia w Rzymie\n\nTego dnia, 41 n.e. – Zdecydowała się na obwołanie cesarzem Klaudiusza, Ponadto był ostatnim znanym człowiekiem władającym językiem (PL) etruskim. \n\n#antycznyrzym #imperiumromanum #tegodniawrzymie #wydarzenia #gruparatowaniapoziomu [test] #historia #tegodnia #tegodniawrzymie #ancientrome #rzym #ciekawostki #venividivici #liganauki"

        // When
        val links = LinkParser.extractLinks(content)

        // Then
        assertEquals(0, links.size)
    }

    @Test
    fun `one line content without links`() {
        // Given
        val content = "#liganauki"

        // When
        val links = LinkParser.extractLinks(content)

        // Then
        assertEquals(0, links.size)
    }

    @Test
    fun `empty content`() {
        // Given
        val content = ""

        // When
        val links = LinkParser.extractLinks(content)

        // Then
        assertEquals(0, links.size)
    }
}