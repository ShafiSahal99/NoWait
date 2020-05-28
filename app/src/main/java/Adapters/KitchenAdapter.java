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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.MyViewHolder> {
    private List<Chair> chairList;
    private List<String> keyList;
    private List<Order> orderList = new ArrayList<>();
    private Context context;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String uid, userType = "kitchen";

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView txtChairNumber;

        public MyViewHolder(View view){
            super(view);

            txtChairNumber = view.findViewById(R.id.chair_number_txt);
            user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null){
                uid = user.getUid();
            }

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/customer");
        }

    }

    public KitchenAdapter(List<Chair> chairList, Context context){
        this.chairList = chairList;
        this.context = context;
    }

    public KitchenAdapter(List<Chair> chairList, Context context, String userType){
        this.chairList = chairList;
        this.context = context;
        this.userType = userType;
    }

    @Override
    public KitchenAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kitchen_row, parent, false);

        return new KitchenAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position){
        final Chair chair = chairList.get(position);

        holder.txtChairNumber.setText(String.valueOf(chair.getChairNumber()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userType.equals("kitchen")) {
                    Intent intent = new Intent(context, OrderDisplayActivity.class);
                    intent.putExtra("key", keyList.get(position));
                    intent.putExtra("userType", "kitchen");
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, BillDisplayActivity.class);
                    intent.putExtra("key", keyList.get(position));
                    intent.putExtra("userType", "counter");
                    context.startActivity(intent);
                }
            }
        });

        if (userType.equals("kitchen")) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String[] options = {"Processed"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Press if the order is ready to deliver");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                mDatabase.child(keyList.get(position)).removeValue();
                               /* chairList.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                                changeDataSet(chairList);*/
                            }
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount(){return chairList.size();}

    public void changeDataSet(List<Chair> chairList){
        this.chairList = chairList;
    }

    public void setKey(List<String> keyList){
        this.keyList = keyList;
    }
}
