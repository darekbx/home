package com.darekbx.rssreader.data

import androidx.annotation.DrawableRes
import com.darekbx.rssreader.R

sealed class NewsSource(val name: String, val url: String, @DrawableRes val icon: Int) {
    class TuStolicaBemowo : NewsSource("Tu stolica - Bemowo", "https://tustolica.pl/feed.php?echo/bem", R.drawable.tostolica_logo)
    class TuStolicaWola : NewsSource("Tu stolica - Wola", "https://tustolica.pl/feed.php?echo/wol", R.drawable.tostolica_logo)
    class TuStolicaMokotow : NewsSource("Tu stolica - Mokotów", "https://tustolica.pl/feed.php?echo/mok", R.drawable.tostolica_logo)
    class TuStolicaBielany : NewsSource("Tu stolica - Bielany", "https://tustolica.pl/feed.php?echo/bie", R.drawable.tostolica_logo)
    class TuStolicaWawer : NewsSource("Tu stolica - Wawer", "https://tustolica.pl/feed.php?echo/iwa", R.drawable.tostolica_logo)
    class TuStolicaBialoleka : NewsSource("Tu stolica - Białołęka", "https://tustolica.pl/feed.php?echo/bia", R.drawable.tostolica_logo)
    class TuStolicaPlus : NewsSource("Tu stolica - Plus", "https://tustolica.pl/feed.php?puls", R.drawable.tostolica_logo)
    class YCombinator : NewsSource("News Y Combinator", "https://news.ycombinator.com/rss", R.drawable.news_y_logo)
    class Hackaday : NewsSource("Hackaday", "https://hackaday.com/blog/feed/", R.drawable.hackaday_logo)
    class Arduino : NewsSource("Arduino", "https://blog.arduino.cc/feed/", R.drawable.arduino_logo)
    class Niebezpiecznik : NewsSource("Niebezpiecznik", "https://feeds.feedburner.com/niebezpiecznik/tylko-glowna/", R.drawable.niebezpiecznik_logo)
    class Forbot : NewsSource("Forbot", "https://forbot.pl/blog/kanal-rss", R.drawable.forbot_logo)
}
