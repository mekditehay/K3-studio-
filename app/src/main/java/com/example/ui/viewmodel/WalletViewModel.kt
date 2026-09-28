package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.CoinTransaction
import com.example.data.model.CoinWallet
import com.example.data.repository.WalletRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class AdCampaign(
    val id: String,
    val title: String,
    val brand: String,
    val description: String,
    val callToAction: String,
    val rewardCoins: Int = 25,
    val bannerUrl: String
)

data class AdState(
    val isShowing: Boolean = false,
    val currentCampaign: AdCampaign? = null,
    val secondsRemaining: Int = 8,
    val isCompleted: Boolean = false,
    val hasClaimed: Boolean = false
)

class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val walletRepository = WalletRepository(database.walletDao())

    val wallet: StateFlow<CoinWallet?> = walletRepository.wallet
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<CoinTransaction>> = walletRepository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Rewarded Ad state
    private val _adState = MutableStateFlow(AdState())
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    private var adTimerJob: Job? = null

    // Daily bonus message
    private val _dailyBonusMessage = MutableStateFlow<String?>(null)
    val dailyBonusMessage = _dailyBonusMessage.asStateFlow()

    // Spin result message
    private val _spinMessage = MutableStateFlow<String?>(null)
    val spinMessage = _spinMessage.asStateFlow()

    private val adCampaigns = listOf(
        AdCampaign(
            id = "ad_telebirr",
            title = "ቴሌብር ሱፐርአፕ (telebirr)",
            brand = "ኢትዮ ቴሌኮም",
            description = "ክፍያዎችን በቀላሉ እና በቅጽበት ይፈጽሙ! በቴሌብር ገንዘብ ይላኩ፣ የፊልም ትኬት ይቁረጡ።",
            callToAction = "ቴሌብርን ይሞክሩ",
            rewardCoins = 25,
            bannerUrl = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=600&auto=format&fit=crop&q=80"
        ),
        AdCampaign(
            id = "ad_cbe",
            title = "ሲቢኢ ብር (CBE Birr)",
            brand = "የኢትዮጵያ ንግድ ባንክ",
            description = "ዘመናዊ የዲጂታል ባንክ አገልግሎት በጣትዎ ጫፍ! ደህንነቱ የተጠበቀ ክፍያ።",
            callToAction = "መተግበሪያውን ያውርዱ",
            rewardCoins = 25,
            bannerUrl = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=600&auto=format&fit=crop&q=80"
        ),
        AdCampaign(
            id = "ad_ethiopian",
            title = "የኢትዮጵያ አየር መንገድ",
            brand = "Ethiopian Airlines",
            description = "የአፍሪካ ግንባር ቀደም አየር መንገድ ጋር ዓለምን ያስሱ። ልዩ የበረራ ቅናሾች!",
            callToAction = "በረራ ያስይዙ",
            rewardCoins = 30,
            bannerUrl = "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=600&auto=format&fit=crop&q=80"
        ),
        AdCampaign(
            id = "ad_k3_vip",
            title = "K3 Movie VIP Cinema Pass",
            brand = "K3 Movie Pro",
            description = "ያለ ምንም ማስታወቂያ ሁሉንም የትርጉም ፊልሞች በከፍተኛ ጥራት በነፃ ይመልከቱ!",
            callToAction = "VIP ይሁኑ",
            rewardCoins = 25,
            bannerUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=600&auto=format&fit=crop&q=80"
        )
    )

    fun startWatchingAd() {
        val randomCampaign = adCampaigns.random()
        val duration = 8 // 8 seconds countdown
        _adState.value = AdState(
            isShowing = true,
            currentCampaign = randomCampaign,
            secondsRemaining = duration,
            isCompleted = false,
            hasClaimed = false
        )

        adTimerJob?.cancel()
        adTimerJob = viewModelScope.launch {
            for (i in duration downTo 1) {
                delay(1000)
                _adState.value = _adState.value.copy(secondsRemaining = i - 1)
            }
            _adState.value = _adState.value.copy(isCompleted = true, secondsRemaining = 0)
        }
    }

    fun claimAdReward() {
        val currentState = _adState.value
        if (!currentState.isCompleted || currentState.hasClaimed) return

        val reward = currentState.currentCampaign?.rewardCoins ?: 25
        val brand = currentState.currentCampaign?.brand ?: "ማስታወቂያ"

        viewModelScope.launch {
            walletRepository.addRewardCoins(
                amount = reward,
                reason = "የ$brand ማስታወቂያ በማየት የተገኘ +$reward ኮይን",
                type = "REWARDED_AD"
            )
            vibrateSuccess()
            _adState.value = currentState.copy(hasClaimed = true)
        }
    }

    fun dismissAd() {
        adTimerJob?.cancel()
        _adState.value = AdState(isShowing = false)
    }

    fun claimDailyReward() {
        viewModelScope.launch {
            val (success, amount) = walletRepository.claimDailyBonus()
            if (success) {
                vibrateSuccess()
                _dailyBonusMessage.value = "እንኳን ደስ አለዎት! የዕለቱ $amount ነፃ ኮይን ወደ ቦርሳዎ ገብቷል!"
            } else {
                _dailyBonusMessage.value = "የዛሬውን የዕለት ጉርሻ አስቀድመው ወስደዋል! ነገ እንደገና ይሞክሩ።"
            }
        }
    }

    fun clearDailyBonusMessage() {
        _dailyBonusMessage.value = null
    }

    fun spinLuckyWheel() {
        viewModelScope.launch {
            val possibleRewards = listOf(15, 25, 30, 50, 75, 100)
            val wonReward = possibleRewards[Random.nextInt(possibleRewards.size)]

            walletRepository.addRewardCoins(
                amount = wonReward,
                reason = "በዕድል እሽክርክሪት (Lucky Spin) የተገኘ +$wonReward ኮይን",
                type = "LUCKY_SPIN"
            )
            vibrateSuccess()
            _spinMessage.value = "እንኳን ደስ አለዎት! በእሽክርክሪቱ +$wonReward ኮይን አሸንፈዋል!"
        }
    }

    fun clearSpinMessage() {
        _spinMessage.value = null
    }

    private fun vibrateSuccess() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(120)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibrator not available
        }
    }
}
