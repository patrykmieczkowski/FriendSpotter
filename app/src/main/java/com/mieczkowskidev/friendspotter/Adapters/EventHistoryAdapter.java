package com.mieczkowskidev.friendspotter.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mieczkowskidev.friendspotter.R;

import java.util.List;

/**
 * Created by Patryk Mieczkowski on 2015-12-28
 */
public class EventHistoryAdapter extends RecyclerView.Adapter<EventHistoryAdapter.EventHolder> {

    public static final String TAG = EventHistoryAdapter.class.getSimpleName();

    private Context context;
    private List<String> stringList;

    public EventHistoryAdapter(Context context, List<String> mojalista) {
        this.context = context;
        stringList = mojalista;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_history_item, parent, false);
        return new EventHolder(v);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {

        holder.eventTitle.setText(stringList.get(position));

    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public static class EventHolder extends RecyclerView.ViewHolder {

        TextView eventTitle;

        public EventHolder(View itemView) {
            super(itemView);

            eventTitle = (TextView) itemView.findViewById(R.id.event_item_title);
        }
    }

}
