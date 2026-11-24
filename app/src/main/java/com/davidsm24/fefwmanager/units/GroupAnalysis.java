package com.davidsm24.fefwmanager.units;

import java.util.List;

record GroupAnalysis(int evaluatedUnits, int totalUnits, int atk, int def, int mov, int sup, int heal) {
    static GroupAnalysis evaluate(List<String> classIds) {
        int evaluated = 0;
        int[] best = new int[5];
        int[] secondBest = new int[5];

        for (String classId : classIds) {
            ClassRoleProfile profile = ClassRoleCatalog.forClass(classId);
            if (profile == null) {
                continue;
            }
            evaluated++;
            int[] scores = {profile.atk(), profile.def(), profile.mov(), profile.sup(), profile.heal()};
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] >= best[i]) {
                    secondBest[i] = best[i];
                    best[i] = scores[i];
                } else {
                    secondBest[i] = Math.max(secondBest[i], scores[i]);
                }
            }
        }

        // Use the top two contributors so a specialist is visible even in a large roster.
        int contributors = Math.min(2, evaluated);
        if (contributors > 0) {
            for (int i = 0; i < best.length; i++) {
                best[i] = Math.max(1, Math.round((best[i] + secondBest[i]) / (float) contributors));
            }
        }
        return new GroupAnalysis(evaluated, classIds.size(), best[0], best[1], best[2], best[3], best[4]);
    }
}
