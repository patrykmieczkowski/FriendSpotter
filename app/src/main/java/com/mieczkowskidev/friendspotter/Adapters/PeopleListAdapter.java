package com.mieczkowskidev.friendspotter.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mieczkowskidev.friendspotter.Fragments.PeopleFragment;
import com.mieczkowskidev.friendspotter.R;
import com.trnql.smart.people.PersonEntry;

import java.util.List;

/**
 * Created by Patryk Mieczkowski on 2015-12-28
 */
public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.EventHolder> {

    public static final String TAG = PeopleListAdapter.class.getSimpleName();

    private Context context;
    private List<PersonEntry> personEntryList;

    public PeopleListAdapter(Context context, List<PersonEntry> personEntryList) {
        this.context = context;
        this.personEntryList = personEntryList;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_item, parent, false);
        return new EventHolder(v);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {

        holder.username.setText(personEntryList.get(position).getUserToken());

        holder.description.setText(personEntryList.get(position).getActivityString());

    }

    @Override
    public int getItemCount() {
        return personEntryList.size();
    }

    public static class EventHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView description;
        ImageView image;

        public EventHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.people_item_username);
            description = (TextView) itemView.findViewById(R.id.people_item_description);
            image = (ImageView) itemView.findViewById(R.id.people_item_image);
        }
    }

}
