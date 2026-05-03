package com.example.vinilosapp.data.cache

import android.util.LruCache
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector

object CacheManager {
    private const val DEFAULT_CACHE_MAX_SIZE = 15

    object CacheKeys {
        const val ALBUMS_LIST = "albums_list"
        const val COLLECTORS_LIST = "collectors_list"
    }

    private val albumsListCache = LruCache<String, List<Album>>(DEFAULT_CACHE_MAX_SIZE)
    private val collectorsListCache = LruCache<String, List<Collector>>(DEFAULT_CACHE_MAX_SIZE)

    @Synchronized
    fun getAlbumsList(): List<Album>? = albumsListCache.get(CacheKeys.ALBUMS_LIST)

    @Synchronized
    fun putAlbumsList(albums: List<Album>) {
        albumsListCache.put(CacheKeys.ALBUMS_LIST, albums)
    }

    @Synchronized
    fun getCollectorsList(): List<Collector>? = collectorsListCache.get(CacheKeys.COLLECTORS_LIST)

    @Synchronized
    fun putCollectorsList(collectors: List<Collector>) {
        collectorsListCache.put(CacheKeys.COLLECTORS_LIST, collectors)
    }

    @Synchronized
    fun invalidateAlbumsListCache() {
        albumsListCache.remove(CacheKeys.ALBUMS_LIST)
    }

    @Synchronized
    fun invalidateCollectorsListCache() {
        collectorsListCache.remove(CacheKeys.COLLECTORS_LIST)
    }

    @Synchronized
    fun clearAllCaches() {
        albumsListCache.evictAll()
        collectorsListCache.evictAll()
    }
}
