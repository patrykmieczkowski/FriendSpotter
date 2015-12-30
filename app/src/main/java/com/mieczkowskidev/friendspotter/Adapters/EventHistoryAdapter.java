package com.mieczkowskidev.friendspotter.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mieczkowskidev.friendspotter.Config;
import com.mieczkowskidev.friendspotter.Objects.Event;
import com.mieczkowskidev.friendspotter.R;
import com.mieczkowskidev.friendspotter.Utils.LoginManager;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Patryk Mieczkowski on 2015-12-28
 */
public class EventHistoryAdapter extends RecyclerView.Adapter<EventHistoryAdapter.EventHolder> {

    public static final String TAG = EventHistoryAdapter.class.getSimpleName();

    private Context context;
    private List<Event> eventList;

    public EventHistoryAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_history_item, parent, false);
        return new EventHolder(v);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {

        holder.eventTitle.setText(eventList.get(position).getTitle());

        String url = Config.RestAPI + "/" + eventList.get(position).getThumbnail();
        Picasso.with(context).load(url).into(holder.eventImage);

        holder.eventDescription.setText(eventList.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventHolder extends RecyclerView.ViewHolder {

        TextView eventTitle;
        TextView eventDescription;
        ImageView eventImage;

        public EventHolder(View itemView) {
            super(itemView);

            eventTitle = (TextView) itemView.findViewById(R.id.event_item_title);
            eventDescription = (TextView) itemView.findViewById(R.id.event_item_address);
            eventImage = (ImageView) itemView.findViewById(R.id.event_item_image);
        }
    }

}
