package com.example.mpergar9.kepler1652b.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CampaignListUiState(
    val campaigns: List<Campaign> = emptyList(),
    val currentUserProfile: UserProfile = UserProfile(),
    val selectedTab: Int = 0 // 0: Todas, 1: Máster, 2: Jugador
)

class CampaignListViewModel : ViewModel() {

    val uiState: StateFlow<CampaignListUiState> = combine(
        CampaignRepository.campaigns,
        CampaignRepository.currentUserProfile
    ) { campaigns, profile ->
        CampaignListUiState(
            campaigns = campaigns,
            currentUserProfile = profile
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CampaignListUiState()
    )

    fun updateProfileName(newName: String) {
        CampaignRepository.updateProfileName(newName)
    }

    fun deleteCampaign(campaignId: String) {
        CampaignRepository.deleteCampaign(campaignId)
    }

    fun createCampaign(name: String, description: String, masterName: String): Campaign {
        return CampaignRepository.createCampaign(name, description, masterName)
    }

    fun joinCampaignByCode(code: String): Campaign? {
        return CampaignRepository.joinCampaignByCode(code)
    }
}
