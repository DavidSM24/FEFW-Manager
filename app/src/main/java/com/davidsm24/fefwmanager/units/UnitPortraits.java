package com.davidsm24.fefwmanager.units;

import android.content.Context;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.databinding.ViewUnitPortraitBinding;
import com.davidsm24.fefwmanager.route.Route;

import java.util.function.Consumer;

public final class UnitPortraits {
    private UnitPortraits() {
    }

    public static void bind(ViewUnitPortraitBinding binding, Route route) {
        Context context = binding.getRoot().getContext();
        bind(binding, new UnitLabel(route.id, context.getString(route.nameResource)),
                context.getColor(route.accentResource));
    }

    public static void enableDetails(ViewUnitPortraitBinding binding, Route route,
            Consumer<UnitLabel> onDetails, @Nullable View.OnClickListener onTap) {
        Context context = binding.getRoot().getContext();
        UnitLabel unit = new UnitLabel(route.id, context.getString(route.nameResource));
        enableDetails(binding, unit, onDetails, onTap);
    }

    public static void enableDetails(ViewUnitPortraitBinding binding, UnitLabel unit,
            Consumer<UnitLabel> onDetails, @Nullable View.OnClickListener onTap) {
        HoldImageButton button = binding.inspectUnitPortrait;
        button.cancelHold();
        button.setContentDescription(button.getContext().getString(R.string.unit_portrait_details,
                unit.name()));
        button.setOnClickListener(onTap);
        button.setClickable(onTap != null);
        button.setOnLongClickListener(view -> {
            onDetails.accept(unit);
            return true;
        });
        button.setVisibility(View.VISIBLE);
    }

    public static void bind(ViewUnitPortraitBinding binding, UnitLabel unit, @ColorInt int accent) {
        int resource = resourceFor(unit.id());
        boolean hasPortrait = resource != 0;
        binding.getRoot().setClipToOutline(true);
        binding.unitPortraitImage.setImageResource(resource);
        binding.unitPortraitImage.setVisibility(hasPortrait ? View.VISIBLE : View.GONE);
        binding.unitPortraitInitial.setText(hasPortrait ? "" : unit.initial());
        binding.unitPortraitInitial.setTextColor(accent);
        binding.unitPortraitInitial.setVisibility(hasPortrait ? View.GONE : View.VISIBLE);
    }

    @DrawableRes
    public static int resourceFor(String unitId) {
        return switch (unitId) {
            case "alexandra" -> R.drawable.portrait_alexandra;
            case "anatolia" -> R.drawable.portrait_anatolia;
            case "aswan" -> R.drawable.portrait_aswan;
            case "benditz" -> R.drawable.portrait_benditz;
            case "bertrand" -> R.drawable.portrait_bertrand;
            case "bonaventure" -> R.drawable.portrait_bonaventure;
            case "buccar" -> R.drawable.portrait_buccar;
            case "cai" -> R.drawable.portrait_cai;
            case "catania" -> R.drawable.portrait_catania;
            case "centurio" -> R.drawable.portrait_centurio;
            case "creek" -> R.drawable.portrait_creek;
            case "dadao" -> R.drawable.portrait_dadao;
            case "dante" -> R.drawable.portrait_dante;
            case "diego" -> R.drawable.portrait_diego;
            case "dietrich" -> R.drawable.portrait_dietrich;
            case "eshmel" -> R.drawable.portrait_eshmel;
            case "esmeralda" -> R.drawable.portrait_esmeralda;
            case "fabio" -> R.drawable.portrait_fabio;
            case "fianna" -> R.drawable.portrait_fianna;
            case "gaitz" -> R.drawable.portrait_gaitz;
            case "goliath" -> R.drawable.portrait_goliath;
            case "guzran" -> R.drawable.portrait_guzran;
            case "halvin" -> R.drawable.portrait_halvin;
            case "hong-hua" -> R.drawable.portrait_hong_hua;
            case "inyoni" -> R.drawable.portrait_inyoni;
            case "io" -> R.drawable.portrait_io;
            case "jasmine" -> R.drawable.portrait_jasmine;
            case "jester" -> R.drawable.portrait_jester;
            case "kiroc" -> R.drawable.portrait_kiroc;
            case "klapka" -> R.drawable.portrait_klapka;
            case "leda" -> R.drawable.portrait_leda;
            case "lilian" -> R.drawable.portrait_lilian;
            case "loretta" -> R.drawable.portrait_loretta;
            case "ludia" -> R.drawable.portrait_ludia;
            case "lysander" -> R.drawable.portrait_lysander;
            case "majide" -> R.drawable.portrait_majide;
            case "mikaela" -> R.drawable.portrait_mikaela;
            case "mu" -> R.drawable.portrait_mu;
            case "nathan" -> R.drawable.portrait_nathan;
            case "nezha" -> R.drawable.portrait_nezha;
            case "ninae" -> R.drawable.portrait_ninae;
            case "noctula" -> R.drawable.portrait_noctula;
            case "nuzzuo" -> R.drawable.portrait_nuzzuo;
            case "nydine" -> R.drawable.portrait_nydine;
            case "olympia" -> R.drawable.portrait_olympia;
            case "orchel" -> R.drawable.portrait_orchel;
            case "peppe" -> R.drawable.portrait_peppe;
            case "peter" -> R.drawable.portrait_peter;
            case "seteth" -> R.drawable.portrait_seteth;
            case "sha-lan" -> R.drawable.portrait_sha_lan;
            case "simon" -> R.drawable.portrait_simon;
            case "sirocco" -> R.drawable.portrait_sirocco;
            case "sofia" -> R.drawable.portrait_sofia;
            case "tahonia" -> R.drawable.portrait_tahonia;
            case "talimun" -> R.drawable.portrait_talimun;
            case "theodora" -> R.drawable.portrait_theodora;
            case "tialla" -> R.drawable.portrait_tialla;
            case "tobias" -> R.drawable.portrait_tobias;
            case "troy" -> R.drawable.portrait_troy;
            case "ultand" -> R.drawable.portrait_ultand;
            case "ursula" -> R.drawable.portrait_ursula;
            case "yang-jie" -> R.drawable.portrait_yang_jie;
            case "zarcone" -> R.drawable.portrait_zarcone;
            default -> 0;
        };
    }
}
