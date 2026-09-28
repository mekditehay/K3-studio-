package com.example.data.model

enum class ServiceType(
    val id: String,
    val titleAmharic: String,
    val titleEnglish: String,
    val shortDescAmharic: String,
    val normalPrice: Double,
    val vipPrice: Double,
    val proPrice: Double,
    val iconName: String
) {
    LOGO_DESIGN(
        id = "LOGO",
        titleAmharic = "የሎጎ ዲዛይን (Logo Design)",
        titleEnglish = "Logo Design",
        shortDescAmharic = "ለድርጅትዎ ወይም ለግል ብራንድዎ ዘመናዊና ማራኪ ሎጎ",
        normalPrice = 30.0,
        vipPrice = 60.0,
        proPrice = 120.0,
        iconName = "brush"
    ),
    PHOTO_EDITING(
        id = "PHOTO",
        titleAmharic = "ፎቶ ኤዲቲንግ (Photo Editing)",
        titleEnglish = "Photo Editing",
        shortDescAmharic = "ከፍተኛ ጥራት ያለው የቆዳ ማስተካከል፣ ዳራ መቀየር እና የቀለም እርማት",
        normalPrice = 50.0,
        vipPrice = 100.0,
        proPrice = 200.0,
        iconName = "photo"
    ),
    VIDEO_EDITING(
        id = "VIDEO",
        titleAmharic = "ቪዲዮ ኤዲቲንግ (Video Editing)",
        titleEnglish = "Video Editing",
        shortDescAmharic = "ቲክቶክ፣ ሪልስ፣ ዩቲዩብ እና የማስታወቂያ ቪዲዮዎችን በከፍተኛ ጥራት መቁረጥና ማሳመር",
        normalPrice = 150.0,
        vipPrice = 300.0,
        proPrice = 500.0,
        iconName = "movie"
    ),
    SOCIAL_MEDIA(
        id = "SOCIAL",
        titleAmharic = "ሶሻል ሚዲያ ማኔጅመንት (Social Media)",
        titleEnglish = "Social Media Management",
        shortDescAmharic = "የቴሌግራም፣ ቲክቶክ እና ፌስቡክ ፖስቶች፣ ባነሮች እና የማስታወቂያ እቅዶች",
        normalPrice = 250.0,
        vipPrice = 500.0,
        proPrice = 1000.0,
        iconName = "share"
    )
}

enum class TierType(
    val id: String,
    val titleAmharic: String,
    val titleEnglish: String,
    val deliverySpeedAmharic: String,
    val deliverySpeedEnglish: String,
    val revisionsAmharic: String,
    val badgeAmharic: String,
    val isPriority: Boolean
) {
    NORMAL(
        id = "NORMAL",
        titleAmharic = "ኖርማል (Normal)",
        titleEnglish = "Normal",
        deliverySpeedAmharic = "ከ24 - 48 ሰዓት",
        deliverySpeedEnglish = "24-48 Hours",
        revisionsAmharic = "2 ማስተካከያ (Revisions)",
        badgeAmharic = "መደበኛ",
        isPriority = false
    ),
    VIP(
        id = "VIP",
        titleAmharic = "ቪአይፒ (VIP)",
        titleEnglish = "VIP Priority",
        deliverySpeedAmharic = "በ12 - 24 ሰዓት (ፈጣን)",
        deliverySpeedEnglish = "12-24 Hours (Fast)",
        revisionsAmharic = "ያልተገደበ ማስተካከያ (Unlimited)",
        badgeAmharic = "ልዩ ቅድሚያ ⚡",
        isPriority = true
    ),
    PRO(
        id = "PRO",
        titleAmharic = "ፕሮ ማስተር (PRO)",
        titleEnglish = "PRO Master",
        deliverySpeedAmharic = "በ6 - 12 ሰዓት (እጅግ በጣም ፈጣን!)",
        deliverySpeedEnglish = "6-12 Hours (Ultra Rush)",
        revisionsAmharic = "ያልተገደበ + ዋና ፋይል (Source Files)",
        badgeAmharic = "ፈጣን አጋዥ 👑",
        isPriority = true
    );

    fun getPriceFor(service: ServiceType): Double {
        return when (this) {
            NORMAL -> service.normalPrice
            VIP -> service.vipPrice
            PRO -> service.proPrice
        }
    }
}
