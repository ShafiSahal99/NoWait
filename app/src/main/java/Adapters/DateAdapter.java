package Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import MyObjects.Chair;
import MyObjects.Order;

import com.example.shafi.digitalizedrestaurant.BillDisplayActivity;
import com.example.shafi.digitalizedrestaurant.KitchenActivity;
import com.example.shafi.digitalizedrestaurant.OrderDisplayActivity;
import com.example.shafi.digitalizedrestaurant.R;
import com.example.shafi.digitalizedrestaurant.SalesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder> {
    private List<String> dateList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtDate;

        public MyViewHolder(View view){
            super(view);

            txtDate = view.findViewById(R.id.date_txt);

        }

    }

    public DateAdapter(List<String> dateList, Context context){
        this.dateList = dateList;
        this.context = context;
    }


    @Override
    public DateAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.date_row, parent, false);

        return new DateAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position){
        final String date = dateList.get(position);

        holder.txtDate.setText(date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SalesActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("userType", "admin");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount(){return dateList.size();}

    public void changeDataSet(List<String> dateList){
        this.dateList = dateList;
    }

}
