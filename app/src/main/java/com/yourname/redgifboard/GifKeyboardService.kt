package com.yourname.redgifboard

import android.content.ClipDescription
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.FileProvider
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.File
import java.net.URL

class GifKeyboardService : InputMethodService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var gifAdapter: GifAdapter
    private var authToken: String = ""
    private var currentQuery: String = "trending"
    private var currentPage: Int = 1
    private var totalPages: Int = 1
    private var isLoading = false
    private var currentDownloadJob: Job? = null

    private val categories = listOf(
        "🔥 Trending", "💋 Kiss", "👙 Boobs", "🍑 Ass",
        "❤️ Sex", "🫦 Blowjob", "🔞 Fuck", "👩 Teen",
        "🌶️ Hot", "💦 Wet", "👅 Lick", "🎀 Cute"
    )

    private val categoryQueries = listOf(
        "trending", "kiss", "boobs", "ass",
        "sex", "blowjob", "fuck", "teen",
        "hot", "wet", "lick", "cute"
    )

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.gifGrid)
        val searchBar = view.findViewById<EditText>(R.id.searchBar)
        val loadingBar = view.findViewById<ProgressBar>(R.id.loadingBar)
        val statusText = view.findViewById<TextView>(R.id.statusText)
        val categoryContainer = view.findViewById<LinearLayout>(R.id.categoryContainer)
        val keyboardView = view.findViewById<LinearLayout>(R.id.inlineKeyboard)

        // Build category chips
        categories.forEachIndexed { index, label ->
            val chip = layoutInflater.inflate(R.layout.category_chip, categoryContainer, false) as TextView
            chip.text = label
            chip.setOnClickListener {
                currentQuery = categoryQueries[index]
                searchBar.setText(categoryQueries[index])
                currentPage = 1
                serviceScope.launch { loadGifs(loadingBar, statusText) }
            }
            categoryContainer.addView(chip)
        }

        // Inline QWERTY — pass loadingBar and statusText directly
        setupInlineKeyboard(keyboardView, searchBar, loadingBar, statusText)

        // GIF grid
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (gifAdapter.getItemViewType(position) == 1) 2 else 1
            }
        }

        gifAdapter = GifAdapter(
            onGifClick = { gif -> sendGif(gif, loadingBar, statusText) },
            onLoadMoreClick = {
                if (currentPage < totalPages && !isLoading) {
                    currentPage++
                    serviceScope.launch { loadMoreGifs(loadingBar, statusText) }
                }
            }
        )
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = gifAdapter

        // Initial load
        serviceScope.launch {
            loadingBar.visibility = View.VISIBLE
            statusText.text = "Loading..."
            fetchToken()
            if (authToken.isNotEmpty()) {
                loadGifs(loadingBar, statusText)
            } else {
                loadingBar.visibility = View.GONE
                statusText.text = "Auth failed. Check connection."
            }
        }

        // Search bar action
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val q = searchBar.text.toString().trim()
                if (q.isNotEmpty()) {
                    currentQuery = q
                    currentPage = 1
                    serviceScope.launch { loadGifs(loadingBar, statusText) }
                }
                true
            } else false
        }

        return view
    }

    private fun setupInlineKeyboard(
        keyboardView: LinearLayout,
        searchBar: EditText,
        loadingBar: ProgressBar,
        statusText: TextView
    ) {
        val rows = listOf(
            listOf("q","w","e","r","t","y","u","i","o","p"),
            listOf("a","s","d","f","g","h","j","k","l"),
            listOf("⇧","z","x","c","v","b","n","m","⌫"),
            listOf("123","Space","Search","⏎")
        )

        var isCaps = false
        val keyButtons = mutableListOf<Pair<String, TextView>>()

        fun refreshKeys() {
            keyButtons.forEach { (key, btn) ->
                if (key.length == 1) {
                    btn.text = if (isCaps) key.uppercase() else key
                }
            }
        }

        keyboardView.removeAllViews()
        rows.forEach { row ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            row.forEach { key ->
                val btn = layoutInflater.inflate(R.layout.key_button, rowLayout, false) as TextView
                val displayKey = if (isCaps && key.length == 1) key.uppercase() else key
                btn.text = displayKey
                if (key == "Space" || key == "Search") {
                    (btn.layoutParams as LinearLayout.LayoutParams).weight = 2f
                }

                keyButtons.add(Pair(key, btn))

                btn.setOnClickListener {
                    val current = searchBar.text.toString()
                    val sel = searchBar.selectionEnd.coerceAtLeast(0)
                    when (key) {
                        "⌫" -> {
                            if (current.isNotEmpty() && sel > 0) {
                                searchBar.setText(current.removeRange(sel - 1, sel))
                                searchBar.setSelection((sel - 1).coerceAtLeast(0))
                            }
                        }
                        "⇧" -> {
                            isCaps = !isCaps
                            refreshKeys()
                        }
                        "Space" -> {
                            searchBar.setText(current.substring(0, sel) + " " + current.substring(sel))
                            searchBar.setSelection(sel + 1)
                        }
                        "Search", "⏎" -> {
                            val q = searchBar.text.toString().trim()
                            if (q.isNotEmpty()) {
                                currentQuery = q
                                currentPage = 1
                                serviceScope.launch { loadGifs(loadingBar, statusText) }
                            }
                        }
                        "123" -> { }
                        else -> {
                            val char = if (isCaps) key.uppercase() else key
                            searchBar.setText(current.substring(0, sel) + char + current.substring(sel))
                            searchBar.setSelection(sel + 1)
                            if (isCaps) { isCaps = false; refreshKeys() }
                        }
                    }
                }
                rowLayout.addView(btn)
            }
            keyboardView.addView(rowLayout)
        }

        refreshKeys()
    }

    private suspend fun fetchToken() {
        try {
            val response = withContext(Dispatchers.IO) { RedGifsClient.api.getToken() }
            authToken = response.token
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadGifs(loadingBar: ProgressBar, statusText: TextView) {
        if (authToken.isEmpty()) fetchToken()
        isLoading = true
        loadingBar.visibility = View.VISIBLE
        statusText.text = "Searching..."
        gifAdapter.clearGifs()
        try {
            val response = withContext(Dispatchers.IO) {
                RedGifsClient.api.searchGifs(
                    auth = "Bearer $authToken",
                    query = currentQuery,
                    count = 20,
                    page = 1
                )
            }
            totalPages = response.pages
            gifAdapter.showLoadMore = currentPage < totalPages
            gifAdapter.setGifs(response.gifs)
            loadingBar.visibility = View.GONE
            statusText.text = if (response.gifs.isEmpty()) "No results found" else ""
        } catch (e: Exception) {
            loadingBar.visibility = View.GONE
            statusText.text = "Error. Try again."
            e.printStackTrace()
        }
        isLoading = false
    }

    private suspend fun loadMoreGifs(loadingBar: ProgressBar, statusText: TextView) {
        isLoading = true
        loadingBar.visibility = View.VISIBLE
        statusText.text = "Loading more..."
        try {
            val response = withContext(Dispatchers.IO) {
                RedGifsClient.api.searchGifs(
                    auth = "Bearer $authToken",
                    query = currentQuery,
                    count = 20,
                    page = currentPage
                )
            }
            totalPages = response.pages
            gifAdapter.showLoadMore = currentPage < totalPages
            gifAdapter.appendGifs(response.gifs)
            loadingBar.visibility = View.GONE
            statusText.text = ""
        } catch (e: Exception) {
            loadingBar.visibility = View.GONE
            statusText.text = "Error loading more."
            e.printStackTrace()
        }
        isLoading = false
    }

    private fun sendGif(gif: GifItem, loadingBar: ProgressBar, statusText: TextView) {
        val ic = currentInputConnection ?: return
        val editorInfo = currentInputEditorInfo ?: return
        currentDownloadJob?.cancel()
        currentDownloadJob = serviceScope.launch {
            statusText.text = "Sending..."
            loadingBar.visibility = View.VISIBLE
            try {
                val url = "https://i.redgifs.com/i/${gif.id}.gif"
                val cacheFile = withContext(Dispatchers.IO) {
                    val file = File(cacheDir, "${gif.id}.gif")
                    if (!file.exists()) {
                        URL(url).openStream().use { input ->
                            file.outputStream().use { output -> input.copyTo(output) }
                        }
                    }
                    file
                }
                val contentUri = FileProvider.getUriForFile(
                    this@GifKeyboardService,
                    "${packageName}.fileprovider",
                    cacheFile
                )
                val inputContentInfo = InputContentInfoCompat(
                    contentUri,
                    ClipDescription("gif", arrayOf("image/gif")),
                    null
                )
                InputConnectionCompat.commitContent(
                    ic, editorInfo, inputContentInfo,
                    InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION,
                    null
                )
                loadingBar.visibility = View.GONE
                statusText.text = "✓ Sent!"
                delay(1500)
                statusText.text = ""
            } catch (e: Exception) {
                loadingBar.visibility = View.GONE
                statusText.text = "Failed to send."
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
