package com.iremia.app.numisight.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.iremia.app.numisight.PaymentDetails;
import com.iremia.app.numisight.R;
import com.iremia.app.numisight.helper.IconAssignment;
import com.iremia.app.numisight.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    final ArrayList<Transaction> paymentsHistory;
    final Context context;

    public PaymentAdapter(ArrayList<Transaction> paymentsHistory, Context context) {
        this.paymentsHistory = paymentsHistory;
        this.context = context;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payments_list_item, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        switch (paymentsHistory.get(position).getType()) {
            case 1:
                holder.paymentAmount.setTextColor(context.getResources().getColor(R.color.green));
                holder.paymentAmount.setText(String.valueOf(paymentsHistory.get(position).getAmount()));
                break;
            case 0:
                String text = "-" + paymentsHistory.get(position).getAmount();
                holder.paymentAmount.setTextColor(context.getResources().getColor(R.color.black));
                holder.paymentAmount.setText(text);
                break;
        }

        if (!holder.paymentDescription.getText().toString().isEmpty() || holder.paymentCategory.getText().toString().equals("Other")) {
            holder.paymentDescription.setText(paymentsHistory.get(position).getDescription());
        } else {
            holder.paymentDescription.setVisibility(View.GONE);
            holder.paymentCategory.setVisibility(View.VISIBLE);
            holder.paymentCategory.setText(paymentsHistory.get(position).getCategory());
        }

        holder.paymentDate.setText(new SimpleDateFormat("EEEE, dd.MM", Locale.getDefault()).format(paymentsHistory.get(position).getTimestamp()));

        IconAssignment icon = new IconAssignment(paymentsHistory.get(position).getCategory());
        holder.categoryIcon.setImageDrawable(AppCompatResources.getDrawable(context, icon.getIconRes()));

        holder.parentLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaymentDetails.class);
            intent.putExtra("paymentId", paymentsHistory.get(holder.getAdapterPosition()).getPaymentId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return paymentsHistory.size();
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        final ImageView categoryIcon;
        final TextView paymentAmount;
        final TextView paymentDescription;
        final TextView paymentCategory;
        final TextView paymentDate;
        final ConstraintLayout parentLayout;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryIcon = itemView.findViewById(R.id.iv_categoryIcon);
            paymentAmount = itemView.findViewById(R.id.tv_amount);
            paymentDescription = itemView.findViewById(R.id.tv_paymentDescription);
            paymentCategory = itemView.findViewById(R.id.tv_paymentCategory);
            paymentDate = itemView.findViewById(R.id.tv_paymentDate);
            parentLayout = itemView.findViewById(R.id.cl_payment_list_item);
        }
    }
}
