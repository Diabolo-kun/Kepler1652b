package com.example.mpergar9.kepler1652b.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.ui.screens.*
import com.example.mpergar9.kepler1652b.ui.screens.master.MasterCharacterDetailScreen

object NavRoutes {
    const val CAMPAIGN_LIST = "campaign_list"
    const val MASTER_DASHBOARD = "master_dashboard/{campaignId}"
    const val MASTER_CHARACTER_DETAIL = "master_character_detail/{campaignId}/{characterId}"
    const val CHARACTER_LIST = "character_list/{campaignId}"
    const val PLAYER_SHEET = "player_sheet/{campaignId}/{characterId}"
    const val CHARACTER_CREATION = "character_creation/{campaignId}"

    fun masterDashboard(campaignId: String) = "master_dashboard/$campaignId"
    fun masterCharacterDetail(campaignId: String, characterId: String) = "master_character_detail/$campaignId/$characterId"
    fun characterList(campaignId: String) = "character_list/$campaignId"
    fun playerSheet(campaignId: String, characterId: String) = "player_sheet/$campaignId/$characterId"
    fun characterCreation(campaignId: String) = "character_creation/$campaignId"
}

@Composable
fun KeplerNavHost(
    navController: NavHostController = rememberNavController()
) {
    val campaigns by CampaignRepository.campaigns.collectAsState()
    val characters by CampaignRepository.characters.collectAsState()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.CAMPAIGN_LIST,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable(NavRoutes.CAMPAIGN_LIST) {
            CampaignListScreen(
                onSelectCampaignAsMaster = { campaign ->
                    navController.navigate(NavRoutes.masterDashboard(campaign.id))
                },
                onSelectCampaignAsPlayer = { campaign ->
                    navController.navigate(NavRoutes.characterList(campaign.id))
                },
                onCreateCharacterForCampaign = { campaign ->
                    navController.navigate(NavRoutes.characterCreation(campaign.id))
                }
            )
        }

        composable(
            route = NavRoutes.MASTER_DASHBOARD,
            arguments = listOf(navArgument("campaignId") { type = NavType.StringType })
        ) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            val campaign = campaigns.find { it.id == campaignId }
            if (campaign != null) {
                MasterDashboardScreen(
                    campaign = campaign,
                    onSelectCharacterForDetail = { char ->
                        navController.navigate(NavRoutes.masterCharacterDetail(campaign.id, char.id))
                    },
                    onBack = { navController.popBackStack() },
                    onDeleteCampaign = {
                        navController.navigate(NavRoutes.CAMPAIGN_LIST) {
                            popUpTo(NavRoutes.CAMPAIGN_LIST) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(
            route = NavRoutes.MASTER_CHARACTER_DETAIL,
            arguments = listOf(
                navArgument("campaignId") { type = NavType.StringType },
                navArgument("characterId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            val campaign = campaigns.find { it.id == campaignId }
            val character = characters.find { it.id == characterId }
            if (campaign != null && character != null) {
                MasterCharacterDetailScreen(
                    campaign = campaign,
                    character = character,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = NavRoutes.CHARACTER_LIST,
            arguments = listOf(navArgument("campaignId") { type = NavType.StringType })
        ) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            val campaign = campaigns.find { it.id == campaignId }
            if (campaign != null) {
                CharacterListScreen(
                    campaign = campaign,
                    onSelectCharacter = { selectedChar ->
                        navController.navigate(NavRoutes.playerSheet(campaign.id, selectedChar.id))
                    },
                    onCreateNewCharacter = {
                        navController.navigate(NavRoutes.characterCreation(campaign.id))
                    },
                    onOpenMasterDashboard = {
                        navController.navigate(NavRoutes.masterDashboard(campaign.id))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = NavRoutes.PLAYER_SHEET,
            arguments = listOf(
                navArgument("campaignId") { type = NavType.StringType },
                navArgument("characterId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            val campaign = campaigns.find { it.id == campaignId }
            val character = characters.find { it.id == characterId }
            if (campaign != null) {
                PlayerCharacterSheetScreen(
                    campaign = campaign,
                    selectedCharacter = character,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = NavRoutes.CHARACTER_CREATION,
            arguments = listOf(navArgument("campaignId") { type = NavType.StringType })
        ) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            val campaign = campaigns.find { it.id == campaignId }
            if (campaign != null) {
                CharacterCreationScreen(
                    campaign = campaign,
                    onCharacterCreated = {
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
