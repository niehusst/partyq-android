package com.niehusst.partyq.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchFragmentViewModel : ViewModel() {

    // TODO: implement an enum to include loading value here? or will retrofit handle that some way
    private val _isResult = MutableLiveData(false)
    val isResult: LiveData<Boolean> = _isResult

    fun submitQuery(query: String?) {
        query ?: return

        // TODO: call on CommunicationService
        //if(results.size > 0) {
        _isResult.value = true
        //} else {
        //    _isResult.value = false
        //}
    }
}
