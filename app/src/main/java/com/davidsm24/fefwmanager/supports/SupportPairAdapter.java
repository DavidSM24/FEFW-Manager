package com.davidsm24.fefwmanager.supports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.ColorInt;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.SupportPair;
import com.davidsm24.fefwmanager.databinding.ItemSupportPairBinding;
import com.davidsm24.fefwmanager.databinding.ViewSupportMemberBinding;
import com.davidsm24.fefwmanager.units.UnitLabel;
import com.davidsm24.fefwmanager.units.UnitPortraits;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

final class SupportPairAdapter extends BaseAdapter {
    record Member(UnitLabel unit, boolean recruited) {
    }

    record Row(SupportPair pair, Member first, Member second) {
    }

    private final LayoutInflater inflater;
    private final int accent;
    private final Consumer<Row> onRemove;
    private List<Row> rows = Collections.emptyList();
    private boolean canRemove;

    SupportPairAdapter(LayoutInflater inflater, @ColorInt int accent, Consumer<Row> onRemove) {
        this.inflater = inflater;
        this.accent = accent;
        this.onRemove = onRemove;
    }

    void show(List<Row> rows, boolean canRemove) {
        this.rows = rows;
        this.canRemove = canRemove;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Row getItem(int position) {
        return rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View recycled, ViewGroup parent) {
        ItemSupportPairBinding binding;
        if (recycled == null) {
            binding = ItemSupportPairBinding.inflate(inflater, parent, false);
            binding.getRoot().setTag(binding);
        } else {
            binding = (ItemSupportPairBinding) recycled.getTag();
        }
        Row row = getItem(position);
        bind(binding.firstMember, row.first());
        bind(binding.secondMember, row.second());
        binding.removeSupport.setEnabled(canRemove);
        binding.removeSupport.setContentDescription(parent.getContext().getString(
                R.string.remove_support_named, row.first().unit().name(), row.second().unit().name()));
        binding.removeSupport.setOnClickListener(view -> onRemove.accept(row));
        return binding.getRoot();
    }

    private void bind(ViewSupportMemberBinding binding, Member member) {
        binding.supportMemberName.setText(member.unit().name());
        UnitPortraits.bind(binding.supportMemberPortrait, member.unit(), accent);
        binding.supportMemberStatus.setVisibility(member.recruited() ? View.GONE : View.VISIBLE);
    }
}
