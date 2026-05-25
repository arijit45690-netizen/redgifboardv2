package com.yourname.redgifboard

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
