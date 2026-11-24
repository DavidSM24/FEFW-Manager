package com.davidsm24.fefwmanager.units;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

public final class PartyAnalyzerTest {
    private static final double TOLERANCE = 1e-9;
    private static final ClassRoleProfile UNTYPED = profile(0, 0, 0, 0, 0);
    private static final ClassRoleProfile PHYSICAL_ATTACKER = profile(5, 0, 0, 0, 0, ClassTag.FISICO);
    private static final ClassRoleProfile MAGIC_ATTACKER = profile(4, 0, 0, 0, 0, ClassTag.MAGICO);

    @Test
    public void emptyPartyUsesBaselineScoresWithoutAlerts() {
        PartyAnalysisResult result = PartyAnalyzer.analyze(List.of());

        assertEquals(0, result.evaluatedCount());
        assertTrue(result.isEmpty());
        assertEquals(new PartyAnalysisResult.Raw(1.0, 1.0, 1.0, 1.0, 1.0), result.raw());
        assertEquals(new PartyAnalysisResult.Scores(1, 1, 1, 1, 1), result.scores());
        assertTrue(result.alerts().isEmpty());
    }

    @Test
    public void coverageUsesTheLargerOfFiveAndTheEvaluatedCountAndSaturatesAtFive() {
        ClassRoleProfile healer = profile(2, 4, 2, 1, 3);
        PartyAnalysisResult small = PartyAnalyzer.analyze(List.of(healer));
        PartyAnalysisResult large = PartyAnalyzer.analyze(
                List.of(healer, UNTYPED, UNTYPED, UNTYPED, UNTYPED, UNTYPED));
        PartyAnalysisResult saturated = PartyAnalyzer.analyze(
                Collections.nCopies(6, profile(2, 4, 2, 5, 5)));

        assertEquals(1, small.evaluatedCount());
        assertFalse(small.isEmpty());
        assertEquals(3.823529411764706, small.raw().heal(), TOLERANCE);
        assertEquals(2.066666666666667, small.raw().sup(), TOLERANCE);
        assertEquals(4, small.scores().heal());
        assertEquals(2, small.scores().sup());
        assertEquals(6, large.evaluatedCount());
        assertEquals(3.3529411764705883, large.raw().heal(), TOLERANCE);
        assertEquals(1.8888888888888888, large.raw().sup(), TOLERANCE);
        assertEquals(5.0, saturated.raw().heal(), TOLERANCE);
        assertEquals(5.0, saturated.raw().sup(), TOLERANCE);
    }

    @Test
    public void supportDiscountsEveryLockpickerAfterTheFirst() {
        ClassRoleProfile first = profile(0, 0, 0, 1, 0, ClassTag.CERROJO);
        ClassRoleProfile second = profile(0, 0, 0, 2, 0, ClassTag.CERROJO);
        ClassRoleProfile third = profile(0, 0, 0, 1, 0, ClassTag.CERROJO);
        ClassRoleProfile supporter = profile(0, 0, 0, 1, 0, ClassTag.APOYO);

        PartyAnalysisResult result = PartyAnalyzer.analyze(List.of(supporter, first, second, third));
        assertEquals(4.733333333333333, result.raw().sup(), TOLERANCE);
        assertEquals(5, result.scores().sup());
        assertTrue(result.alerts().contains(PartyAlert.REDUNDANT_LOCKPICKING));
        assertFalse(PartyAnalyzer.analyze(List.of(first)).alerts()
                .contains(PartyAlert.REDUNDANT_LOCKPICKING));
    }

    @Test
    public void defenseUsesTheBestTwoOrBestThreeFromNineUnits() {
        ClassRoleProfile tank = profile(0, 5, 0, 0, 0);
        ClassRoleProfile medium = profile(0, 3, 0, 0, 0);
        ClassRoleProfile light = profile(0, 1, 0, 0, 0);
        PartyAnalysisResult eight = PartyAnalyzer.analyze(
                List.of(light, tank, light, light, medium, light, light, tank));
        PartyAnalysisResult nine = PartyAnalyzer.analyze(
                List.of(light, tank, light, light, medium, light, light, tank, light));

        assertEquals(3.533333333333333, eight.raw().def(), TOLERANCE);
        assertEquals(4, eight.scores().def());
        assertEquals(3.0, nine.raw().def(), TOLERANCE);
        assertEquals(5.0, PartyAnalyzer.analyze(List.of(tank)).raw().def(), TOLERANCE);
        assertEquals(4.0, PartyAnalyzer.analyze(List.of(tank, medium)).raw().def(), TOLERANCE);
        assertEquals(1.0, PartyAnalyzer.analyze(List.of(UNTYPED)).raw().def(), TOLERANCE);
    }

