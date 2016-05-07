package woverines.sfsuapp.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import woverines.sfsuapp.R;
import woverines.sfsuapp.database.Staff;

public class StaffDirectoryAdapter extends RecyclerView.Adapter<StaffDirectoryAdapter.Holder> {

    private List<Staff> directory;
    private StaffDirectoryListener listener;

    public StaffDirectoryAdapter(List<Staff> directory, StaffDirectoryListener listener) {
        this.directory = directory;
        this.listener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.staff_directory_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        Staff staff = directory.get(position);

        holder.name.setText(staff.name);
        holder.phone.setText(staff.phone);
        holder.email.setText(staff.email);

        if (listener != null) {
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return directory.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView phone;
        public TextView email;

        public Holder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            email = (TextView) itemView.findViewById(R.id.email);
        }
    }

    public interface StaffDirectoryListener {

        void onClick(int position);
    }
}
