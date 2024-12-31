package com.iremia.app.numisight.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iremia.app.numisight.AddOrEditAccount;
import com.iremia.app.numisight.R;
import com.iremia.app.numisight.model.Account;

import java.util.ArrayList;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    final ArrayList<Account> accountsList;
    final Context context;

    public AccountAdapter(ArrayList<Account> accountsList, Context context) {
        this.accountsList = accountsList;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accounts_editor_list_item, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        String name = accountsList.get(position).getAccountLabel() + " " + accountsList.get(position).getCurrency();
        holder.accountName.setText(name);

        holder.dbName.setText(accountsList.get(position).getDbReference());

        holder.parentLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddOrEditAccount.class);
            intent.putExtra("database_name", accountsList.get(holder.getAdapterPosition()).getDbReference());
            intent.putExtra("isEditForm", true);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return accountsList.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        final TextView accountName;
        final TextView dbName;
        final RelativeLayout parentLayout;

        public AccountViewHolder(View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.tv_account_name);
            dbName = itemView.findViewById(R.id.tv_db_name);
            parentLayout = itemView.findViewById(R.id.rl_accounts_editor_list_item);
        }
    }
}