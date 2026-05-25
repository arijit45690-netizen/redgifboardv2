import re

with open("app/src/main/java/com/yourname/redgifboard/GifKeyboardService.kt", "r") as f:
    content = f.read()

content = content.replace('''            chip.setOnClickListener {
                currentQuery = categoryQueries[index]
                searchBar.setText(categoryQueries[index])
                currentPage = 1
                serviceScope.launch { loadGifs(loadingBar, statusText) }
            }''', '''            chip.setOnClickListener {
                searchBar.setText(categoryQueries[index])
                performSearch(categoryQueries[index], loadingBar, statusText)
            }''')

content = content.replace('''                if (q.isNotEmpty()) {
                    currentQuery = q
                    currentPage = 1
                    serviceScope.launch { loadGifs(loadingBar, statusText) }
                }''', '''                if (q.isNotEmpty()) {
                    performSearch(q, loadingBar, statusText)
                }''')

content = content.replace('''                                if (q.isNotEmpty()) {
                                    currentQuery = q
                                    currentPage = 1
                                    serviceScope.launch { loadGifs(loadingBar, statusText) }
                                }''', '''                                if (q.isNotEmpty()) {
                                    performSearch(q, loadingBar, statusText)
                                }''')

new_method = '''
    private fun performSearch(query: String, loadingBar: ProgressBar, statusText: TextView) {
        currentQuery = query
        currentPage = 1
        serviceScope.launch { loadGifs(loadingBar, statusText) }
    }

    private suspend fun fetchToken() {'''

content = content.replace('''    private suspend fun fetchToken() {''', new_method)

with open("app/src/main/java/com/yourname/redgifboard/GifKeyboardService.kt", "w") as f:
    f.write(content)
