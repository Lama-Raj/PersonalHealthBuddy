package com.unh.personal_health_buddy.features

import android.util.Log
import com.unh.personal_health_buddy.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


/**
 * Fetches health news articles using the NewsAPI service (https://newsapi.org/).
 * The API key, implementation, and behavior were created with help from
 * ChatGPT, Gemini, and the official NewsAPI documentation (https://newsapi.org/docs).
 */


data class NewsArticle(
        val title: String,
        val description: String?,
        val url: String,
        val urlToImage: String?
)

// Fetch top health news from NewsAPI (runs on IO dispatcher)
suspend fun fetchHealthNews(): List<NewsArticle> {
        return withContext(Dispatchers.IO) {
                try {
                        val apiKey = BuildConfig.NEWS_API_KEY
                        val urlString = "https://newsapi.org/v2/top-headlines?country=us&category=health&apiKey=$apiKey"

                        val url = URL(urlString)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        // Important: NewsAPI often requires a User-Agent to be set
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                        connection.connectTimeout = 5000
                        connection.readTimeout = 5000

                        val responseCode = connection.responseCode
                        if (responseCode == 200) {
                                val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                                val jsonObject = JSONObject(jsonString)

                                if (!jsonObject.has("articles")) return@withContext emptyList()

                                val articlesArray = jsonObject.getJSONArray("articles")
                                val articles = mutableListOf<NewsArticle>()

                                for (i in 0 until articlesArray.length()) {
                                        val articleJson = articlesArray.getJSONObject(i)
                                        val title = articleJson.optString("title")

                                        // skip placeholder/empty titles
                                        if (title == "[Removed]" || title.isEmpty()) continue

                                        articles.add(
                                                NewsArticle(
                                                        title = title,
                                                        description = articleJson.optString("description"),
                                                        url = articleJson.optString("url"),
                                                        urlToImage = if (articleJson.has("urlToImage") && !articleJson.isNull("urlToImage"))
                                                                articleJson.getString("urlToImage") else null
                                                )
                                        )
                                }
                                articles
                        } else {
                                Log.e("HealthNews", "Error fetching news. Response Code: $responseCode")
                                emptyList()
                        }
                } catch (e: Exception) {
                        Log.e("HealthNews", "Exception fetching news", e)
                        e.printStackTrace()
                        emptyList()
                }
        }
}
