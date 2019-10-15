package lv.epasaule.ldcdati.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lv.epasaule.ldcdati.R;

public class ResultViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_key) TextView mTVKey;
    @BindView(R.id.tv_value) TextView mTVValue;

    public ResultViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @NonNull
    public View rootView() {
        return itemView;
    }

    @NonNull
    public TextView tvKey() {
        return mTVKey;
    }

    @NonNull
    public TextView tvValue() {
        return mTVValue;
    }
}
