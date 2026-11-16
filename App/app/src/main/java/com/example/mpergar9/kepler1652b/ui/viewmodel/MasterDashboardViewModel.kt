package com.example.mpergar9.kepler1652b.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.model.Character
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MasterDashboardUiState(
    val characters: List<Character> = emptyList()
)

class MasterDashboardViewModel : ViewModel() {

    fun getCharactersForCampaign(campaignId: String): StateFlow<List<Character>> {
        return CampaignRepository.characters
            .map { list -> list.filter { it.campaignId == campaignId }.sortedBy { it.characterName.lowercase() } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun applyRadiationDelta(campaignId: String, delta: Int) {
        CampaignRepository.applyGlobalRadiationDelta(campaignId, delta)
    }

    fun applyToxicityDelta(campaignId: String, delta: Int) {
        CampaignRepository.applyGlobalToxicityDelta(campaignId, delta)
    }

    fun applySoporteVitalDelta(campaignId: String, delta: Int) {
        CampaignRepository.applyGlobalSoporteVitalDelta(campaignId, delta)
    }

    fun applySanityDelta(campaignId: String, delta: Int) {
        CampaignRepository.applyGlobalSanityDelta(campaignId, delta)
    }

    fun applyStatusEffect(campaignId: String, effect: String) {
        CampaignRepository.applyGlobalStatusEffect(campaignId, effect, applyToAll = true)
    }

    fun clearAllStatusEffects(campaignId: String) {
        CampaignRepository.clearAllGlobalStatusEffects(campaignId)
    }

    fun deleteCharacter(charId: String) {
        CampaignRepository.deleteCharacter(charId)
    }

    fun deleteCampaign(campaignId: String) {
        CampaignRepository.deleteCampaign(campaignId)
    }
}
