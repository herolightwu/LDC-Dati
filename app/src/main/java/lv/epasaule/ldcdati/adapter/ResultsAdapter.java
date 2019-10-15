package lv.epasaule.ldcdati.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import lv.epasaule.ldcdati.R;

public class ResultsAdapter extends RecyclerView.Adapter<ResultViewHolder> {

    private final List<ResultRow> mItems = new ArrayList<>();

    public ResultsAdapter(List<ResultRow> rows) {
        mItems.addAll(rows);
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false);
        return new ResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        ResultRow resultRow = mItems.get(position);
        holder.tvKey().setText(resultRow.key());
        holder.tvValue().setText(resultRow.value());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void set(List<ResultRow> resultRows) {
        int prevSize = mItems.size();
        this.mItems.clear();
        this.mItems.addAll(resultRows);
        notifyItemRangeRemoved(0, prevSize);
        notifyItemRangeInserted(0, resultRows.size());
    }

    public ResultRow getItem(int position) {
        return mItems.get(position);
    }

}
