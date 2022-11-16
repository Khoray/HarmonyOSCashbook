package com.khoray.cashbook;

import com.khoray.cashbook.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.utils.Color;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        getWindow().setStatusBarColor(Color.getIntColor("#FF02ACEF"));
    }
}
