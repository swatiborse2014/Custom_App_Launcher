package com.example.customlauncher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customlauncher.R;
import com.example.customlauncher.common.ApkInfoExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.ViewHolder> implements Filterable {

    private final Context context1;
    private final List<String> mListAppInfo;
    private ApkInfoExtractor apkInfoExtractor;
    private String name;

    public AppInfoAdapter(Context context, List<String> list) {
        context1 = context;
        mListAppInfo = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView imageView;
        public TextView textView_App_Name;
        public TextView textView_App_Package_Name;
        public TextView textView_App_Status;

        public ViewHolder(View view) {

            super(view);

            cardView = view.findViewById(R.id.card_view);
            imageView = view.findViewById(R.id.imageview);
            textView_App_Name = view.findViewById(R.id.Apk_Name);
            textView_App_Package_Name = view.findViewById(R.id.Apk_Package_Name);
            textView_App_Status = view.findViewById(R.id.app_status);
        }
    }

    @NonNull
    @Override
    public AppInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view2 = LayoutInflater.from(context1).inflate(R.layout.app_info, parent, false);
        return new ViewHolder(view2);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        PackageManager pm = context1.getPackageManager();
        apkInfoExtractor = new ApkInfoExtractor(context1);
        Collections.sort(mListAppInfo);
        name = String.valueOf(mListAppInfo.get(position));
        boolean isInstalled = isPackageInstalled(String.valueOf(apkInfoExtractor.getAppPackageName(name)), pm);
        String applicationLabelName = apkInfoExtractor.GetAppName(name);
        Drawable drawable = apkInfoExtractor.getAppIconByPackageName(name);

        viewHolder.textView_App_Name.setText(applicationLabelName);
        viewHolder.textView_App_Package_Name.setText(name);
        viewHolder.imageView.setImageDrawable(drawable);

        if (isInstalled) {
            viewHolder.textView_App_Status.setText("Uninstall");
        } else {
            viewHolder.textView_App_Status.setText("Install");
        }

        viewHolder.cardView.setOnClickListener(view -> {

            Intent intent = context1.getPackageManager().getLaunchIntentForPackage(name);
            if (intent != null) {
                context1.startActivity(intent);
            } else {
                Toast.makeText(context1, name + " Error, Please Try Again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return mListAppInfo.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                if (charSequence != null && charSequence.length() > 0) {
                    charSequence = charSequence.toString().toLowerCase();
                    ArrayList<String> filteredPlayers = new ArrayList<>();
                    for (int i = 0; i < mListAppInfo.size(); i++) {

                        if (apkInfoExtractor.GetAppName(name).toLowerCase().contains(charSequence)) {
                            filteredPlayers.add(apkInfoExtractor.GetAppName(name));
                        }
                    }
                    results.count = filteredPlayers.size();
                    results.values = filteredPlayers;
                } else {
                    results.count = mListAppInfo.size();
                    results.values = mListAppInfo;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                notifyDataSetChanged();
            }
        };
    }
}