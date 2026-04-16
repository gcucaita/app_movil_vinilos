package com.example.vinilosapp.data.cache

import android.util.LruCache
import com.example.vinilosapp.data.model.Album

object CacheManager {
    private const val DEFAULT_CACHE_MAX_SIZE = 15

    object CacheKeys {
        const val ALBUMS_LIST = "albums_list"
    }

    private val albumsListCache = LruCache<String, List<Album>>(DEFAULT_CACHE_MAX_SIZE)

    @Synchronized
    fun getAlbumsList(): List<Album>? = albumsListCache.get(CacheKeys.ALBUMS_LIST)

    @Synchronized
    fun putAlbumsList(albums: List<Album>) {
        albumsListCache.put(CacheKeys.ALBUMS_LIST, albums)
    }

    @Synchronized
    fun invalidateAlbumsListCache() {
        albumsListCache.remove(CacheKeys.ALBUMS_LIST)
    }

    @Synchronized
    fun clearAllCaches() {
        albumsListCache.evictAll()
    }
}
