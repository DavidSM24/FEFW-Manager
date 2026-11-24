package com.davidsm24.fefwmanager.units;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

record PartyAnalysisResult(int evaluatedCount, Raw raw, Scores scores, List<PartyAlert> alerts) {
    PartyAnalysisResult {
        alerts = Collections.unmodifiableList(new ArrayList<>(alerts));
    }

    boolean isEmpty() {
        return evaluatedCount == 0;
    }

    record Raw(double atk, double def, double mov, double sup, double heal) {
        Scores rounded() {
            return new Scores((int) Math.round(atk), (int) Math.round(def), (int) Math.round(mov),
                    (int) Math.round(sup), (int) Math.round(heal));
        }
    }

    record Scores(int atk, int def, int mov, int sup, int heal) {}
}
