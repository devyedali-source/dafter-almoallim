plugins {
  alias(libs.plugins.android.asset.pack)
}

// حزمة صور كتب المدرسة: تنزّلها Google Play تلقائيًا بعد تثبيت التطبيق (fast-follow)
assetPack {
  packName.set("books")
  dynamicDelivery {
    deliveryType.set("fast-follow")
  }
}
