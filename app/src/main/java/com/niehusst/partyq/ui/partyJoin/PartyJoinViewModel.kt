package com.niehusst.partyq.ui.partyJoin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niehusst.partyq.services.CommunicationService

class PartyJoinViewModel : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun connectToParty(code: String) {
        if (code.length != 4) {
            return // fast fail TODO: show some error snackbar
        }
        CommunicationService.connectToParty(code)
        _loading.value = true
        // TODO: how to nav on success???
    }
}
