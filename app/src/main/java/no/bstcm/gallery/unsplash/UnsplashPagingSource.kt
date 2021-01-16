package no.bstcm.gallery.unsplash

import android.util.Log
import androidx.paging.PagingSource
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE = 1

class UnsplashPagingSource(
    private val unsplashApi: UnsplashApi
) : PagingSource<Int, Photo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val position = params.key ?: STARTING_PAGE

        return try {
            val response = unsplashApi.randomPhotos(position, params.loadSize)
                LoadResult.Page(
                    data = response,
                    prevKey = if (position == STARTING_PAGE) null else position - 1,
                    nextKey = if (response.isEmpty()) null else position + 1
                )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}