    @Test
    public void attackBonusUsesOnlyAttackersForTheMagicRatio() {
        ClassRoleProfile lowAttackMage = profile(2, 0, 0, 0, 0, ClassTag.MAGICO);
        PartyAnalysisResult atThreshold = PartyAnalyzer.analyze(List.of(
                PHYSICAL_ATTACKER, PHYSICAL_ATTACKER, PHYSICAL_ATTACKER, MAGIC_ATTACKER, lowAttackMage));
        PartyAnalysisResult belowThreshold = PartyAnalyzer.analyze(List.of(
                PHYSICAL_ATTACKER, PHYSICAL_ATTACKER, PHYSICAL_ATTACKER, PHYSICAL_ATTACKER, MAGIC_ATTACKER));
        ClassRoleProfile hybrid = profile(5, 0, 0, 0, 0, ClassTag.FISICO, ClassTag.MAGICO);
        PartyAnalysisResult withHybrid = PartyAnalyzer.analyze(List.of(hybrid));

        assertEquals(4.5, atThreshold.raw().atk(), TOLERANCE);
        assertEquals(5, atThreshold.scores().atk());
        assertEquals(4.8, belowThreshold.raw().atk(), TOLERANCE);
        assertEquals(5.0, withHybrid.raw().atk(), TOLERANCE);
        assertFalse(withHybrid.alerts().contains(PartyAlert.MISSING_MAGIC_DAMAGE));
        assertFalse(withHybrid.alerts().contains(PartyAlert.MISSING_PHYSICAL_DAMAGE));
    }

    @Test
    public void missingEitherDamageTypeCapsAttackAndEmitsItsAlert() {
        ClassRoleProfile lowAttackMage = profile(2, 0, 0, 0, 0, ClassTag.MAGICO);
        ClassRoleProfile strongMage = profile(5, 0, 0, 0, 0, ClassTag.MAGICO);
        PartyAnalysisResult physicalOnly = PartyAnalyzer.analyze(
                List.of(PHYSICAL_ATTACKER, PHYSICAL_ATTACKER, PHYSICAL_ATTACKER, lowAttackMage));
        PartyAnalysisResult magicOnly = PartyAnalyzer.analyze(List.of(strongMage));

        assertEquals(4.0, physicalOnly.raw().atk(), TOLERANCE);
        assertTrue(physicalOnly.alerts().contains(PartyAlert.MISSING_MAGIC_DAMAGE));
        assertFalse(physicalOnly.alerts().contains(PartyAlert.MISSING_PHYSICAL_DAMAGE));
        assertEquals(4.0, magicOnly.raw().atk(), TOLERANCE);
        assertTrue(magicOnly.alerts().contains(PartyAlert.MISSING_PHYSICAL_DAMAGE));
        assertFalse(magicOnly.alerts().contains(PartyAlert.MISSING_MAGIC_DAMAGE));
    }

    @Test
    public void mobilityCountsAnAirborneMountedUnitOnceAndAppliesTheThreshold() {
        ClassRoleProfile mobile = profile(0, 0, 5, 0, 0, ClassTag.VOLADOR, ClassTag.MONTADO);
        ClassRoleProfile ground = profile(0, 0, 2, 0, 0);
        PartyAnalysisResult atThreshold = PartyAnalyzer.analyze(List.of(mobile, ground, ground, ground, ground));
        PartyAnalysisResult belowThreshold = PartyAnalyzer.analyze(
                List.of(mobile, ground, ground, ground, ground, ground));

        assertEquals(3.1, atThreshold.raw().mov(), TOLERANCE);
        assertEquals(2.5, belowThreshold.raw().mov(), TOLERANCE);
        assertEquals(3, belowThreshold.scores().mov());
        assertFalse(belowThreshold.alerts().contains(PartyAlert.NO_MOBILE_UNITS));
        assertEquals(5.0, PartyAnalyzer.analyze(List.of(mobile)).raw().mov(), TOLERANCE);
    }

    @Test
    public void alertsCoverEachTacticalWeaknessIndependently() {
        ClassRoleProfile lockpicker = profile(2, 3, 2, 1, 0, ClassTag.CERROJO);
        PartyAnalysisResult result = PartyAnalyzer.analyze(List.of(lockpicker, lockpicker));

        assertEquals(List.of(
                PartyAlert.MISSING_MAGIC_DAMAGE,
                PartyAlert.MISSING_PHYSICAL_DAMAGE,
                PartyAlert.LOW_HEALING,
                PartyAlert.NO_FRONTLINE,
                PartyAlert.NO_MOBILE_UNITS,
                PartyAlert.REDUNDANT_LOCKPICKING), result.alerts());
    }

    @Test
    public void healingAlertUsesRoundedScoreAndDefenseNeedsOneQualifiedUnit() {
        PartyAnalysisResult low = PartyAnalyzer.analyze(List.of(profile(0, 4, 0, 0, 1)));
        PartyAnalysisResult enough = PartyAnalyzer.analyze(List.of(profile(
                3, 4, 3, 1, 2, ClassTag.FISICO, ClassTag.MAGICO, ClassTag.MONTADO)));

        assertEquals(2, low.scores().heal());
        assertTrue(low.alerts().contains(PartyAlert.LOW_HEALING));
        assertFalse(low.alerts().contains(PartyAlert.NO_FRONTLINE));
        assertEquals(3, enough.scores().heal());
        assertTrue(enough.alerts().isEmpty());
    }

    private static ClassRoleProfile profile(int atk, int def, int mov, int sup, int heal, ClassTag... tags) {
        return new ClassRoleProfile(atk, def, mov, sup, heal, List.of(tags));
    }
}
