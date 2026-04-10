package com.earth2me.essentials.signs;

//This enum is used when checking to see what signs are enabled
public enum Signs {
    ANVIL(new SignAnvil()),
    BALANCE(new SignBalance()),
    CARTOGRAPHY(new SignCartography()),
    DISPOSAL(new SignDisposal()),
    ENCHANT(new SignEnchant()),
    FREE(new SignFree()),
    GAMEMODE(new SignGameMode()),
    GRINDSTONE(new SignGrindstone()),
    HEAL(new SignHeal()),
    INFO(new SignInfo()),
    LOOM(new SignLoom()),
    PROTECTION(new SignProtection()),
    REPAIR(new SignRepair()),
    SMITHING(new SignSmithing()),
    SPAWNMOB(new SignSpawnmob()),
    TIME(new SignTime()),
    TRADE(new SignTrade()),
    WARP(new SignWarp()),
    WEATHER(new SignWeather()),
    WORKBENCH(new SignWorkbench());
    private final EssentialsSign sign;

    Signs(final EssentialsSign sign) {
        this.sign = sign;
    }

    public EssentialsSign getSign() {
        return sign;
    }
}
