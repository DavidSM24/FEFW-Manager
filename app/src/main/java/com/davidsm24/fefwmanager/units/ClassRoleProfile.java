package com.davidsm24.fefwmanager.units;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

record ClassRoleProfile(int atk, int def, int mov, int sup, int heal, List<ClassTag> tags) {
    ClassRoleProfile {
        for (int score : new int[]{atk, def, mov, sup, heal}) {
            if (score < 0 || score > 5) {
                throw new IllegalArgumentException("Class role scores must be between 0 and 5");
            }
        }
        tags = Collections.unmodifiableList(new ArrayList<>(tags));
    }
}
