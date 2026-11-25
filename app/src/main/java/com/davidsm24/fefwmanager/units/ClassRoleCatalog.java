package com.davidsm24.fefwmanager.units;

import static com.davidsm24.fefwmanager.units.ClassTag.APOYO;
import static com.davidsm24.fefwmanager.units.ClassTag.CERROJO;
import static com.davidsm24.fefwmanager.units.ClassTag.CURADOR;
import static com.davidsm24.fefwmanager.units.ClassTag.FISICO;
import static com.davidsm24.fefwmanager.units.ClassTag.MAGICO;
import static com.davidsm24.fefwmanager.units.ClassTag.MONTADO;
import static com.davidsm24.fefwmanager.units.ClassTag.TANQUE;
import static com.davidsm24.fefwmanager.units.ClassTag.VOLADOR;
import static com.davidsm24.fefwmanager.units.ClassTier.ADVANCED;
import static com.davidsm24.fefwmanager.units.ClassTier.DIVINE;
import static com.davidsm24.fefwmanager.units.ClassTier.INITIAL;
import static com.davidsm24.fefwmanager.units.ClassTier.INTERMEDIATE;
import static com.davidsm24.fefwmanager.units.ClassTier.MASTER;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class ClassRoleCatalog {
    private record Entry(ClassTier tier, ClassRoleProfile profile) {}

    private static final Map<String, Entry> ENTRIES = buildEntries();

    private ClassRoleCatalog() {
    }

    static ClassRoleProfile forClass(String classId) {
        Entry entry = ENTRIES.get(classId);
        return entry == null ? null : entry.profile();
    }

    static ClassTier tierForClass(String classId) {
        Entry entry = ENTRIES.get(classId);
        return entry == null ? ClassTier.UNCLASSIFIED : entry.tier();
    }

    static Set<String> classIds() {
        return ENTRIES.keySet();
    }

    private static Map<String, Entry> buildEntries() {
        Map<String, Entry> entries = new HashMap<>();

        // Scores describe each role within its tier: attack, defense, movement, support, healing.
        // Initial includes the base and beginner classes.
        entries.put("commoner", entry(INITIAL, 2, 2, 3, 1, 0, FISICO));
        entries.put("noble", entry(INITIAL, 2, 3, 3, 2, 0, FISICO));

        entries.put("diviner", entry(INITIAL, 3, 1, 3, 2, 3, MAGICO, CURADOR));
        entries.put("gladiator", entry(INITIAL, 4, 2, 3, 1, 0, FISICO));
        entries.put("hunter", entry(INITIAL, 3, 2, 3, 2, 0, FISICO));
        entries.put("ornius-rider", entry(INITIAL, 3, 2, 4, 2, 0, FISICO, MONTADO));
        entries.put("soldier", entry(INITIAL, 3, 3, 3, 1, 0, FISICO));

        // Specialty
        entries.put("archer", entry(INTERMEDIATE, 4, 2, 3, 2, 0, FISICO));
        entries.put("armored-knight", entry(INTERMEDIATE, 3, 5, 1, 1, 0, TANQUE, FISICO));
        entries.put("armored-ornius-rider", entry(INTERMEDIATE, 3, 4, 4, 1, 0, FISICO, MONTADO, TANQUE));
        entries.put("brigand", entry(INTERMEDIATE, 5, 2, 3, 1, 0, FISICO));
        entries.put("charioteer", entry(INTERMEDIATE, 5, 3, 4, 2, 0, FISICO, MONTADO));
        entries.put("light-cavalry", entry(INTERMEDIATE, 3, 3, 4, 2, 0, FISICO, MONTADO));
        entries.put("myrmidon", entry(INTERMEDIATE, 5, 2, 3, 1, 0, FISICO));
        entries.put("priest", entry(INTERMEDIATE, 2, 2, 2, 3, 5, MAGICO, CURADOR));
        entries.put("pugilist", entry(INTERMEDIATE, 4, 2, 3, 2, 0, FISICO));
        entries.put("rogue", entry(INTERMEDIATE, 3, 2, 4, 4, 0, CERROJO, FISICO));
        entries.put("shaman", entry(INTERMEDIATE, 4, 1, 3, 2, 1, MAGICO));
        entries.put("wing-soldier", entry(INTERMEDIATE, 4, 2, 5, 2, 0, VOLADOR, FISICO));

        // Advanced
        entries.put("bardinger", entry(ADVANCED, 4, 4, 4, 2, 0, FISICO, MONTADO));
        entries.put("bishop", entry(ADVANCED, 2, 2, 2, 3, 5, MAGICO, CURADOR));
        entries.put("blacksmith", entry(ADVANCED, 4, 3, 3, 2, 0, FISICO, MAGICO));
        entries.put("caladrius", entry(ADVANCED, 4, 1, 3, 2, 1, MAGICO, MONTADO));
        entries.put("cataphract", entry(ADVANCED, 4, 5, 4, 1, 0, FISICO, MONTADO, TANQUE));
        entries.put("dancer", entry(ADVANCED, 2, 1, 3, 5, 1, APOYO));
        entries.put("dragoon", entry(ADVANCED, 4, 2, 5, 2, 0, VOLADOR, FISICO));
        entries.put("dreadnought", entry(ADVANCED, 3, 5, 1, 1, 0, TANQUE, FISICO));
        entries.put("elephant-rider", entry(ADVANCED, 5, 5, 3, 2, 0, FISICO, MONTADO, TANQUE));
        entries.put("forest-knight", entry(ADVANCED, 4, 3, 4, 2, 0, FISICO, MONTADO));
        entries.put("guardian", entry(ADVANCED, 3, 4, 2, 2, 4, TANQUE, CURADOR));
        entries.put("ovate", entry(ADVANCED, 4, 1, 3, 3, 3, MAGICO, CURADOR));
        entries.put("ranger", entry(ADVANCED, 3, 2, 4, 4, 0, CERROJO, FISICO));
        entries.put("shido", entry(ADVANCED, 5, 2, 3, 1, 0, FISICO));
        entries.put("sniper", entry(ADVANCED, 5, 2, 3, 2, 0, FISICO));
        entries.put("troubadour", entry(ADVANCED, 3, 2, 4, 4, 4, MAGICO, MONTADO, CURADOR, APOYO));
        entries.put("warrior", entry(ADVANCED, 5, 3, 3, 1, 0, FISICO));

        // Master
        entries.put("battlemaster", entry(MASTER, 5, 3, 3, 2, 0, FISICO));
        entries.put("bau-lord", entry(MASTER, 5, 3, 5, 2, 0, VOLADOR, FISICO));
        entries.put("bow-adept", entry(MASTER, 5, 2, 3, 2, 0, FISICO));
        entries.put("bow-knight", entry(MASTER, 4, 3, 5, 2, 0, FISICO, MONTADO));
        entries.put("castle-knight", entry(MASTER, 3, 5, 1, 2, 0, TANQUE, FISICO));
        entries.put("celestial-trooper", entry(MASTER, 4, 3, 5, 2, 0, VOLADOR, FISICO));
        entries.put("druid", entry(MASTER, 5, 1, 3, 3, 0, MAGICO));
        entries.put("great-knight", entry(MASTER, 4, 5, 4, 1, 0, FISICO, MONTADO, TANQUE));
        entries.put("high-savant", entry(MASTER, 5, 3, 3, 2, 0, FISICO, MAGICO));
        entries.put("orichaldia", entry(MASTER, 4, 4, 5, 2, 0, FISICO, MONTADO));
        entries.put("sentinel", entry(MASTER, 3, 5, 2, 3, 4, TANQUE, CURADOR));
        entries.put("shadow-seeker", entry(MASTER, 4, 2, 4, 5, 0, CERROJO, FISICO));
        entries.put("swordmaster", entry(MASTER, 5, 2, 3, 1, 0, FISICO));
        entries.put("valkyrium", entry(MASTER, 4, 3, 5, 4, 4, MAGICO, MONTADO, CURADOR, APOYO));
        entries.put("war-monk", entry(MASTER, 4, 3, 3, 3, 3, FISICO, CURADOR));
        entries.put("wiseman", entry(MASTER, 2, 2, 2, 4, 5, MAGICO, CURADOR));

        // Divine
        entries.put("the-apsara", entry(DIVINE, 3, 2, 4, 5, 1, APOYO));
        entries.put("the-avatar", entry(DIVINE, 5, 2, 3, 4, 5, MAGICO, CURADOR, APOYO));
        entries.put("the-blade", entry(DIVINE, 5, 2, 3, 1, 0, FISICO));
        entries.put("the-calamity", entry(DIVINE, 4, 2, 4, 5, 0, CERROJO, FISICO));
        entries.put("the-cavalier", entry(DIVINE, 5, 4, 5, 2, 0, FISICO, MONTADO));
        entries.put("the-eternal", entry(DIVINE, 5, 4, 3, 1, 0, FISICO));
        entries.put("the-godhand", entry(DIVINE, 4, 3, 3, 3, 4, FISICO, CURADOR));
        entries.put("the-trident", entry(DIVINE, 3, 5, 2, 3, 5, TANQUE, CURADOR));

        return Collections.unmodifiableMap(entries);
    }

    private static Entry entry(ClassTier tier, int atk, int def, int mov, int sup, int heal,
            ClassTag... tags) {
        return new Entry(tier, new ClassRoleProfile(atk, def, mov, sup, heal, Arrays.asList(tags)));
    }
}
