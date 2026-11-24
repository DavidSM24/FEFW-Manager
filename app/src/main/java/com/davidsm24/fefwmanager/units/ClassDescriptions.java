package com.davidsm24.fefwmanager.units;

import androidx.annotation.StringRes;

import com.davidsm24.fefwmanager.R;

final class ClassDescriptions {
    private ClassDescriptions() {}

    @StringRes
    static int forClass(String classId) {
        return switch (classId) {
            case "archer" -> R.string.class_description_archer;
            case "armored-knight" -> R.string.class_description_armored_knight;
            case "armored-ornius-rider" -> R.string.class_description_armored_ornius_rider;
            case "bardinger" -> R.string.class_description_bardinger;
            case "battlemaster" -> R.string.class_description_battlemaster;
            case "bau-lord" -> R.string.class_description_bau_lord;
            case "bishop" -> R.string.class_description_bishop;
            case "blacksmith" -> R.string.class_description_blacksmith;
            case "bow-adept" -> R.string.class_description_bow_adept;
            case "bow-knight" -> R.string.class_description_bow_knight;
            case "brigand" -> R.string.class_description_brigand;
            case "caladrius" -> R.string.class_description_caladrius;
            case "castle-knight" -> R.string.class_description_castle_knight;
            case "cataphract" -> R.string.class_description_cataphract;
            case "celestial-trooper" -> R.string.class_description_celestial_trooper;
            case "charioteer" -> R.string.class_description_charioteer;
            case "commoner" -> R.string.class_description_commoner;
            case "dancer" -> R.string.class_description_dancer;
            case "diviner" -> R.string.class_description_diviner;
            case "dragoon" -> R.string.class_description_dragoon;
            case "dreadnought" -> R.string.class_description_dreadnought;
            case "druid" -> R.string.class_description_druid;
            case "elephant-rider" -> R.string.class_description_elephant_rider;
            case "forest-knight" -> R.string.class_description_forest_knight;
            case "gladiator" -> R.string.class_description_gladiator;
            case "great-knight" -> R.string.class_description_great_knight;
            case "guardian" -> R.string.class_description_guardian;
            case "high-savant" -> R.string.class_description_high_savant;
            case "hunter" -> R.string.class_description_hunter;
            case "light-cavalry" -> R.string.class_description_light_cavalry;
            case "myrmidon" -> R.string.class_description_myrmidon;
            case "noble" -> R.string.class_description_noble;
            case "orichaldia" -> R.string.class_description_orichaldia;
            case "ornius-rider" -> R.string.class_description_ornius_rider;
            case "ovate" -> R.string.class_description_ovate;
            case "priest" -> R.string.class_description_priest;
            case "pugilist" -> R.string.class_description_pugilist;
            case "ranger" -> R.string.class_description_ranger;
            case "rogue" -> R.string.class_description_rogue;
            case "sentinel" -> R.string.class_description_sentinel;
            case "shadow-seeker" -> R.string.class_description_shadow_seeker;
            case "shaman" -> R.string.class_description_shaman;
            case "shido" -> R.string.class_description_shido;
            case "sniper" -> R.string.class_description_sniper;
            case "soldier" -> R.string.class_description_soldier;
            case "swordmaster" -> R.string.class_description_swordmaster;
            case "the-apsara" -> R.string.class_description_the_apsara;
            case "the-avatar" -> R.string.class_description_the_avatar;
            case "the-blade" -> R.string.class_description_the_blade;
            case "the-calamity" -> R.string.class_description_the_calamity;
            case "the-cavalier" -> R.string.class_description_the_cavalier;
            case "the-eternal" -> R.string.class_description_the_eternal;
            case "the-godhand" -> R.string.class_description_the_godhand;
            case "the-trident" -> R.string.class_description_the_trident;
            case "troubadour" -> R.string.class_description_troubadour;
            case "valkyrium" -> R.string.class_description_valkyrium;
            case "war-monk" -> R.string.class_description_war_monk;
            case "warrior" -> R.string.class_description_warrior;
            case "wing-soldier" -> R.string.class_description_wing_soldier;
            case "wiseman" -> R.string.class_description_wiseman;
            default -> R.string.class_description_unknown;
        };
    }
}
