package com.example.a402413138_mad700_pantrymanagerassignment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying pantry ingredients, expiry status badges,
 * and edit/delete action triggers.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.Holder> {

    /**
     * Interface definition for callbacks to edit or update pantry items.
     */
    public interface Listener {
        void onEdit(PantryItem item);
        void onChanged();
    }

    private final List<PantryItem> items;
    private final DatabaseHelper db;
    private final Listener listener;

    public PantryAdapter(List<PantryItem> items, DatabaseHelper db, Listener listener) {
        this.items = items;
        this.db = db;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pantry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        PantryItem item = items.get(position);
        h.name.setText(item.name);

        // Display quantity, unit, and optional expiration date string
        String quantityText = item.quantity + " " + item.unit + 
                (item.expiry == null || item.expiry.isEmpty() ? "" : " • Expires " + item.expiry);
        h.quantity.setText(quantityText);

        // Check if user enabled expiration reminders in settings
        SharedPreferences prefs = h.itemView.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean showReminder = prefs.getBoolean("expiry_reminder", true);

        if (showReminder) {
            String reminderText = getExpiryReminderText(item.expiry);
            if (reminderText != null) {
                h.expiryReminder.setVisibility(View.VISIBLE);
                h.expiryReminder.setText(reminderText);
            } else {
                h.expiryReminder.setVisibility(View.GONE);
            }
        } else {
            h.expiryReminder.setVisibility(View.GONE);
        }

        // Edit button listener
        h.edit.setOnClickListener(v -> listener.onEdit(item));

        // Delete button listener with confirmation dialog
        h.delete.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Delete ingredient?")
                .setMessage("Remove " + item.name + " from your pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> {
                    db.deletePantryItem(item.id);
                    listener.onChanged();
                })
                .show());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Calculates remaining days until expiration and returns an appropriate warning label.
     *
     * @param expiryStr Expiration date formatted as YYYY-MM-DD
     * @return Formatted reminder string or null if not expiring soon/invalid
     */
    private String getExpiryReminderText(String expiryStr) {
        if (expiryStr == null || !expiryStr.matches("\\d{4}-\\d{2}-\\d{2}")) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(expiryStr);
            if (date == null) return null;

            // Zero out time components for date comparison
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar expDate = Calendar.getInstance();
            expDate.setTime(date);
            expDate.set(Calendar.HOUR_OF_DAY, 0);
            expDate.set(Calendar.MINUTE, 0);
            expDate.set(Calendar.SECOND, 0);
            expDate.set(Calendar.MILLISECOND, 0);

            long diffMillis = expDate.getTimeInMillis() - today.getTimeInMillis();
            long diffDays = diffMillis / (24 * 60 * 60 * 1000);

            if (diffDays < 0) {
                return "⚠️ Expired on " + expiryStr;
            } else if (diffDays == 0) {
                return "⚠️ Expiring today!";
            } else if (diffDays == 1) {
                return "⚠️ Expiring tomorrow!";
            } else if (diffDays <= 3) {
                return "⚠️ Expiring in " + diffDays + " days (" + expiryStr + ")";
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * ViewHolder for pantry item rows.
     */
    static class Holder extends RecyclerView.ViewHolder {
        TextView name, quantity, expiryReminder;
        Button edit, delete;

        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.txtName);
            quantity = v.findViewById(R.id.txtQuantity);
            expiryReminder = v.findViewById(R.id.txtExpiryReminder);
            edit = v.findViewById(R.id.btnEdit);
            delete = v.findViewById(R.id.btnDelete);
        }
    }
}
