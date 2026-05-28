package com.yourname.redgifboard

import android.os.Build
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1]) // Choose an SDK to avoid robolectric warnings if any
class GifAdapterTest {

    private lateinit var adapter: GifAdapter
    private var clickedGif: GifItem? = null
    private var loadMoreClicked = false

    @Before
    fun setUp() {
        clickedGif = null
        loadMoreClicked = false
        adapter = GifAdapter(
            onGifClick = { clickedGif = it },
            onLoadMoreClick = { loadMoreClicked = true }
        )
    }

    @Test
    fun `initial state has zero items`() {
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `item count reflects list size when showLoadMore is false`() {
        val gifs = listOf(GifItem("1"), GifItem("2"))
        adapter.setGifs(gifs)

        adapter.showLoadMore = false
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun `item count adds one for load more when showLoadMore is true`() {
        val gifs = listOf(GifItem("1"), GifItem("2"))
        adapter.setGifs(gifs)

        adapter.showLoadMore = true
        assertEquals(3, adapter.itemCount)
    }

    @Test
    fun `getItemViewType returns TYPE_GIF for regular items`() {
        val gifs = listOf(GifItem("1"), GifItem("2"))
        adapter.setGifs(gifs)

        assertEquals(0, adapter.getItemViewType(0)) // TYPE_GIF
        assertEquals(0, adapter.getItemViewType(1)) // TYPE_GIF
    }

    @Test
    fun `getItemViewType returns TYPE_LOAD_MORE for the last item when showLoadMore is true`() {
        val gifs = listOf(GifItem("1"), GifItem("2"))
        adapter.setGifs(gifs)
        adapter.showLoadMore = true

        assertEquals(1, adapter.getItemViewType(2)) // TYPE_LOAD_MORE
    }

    @Test
    fun `setGifs replaces existing items`() {
        adapter.setGifs(listOf(GifItem("1")))
        assertEquals(1, adapter.itemCount)

        adapter.setGifs(listOf(GifItem("2"), GifItem("3")))
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun `appendGifs adds items to existing list`() {
        adapter.setGifs(listOf(GifItem("1")))
        assertEquals(1, adapter.itemCount)

        adapter.appendGifs(listOf(GifItem("2"), GifItem("3")))
        assertEquals(3, adapter.itemCount)
    }

    @Test
    fun `clearGifs removes all items`() {
        adapter.setGifs(listOf(GifItem("1"), GifItem("2")))
        assertEquals(2, adapter.itemCount)

        adapter.clearGifs()
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `onCreateViewHolder creates correct view holders`() {
        val parent = FrameLayout(ApplicationProvider.getApplicationContext())

        val gifHolder = adapter.onCreateViewHolder(parent, 0) // TYPE_GIF
        assertTrue(gifHolder is GifAdapter.GifViewHolder)

        val loadMoreHolder = adapter.onCreateViewHolder(parent, 1) // TYPE_LOAD_MORE
        assertTrue(loadMoreHolder is GifAdapter.LoadMoreViewHolder)
    }

    @Test
    fun `onBindViewHolder for GIF item sets up click listener`() {
        val gifs = listOf(GifItem("123"))
        adapter.setGifs(gifs)

        val parent = FrameLayout(ApplicationProvider.getApplicationContext())
        val holder = adapter.onCreateViewHolder(parent, 0) as GifAdapter.GifViewHolder

        adapter.onBindViewHolder(holder, 0)

        // Simulate click
        holder.imageView.performClick()

        assertNotNull(clickedGif)
        assertEquals("123", clickedGif?.id)
    }

    @Test
    fun `onBindViewHolder for Load More item sets up click listener`() {
        val gifs = listOf(GifItem("123"))
        adapter.setGifs(gifs)
        adapter.showLoadMore = true

        val parent = FrameLayout(ApplicationProvider.getApplicationContext())
        val holder = adapter.onCreateViewHolder(parent, 1) as GifAdapter.LoadMoreViewHolder

        adapter.onBindViewHolder(holder, 1) // index 1 is the load more button

        // Simulate click
        holder.button.performClick()

        assertTrue(loadMoreClicked)
import androidx.recyclerview.widget.RecyclerView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class GifAdapterTest {

    @Test
    fun `appendGifs adds items to empty list and notifies correctly`() {
        val adapter = GifAdapter(onGifClick = {}, onLoadMoreClick = {})
        val observer = mock<RecyclerView.AdapterDataObserver>()
        adapter.registerAdapterDataObserver(observer)

        val newGifs = listOf(GifItem(id = "1"), GifItem(id = "2"))

        adapter.appendGifs(newGifs)

        assertEquals(2, adapter.itemCount)
        verify(observer).onItemRangeInserted(0, 2)
    }

    @Test
    fun `appendGifs appends items to existing list and notifies correctly`() {
        val adapter = GifAdapter(onGifClick = {}, onLoadMoreClick = {})
        adapter.setGifs(listOf(GifItem(id = "1"), GifItem(id = "2")))

        val observer = mock<RecyclerView.AdapterDataObserver>()
        adapter.registerAdapterDataObserver(observer)

        val newGifs = listOf(GifItem(id = "3"), GifItem(id = "4"), GifItem(id = "5"))

        adapter.appendGifs(newGifs)

        assertEquals(5, adapter.itemCount)
        verify(observer).onItemRangeInserted(2, 3)
    }

    @Test
    fun `appendGifs empty list does not break`() {
        val adapter = GifAdapter(onGifClick = {}, onLoadMoreClick = {})
        val observer = mock<RecyclerView.AdapterDataObserver>()
        adapter.registerAdapterDataObserver(observer)

        adapter.appendGifs(emptyList())

        assertEquals(0, adapter.itemCount)
        verify(observer).onItemRangeInserted(0, 0)
    }

    @Test
    fun `appendGifs check itemCount with showLoadMore true`() {
        val adapter = GifAdapter(onGifClick = {}, onLoadMoreClick = {})
        adapter.showLoadMore = true

        adapter.appendGifs(listOf(GifItem(id = "1")))

        assertEquals(2, adapter.itemCount) // 1 gif + 1 load more
    }
}
