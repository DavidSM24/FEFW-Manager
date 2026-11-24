package com.davidsm24.fefwmanager.units;

import static com.davidsm24.fefwmanager.units.ClassTag.APOYO;
import static com.davidsm24.fefwmanager.units.ClassTag.CERROJO;
import static com.davidsm24.fefwmanager.units.ClassTag.CURADOR;
import static com.davidsm24.fefwmanager.units.ClassTag.FISICO;
import static com.davidsm24.fefwmanager.units.ClassTag.MAGICO;
import static com.davidsm24.fefwmanager.units.ClassTag.MONTADO;
import static com.davidsm24.fefwmanager.units.ClassTag.TANQUE;
import static com.davidsm24.fefwmanager.units.ClassTag.VOLADOR;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class ClassRoleCatalog {
    private static final Map<String, ClassRoleProfile> PROFILES = buildProfiles();

    private ClassRoleCatalog() {
    }

    static ClassRoleProfile forClass(String classId) {
        return PROFILES.get(classId);
    }

    static Set<String> classIds() {
        return PROFILES.keySet();
    }

    private static Map<String, ClassRoleProfile> buildProfiles() {
        Map<String, ClassRoleProfile> profiles = new HashMap<>();

        // Scores describe each role within its tier: attack, defense, movement, support, healing.
        // Base
        profiles.put("commoner", profile(2, 2, 3, 1, 0, FISICO));
        profiles.put("noble", profile(2, 3, 3, 2, 0, FISICO));

        // Beginner
        profiles.put("diviner", profile(3, 1, 3, 2, 3, MAGICO, CURADOR));
        profiles.put("gladiator", profile(4, 2, 3, 1, 0, FISICO));
        profiles.put("hunter", profile(3, 2, 3, 2, 0, FISICO));
        profiles.put("ornius-rider", profile(3, 2, 4, 2, 0, FISICO, MONTADO));
        profiles.put("soldier", profile(3, 3, 3, 1, 0, FISICO));

        // Specialty
        profiles.put("archer", profile(4, 2, 3, 2, 0, FISICO));
        profiles.put("armored-knight", profile(3, 5, 1, 1, 0, TANQUE, FISICO));
        profiles.put("armored-ornius-rider", profile(3, 4, 4, 1, 0, FISICO, MONTADO, TANQUE));
        profiles.put("brigand", profile(5, 2, 3, 1, 0, FISICO));
        profiles.put("charioteer", profile(5, 3, 4, 2, 0, FISICO, MONTADO));
        profiles.put("light-cavalry", profile(3, 3, 4, 2, 0, FISICO, MONTADO));
        profiles.put("myrmidon", profile(5, 2, 3, 1, 0, FISICO));
        profiles.put("priest", profile(2, 2, 2, 3, 5, MAGICO, CURADOR));
        profiles.put("pugilist", profile(4, 2, 3, 2, 0, FISICO));
        profiles.put("rogue", profile(3, 2, 4, 4, 0, CERROJO, FISICO));
        profiles.put("shaman", profile(4, 1, 3, 2, 1, MAGICO));
        profiles.put("wing-soldier", profile(4, 2, 5, 2, 0, VOLADOR, FISICO));

        // Advanced
        profiles.put("bardinger", profile(4, 4, 4, 2, 0, FISICO, MONTADO));
        profiles.put("bishop", profile(2, 2, 2, 3, 5, MAGICO, CURADOR));
        profiles.put("blacksmith", profile(4, 3, 3, 2, 0, FISICO, MAGICO));
        profiles.put("caladrius", profile(4, 1, 3, 2, 1, MAGICO, MONTADO));
        profiles.put("cataphract", profile(4, 5, 4, 1, 0, FISICO, MONTADO, TANQUE));
        profiles.put("dancer", profile(2, 1, 3, 5, 1, APOYO));
        profiles.put("dragoon", profile(4, 2, 5, 2, 0, VOLADOR, FISICO));
        profiles.put("dreadnought", profile(3, 5, 1, 1, 0, TANQUE, FISICO));
        profiles.put("elephant-rider", profile(5, 5, 3, 2, 0, FISICO, MONTADO, TANQUE));
        profiles.put("forest-knight", profile(4, 3, 4, 2, 0, FISICO, MONTADO));
        profiles.put("guardian", profile(3, 4, 2, 2, 4, TANQUE, CURADOR));
        profiles.put("ovate", profile(4, 1, 3, 3, 3, MAGICO, CURADOR));
        profiles.put("ranger", profile(3, 2, 4, 4, 0, CERROJO, FISICO));
        profiles.put("shido", profile(5, 2, 3, 1, 0, FISICO));
        profiles.put("sniper", profile(5, 2, 3, 2, 0, FISICO));
        profiles.put("troubadour", profile(3, 2, 4, 4, 4, MAGICO, MONTADO, CURADOR, APOYO));
        profiles.put("warrior", profile(5, 3, 3, 1, 0, FISICO));

        // Master
        profiles.put("battlemaster", profile(5, 3, 3, 2, 0, FISICO));
        profiles.put("bau-lord", profile(5, 3, 5, 2, 0, VOLADOR, FISICO));
        profiles.put("bow-adept", profile(5, 2, 3, 2, 0, FISICO));
        profiles.put("bow-knight", profile(4, 3, 5, 2, 0, FISICO, MONTADO));
        profiles.put("castle-knight", profile(3, 5, 1, 2, 0, TANQUE, FISICO));
        profiles.put("celestial-trooper", profile(4, 3, 5, 2, 0, VOLADOR, FISICO));
        profiles.put("druid", profile(5, 1, 3, 3, 0, MAGICO));
        profiles.put("great-knight", profile(4, 5, 4, 1, 0, FISICO, MONTADO, TANQUE));
        profiles.put("high-savant", profile(5, 3, 3, 2, 0, FISICO, MAGICO));
        profiles.put("orichaldia", profile(4, 4, 5, 2, 0, FISICO, MONTADO));
        profiles.put("sentinel", profile(3, 5, 2, 3, 4, TANQUE, CURADOR));
        profiles.put("shadow-seeker", profile(4, 2, 4, 5, 0, CERROJO, FISICO));
        profiles.put("swordmaster", profile(5, 2, 3, 1, 0, FISICO));
        profiles.put("valkyrium", profile(4, 3, 5, 4, 4, MAGICO, MONTADO, CURADOR, APOYO));
        profiles.put("war-monk", profile(4, 3, 3, 3, 3, FISICO, CURADOR));
        profiles.put("wiseman", profile(2, 2, 2, 4, 5, MAGICO, CURADOR));

        // Divine
        profiles.put("the-apsara", profile(3, 2, 4, 5, 1, APOYO));
        profiles.put("the-avatar", profile(5, 2, 3, 4, 5, MAGICO, CURADOR, APOYO));
        profiles.put("the-blade", profile(5, 2, 3, 1, 0, FISICO));
        profiles.put("the-calamity", profile(4, 2, 4, 5, 0, CERROJO, FISICO));
        profiles.put("the-cavalier", profile(5, 4, 5, 2, 0, FISICO, MONTADO));
        profiles.put("the-eternal", profile(5, 4, 3, 1, 0, FISICO));
        profiles.put("the-godhand", profile(4, 3, 3, 3, 4, FISICO, CURADOR));
        profiles.put("the-trident", profile(3, 5, 2, 3, 5, TANQUE, CURADOR));

        return Collections.unmodifiableMap(profiles);
    }

    private static ClassRoleProfile profile(int atk, int def, int mov, int sup, int heal, ClassTag... tags) {
        return new ClassRoleProfile(atk, def, mov, sup, heal, Arrays.asList(tags));
    }
}
