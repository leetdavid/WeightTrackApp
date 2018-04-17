package com.paraverity.weighttrack

import android.content.Context
import android.os.Bundle
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

class AboutActivity : MaterialAboutActivity() {
z
    override fun getMaterialAboutList(context: Context): MaterialAboutList {
        return MaterialAboutList.Builder()
                .addCard(MaterialAboutCard("fitmo"))
                .build()
    }

    override fun getActivityTitle(): CharSequence? {
        return getString(R.string.mal_title_about)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }

}
