package com.davidsm24.fefwmanager.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class PartyAnalyzer {
    private static final double MIN_SCORE = 1.0;
    private static final double MAX_SCORE = 5.0;
    private static final int MIN_EFFECTIVE_SIZE = 5;
    private static final double HEAL_TARGET_PER_UNIT = 0.85;
    private static final double SUPPORT_TARGET_PER_UNIT = 0.75;
    private static final double REDUNDANT_LOCKPICK_WEIGHT = 0.5;
    private static final int LARGE_PARTY_SIZE = 9;
    private static final int STANDARD_FRONT_SIZE = 2;
    private static final int LARGE_FRONT_SIZE = 3;
    private static final double FRONTLINE_WEIGHT = 0.6;
    private static final double BACKLINE_WEIGHT = 0.4;
    private static final int ATTACKER_THRESHOLD = 3;
    private static final double MAGIC_BONUS_RATIO = 0.25;
    private static final double DIVERSE_ATTACK_BONUS = 0.3;
    private static final double LIMITED_ATTACK_CAP = 4.0;
    private static final double MOBILITY_BONUS_RATIO = 0.2;
    private static final double MOBILITY_BONUS = 0.5;
    private static final int LOW_HEALING_THRESHOLD = 2;
    private static final int FRONTLINE_DEFENSE_THRESHOLD = 4;

    private PartyAnalyzer() {}

    static PartyAnalysisResult analyze(List<ClassRoleProfile> profiles) {
        int count = profiles.size();
        if (count == 0) {
            PartyAnalysisResult.Raw raw = new PartyAnalysisResult.Raw(
                    MIN_SCORE, MIN_SCORE, MIN_SCORE, MIN_SCORE, MIN_SCORE);
            return new PartyAnalysisResult(0, raw, raw.rounded(), Collections.emptyList());
        }

        int[] defenses = new int[count];
        int attackSum = 0;
        int mobilitySum = 0;
        int healingSum = 0;
        double adjustedSupportSum = 0.0;
        int attackers = 0;
        int magicAttackers = 0;
        boolean hasPhysicalDamage = false;
        int mobileUnits = 0;
        int lockpickers = 0;

        int defenseIndex = 0;
        for (ClassRoleProfile profile : profiles) {
            List<ClassTag> tags = profile.tags();
            attackSum += profile.atk();
            defenses[defenseIndex++] = profile.def();
            mobilitySum += profile.mov();
            healingSum += profile.heal();

            if (profile.atk() >= ATTACKER_THRESHOLD) {
                attackers++;
                if (tags.contains(ClassTag.MAGICO)) {
                    magicAttackers++;
                }
                hasPhysicalDamage |= tags.contains(ClassTag.FISICO);
            }
            if (tags.contains(ClassTag.VOLADOR) || tags.contains(ClassTag.MONTADO)) {
                mobileUnits++;
            }
            if (tags.contains(ClassTag.CERROJO)) {
                lockpickers++;
                adjustedSupportSum += profile.sup()
                        * (lockpickers == 1 ? 1.0 : REDUNDANT_LOCKPICK_WEIGHT);
            } else {
                adjustedSupportSum += profile.sup();
            }
        }

        boolean hasMagicDamage = magicAttackers > 0;
        double attack = (double) attackSum / count;
        if (hasPhysicalDamage && hasMagicDamage) {
            if ((double) magicAttackers / attackers >= MAGIC_BONUS_RATIO) {
                attack += DIVERSE_ATTACK_BONUS;
            }
        } else {
            attack = Math.min(attack, LIMITED_ATTACK_CAP);
        }

        Arrays.sort(defenses);
        double mobileRatio = (double) mobileUnits / count;
        double mobility = (double) mobilitySum / count;
        if (mobileRatio >= MOBILITY_BONUS_RATIO) {
            mobility += MOBILITY_BONUS;
        }
        int effectiveSize = Math.max(MIN_EFFECTIVE_SIZE, count);
        PartyAnalysisResult.Raw raw = new PartyAnalysisResult.Raw(
                clamp(attack), defenseScore(defenses), clamp(mobility),
                coverage(adjustedSupportSum, effectiveSize, SUPPORT_TARGET_PER_UNIT),
                coverage(healingSum, effectiveSize, HEAL_TARGET_PER_UNIT));
        PartyAnalysisResult.Scores scores = raw.rounded();

        List<PartyAlert> alerts = new ArrayList<>();
        if (!hasMagicDamage) {
            alerts.add(PartyAlert.MISSING_MAGIC_DAMAGE);
        }
        if (!hasPhysicalDamage) {
            alerts.add(PartyAlert.MISSING_PHYSICAL_DAMAGE);
        }
        if (scores.heal() <= LOW_HEALING_THRESHOLD) {
            alerts.add(PartyAlert.LOW_HEALING);
        }
        if (defenses[count - 1] < FRONTLINE_DEFENSE_THRESHOLD) {
            alerts.add(PartyAlert.NO_FRONTLINE);
        }
        if (mobileUnits == 0) {
            alerts.add(PartyAlert.NO_MOBILE_UNITS);
        }
        if (lockpickers > 1) {
            alerts.add(PartyAlert.REDUNDANT_LOCKPICKING);
        }
        return new PartyAnalysisResult(count, raw, scores, alerts);
    }

    private static double defenseScore(int[] sortedDefenses) {
        int count = sortedDefenses.length;
        int frontSize = count >= LARGE_PARTY_SIZE ? LARGE_FRONT_SIZE : STANDARD_FRONT_SIZE;
        int frontCount = Math.min(count, frontSize);
        int backCount = count - frontCount;
        int frontSum = 0;
        int backSum = 0;
        for (int index = 0; index < backCount; index++) {
            backSum += sortedDefenses[index];
        }
        for (int index = backCount; index < count; index++) {
            frontSum += sortedDefenses[index];
        }
        double frontAverage = (double) frontSum / frontCount;
        double backAverage = backCount == 0 ? frontAverage : (double) backSum / backCount;
        return clamp(frontAverage * FRONTLINE_WEIGHT + backAverage * BACKLINE_WEIGHT);
    }

    private static double coverage(double sum, int size, double targetPerUnit) {
        return MIN_SCORE + (MAX_SCORE - MIN_SCORE) * Math.min(1.0, sum / (size * targetPerUnit));
    }

    private static double clamp(double score) {
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }
}
