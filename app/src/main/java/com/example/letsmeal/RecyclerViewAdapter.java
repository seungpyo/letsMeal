package com.example.letsmeal;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letsmeal.dummy.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by songmho on 2015-07-12.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context context;
    List<ItemCard> items;
    int item_layout;

    public RecyclerViewAdapter(Context context, List<ItemCard> items, int item_layout) {
        this.context=context;
        this.items=items;
        this.item_layout=item_layout;
    }

    public RecyclerViewAdapter(Context context, RecyclerViewAdapter src) {
        this.context = context;
        this.items = src.items;
        this.item_layout = src.item_layout;
    }

    public RecyclerViewAdapter(Context context, int item_layout) {
        this.context = context;
        this.items = new ArrayList<>();
        this.item_layout = item_layout;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_layout,null);
        return new ViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ItemCard item=items.get(position);
        /*
        Drawable drawable=context.getResources().getDrawable(item.getImage());
        holder.image.setBackground(drawable);
        */
        holder.title.setText(item.getTitle());
        holder.date.setText(item.getDate());
        holder.time.setText(item.getTime());
        holder.place.setText(item.getPlace());
        // toString() is just a quick fix. Maybe we should change View type of participants.
        // holder.participants.setText(item.getParticipants().toString());
        String participantsDisplay = "";
        for (User participant : item.getParticipants()) {
            participantsDisplay += participant.getName();
            participantsDisplay += ", ";
        }
        holder.participants.setText(participantsDisplay);
        holder.description.setText(item.getDescription());

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,item.getTitle(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        TextView time;
        TextView place;
        TextView participants;
        TextView description;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            place = itemView.findViewById(R.id.place);
            participants = itemView.findViewById(R.id.participants);
            description = itemView.findViewById(R.id.description);
            cardview = itemView.findViewById(R.id.cardview);
        }
    }

    public void addItemCard(ItemCard card) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(card);
        Collections.sort(items, Collections.<ItemCard>reverseOrder());
        int pos = items.indexOf(card);
        assert (pos != -1);
        notifyItemInserted(pos);
        // notifyItemInserted(getItemCount()-1);
    }
}
