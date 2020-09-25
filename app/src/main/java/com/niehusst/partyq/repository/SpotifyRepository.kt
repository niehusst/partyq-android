package com.niehusst.partyq.repository

import android.content.Context
import com.niehusst.partyq.network.Resource
import com.niehusst.partyq.network.SpotifyApi
import com.niehusst.partyq.network.models.SearchResult
import com.niehusst.partyq.services.TokenHandlerService
import com.niehusst.partyq.services.UserTypeService
import timber.log.Timber

object SpotifyRepository {

    // TODO: do i need some way to get this back on process death recreation?
    //  (this may already be handled since onCreate in PartyActivity is called again)
    private var api: SpotifyApi? = null

    /**
     * This must be called to initialize the API endpoint access.
     *
     * Precondition:
     *      requires OAuth token is set before this method is called.
     */
    fun start(ctx: Context) {
        if (UserTypeService.isHost(ctx)) {
            api = SpotifyApi(TokenHandlerService.getToken(ctx))
        }
    }

    /**
     * If the user is the host, make an API call to Spotify. Otherwise, send the request to the
     * host to execute. The management of loading state is left to the calling ViewModel.
     */
    suspend fun searchSongs(query: String, context: Context): Resource<SearchResult> {
        return if (UserTypeService.isHost(context)) {
            try {
                val result = api?.endPoints?.searchTracks(query, "track") ?: throw Exception("Uninitialized api")
                Resource.success(result)
            } catch (err: Throwable) {
                Timber.e(err)
                Resource.error(null, "Network error")
            }
        } else {
//          TODO  CommunicationService.sendSearchRequest(query)
            Resource.error(null, "not yet implemented")
        }
    }
}
