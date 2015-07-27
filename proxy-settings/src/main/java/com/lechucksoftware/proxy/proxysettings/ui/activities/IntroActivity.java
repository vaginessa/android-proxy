package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.intro.BaseSlide;
import com.lechucksoftware.proxy.proxysettings.utils.FragmentsUtils;

public class IntroActivity extends AppIntro2
{
    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(BaseSlide.newInstance(R.layout.intro_slide_1));
        addSlide(BaseSlide.newInstance(R.layout.intro_slide_2));
        addSlide(BaseSlide.newInstance(R.layout.intro_slide_3));
        addSlide(BaseSlide.newInstance(R.layout.intro_slide_4));
        addSlide(BaseSlide.newInstance(R.layout.intro_slide_5));

//        addSlide(AppIntroFragment.newInstance("Welcome to Proxy Settings", "Proxy Settings", R.drawable.ic_action_discard, R.color.green_300));
//        addSlide(AppIntroFragment.newInstance("BBB", "BBBB", R.drawable.ic_action_discard, R.color.red_400));
//        addSlide(AppIntroFragment.newInstance("CCC", "BBBB", R.drawable.ic_action_discard, R.color.yellow_500));
//        addSlide(AppIntroFragment.newInstance("DDD", "BBBB", R.drawable.ic_action_discard, R.color.blue_500));
//        addSlide(AppIntroFragment.newInstance("EEE", "BBBB", R.drawable.ic_action_discard, R.color.grey_500));
    }

    @Override
    public void onDonePressed()
    {
        App.getAppStats().updateInstallationDetails();
        FragmentsUtils.goToMainActivity(this);
    }
}
