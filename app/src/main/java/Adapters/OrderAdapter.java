package Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import MyObjects.Order;

import com.example.shafi.digitalizedrestaurant.BasketActivity;
import com.example.shafi.digitalizedrestaurant.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private List<Order> orderList;
    private TextView txtBill;
    private Context context;
    private String userType = "customer";

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView txtOrderName, txtOrderPrice, txtOrderQuantity;

        public MyViewHolder(View view){
            super(view);

            txtOrderName = view.findViewById(R.id.basket_food_name_txt);
            txtOrderPrice = view.findViewById(R.id.basket_food_price_txt);
            txtOrderQuantity = view.findViewById(R.id.basket_food_quantity_txt);
        }

    }

    public OrderAdapter(List<Order> orderList, Context context, TextView txtBill){
        this.orderList = orderList;
        this.context = context;
        this.txtBill = txtBill;
    }

    public OrderAdapter(List<Order> orderList, Context context){
        this.orderList = orderList;
        this.context = context;
    }

    public OrderAdapter(List<Order> orderList, Context context, String userType){
        this.orderList = orderList;
        this.context = context;
        this.userType = userType;
    }

    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basket_row, parent, false);

        return new OrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
        final Order order = orderList.get(position);

        holder.txtOrderName.setText(order.getName());
        holder.txtOrderPrice.setText("Total : Rs " + String.valueOf(order.getPrice()*order.getQuantity()));
        holder.txtOrderQuantity.setText(String.valueOf(order.getQuantity()));


        if (userType.equals("customer")) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String[] options = {"Edit Quantity", "Remove"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("What do you want to do?");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 1) {

                                android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(context);
                                alert.setMessage("Are you sure you want to remove the food?");
                                alert.setTitle("Confirm Action");

                                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        List<Order> oldOrderList = new ArrayList<>();
                                        oldOrderList.clear();
                                        SharedPreferences sharedPreferences = context.getSharedPreferences("BasketFile", Context.MODE_PRIVATE);
                                        String oldOrderAsString = sharedPreferences.getString("OrderDetails", null);

                                        Gson gsonOld = new Gson();
                                        Type listType = new TypeToken<List<Order>>() {}.getType();

                                        if (oldOrderAsString != null) {
                                            oldOrderList = gsonOld.fromJson(oldOrderAsString, listType);
                                            orderList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyDataSetChanged();
                                            changeDataSet(orderList);
                                            int bill = 0;
                                            for (int i = 0; i < orderList.size(); i++) {
                                                bill += oldOrderList.get(i).getPrice() * oldOrderList.get(i).getQuantity();
                                            }

                                            txtBill.setText("Total Amount : Rs " + bill);

                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            Gson gson = new Gson();
                                            String oldOrderListAsString = gson.toJson(orderList);
                                            editor.putString("OrderDetails", oldOrderListAsString);
                                            editor.commit();

                                            BasketActivity basketActivity = new BasketActivity(context);
                                            basketActivity.refresh();
                                        }

                                        Toast.makeText(context, "Food removed from basket", Toast.LENGTH_LONG).show();
                                    }

                                });

                                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                alert.show();


                            } else {
                                android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(context);
                                final EditText editText = new EditText(context);
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                editText.setMaxLines(1);
                                alert.setMessage("Enter New Quantity");
                                alert.setTitle("Edit");
                                alert.setView(editText);

                                alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (editText.getText().toString().isEmpty()) {
                                            Toast.makeText(context, "Enter Quantity", Toast.LENGTH_LONG).show();
                                            return;
                                        }


                                        int quantity = Integer.parseInt(editText.getText().toString());
                                        List<Order> newOrderList = new ArrayList<>();
                                        newOrderList.clear();
                                        List<Order> oldOrderList = new ArrayList<>();
                                        oldOrderList.clear();

                                        order.setQuantity(quantity);
                                        changeDataSet(orderList);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Quantity Changed", Toast.LENGTH_LONG).show();

                                        SharedPreferences sharedPreferences = context.getSharedPreferences("BasketFile", Context.MODE_PRIVATE);
                                        String orderListAsString = sharedPreferences.getString("OrderDetails", null);

                                        Type listType = new TypeToken<List<Order>>() {
                                        }.getType();
                                        Gson oldGson = new Gson();

                                        //if (orderListAsString != null) {
                                        oldOrderList = oldGson.fromJson(orderListAsString, listType);
                                        int bill = 0;

                                        for (int i = 0; i < orderList.size(); i++) {
                                            bill += orderList.get(i).getPrice() * orderList.get(i).getQuantity();
                                        }

                                        txtBill.setText("Total Amount : Rs " + bill);

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        Gson newGson = new Gson();
                                        String newOrderListAsString = newGson.toJson(orderList);
                                        editor.putString("OrderDetails", newOrderListAsString);
                                        editor.commit();
                                        //}


                                    }

                                });

                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                alert.show();
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
    public int getItemCount(){

            return orderList.size();

    }

    public void changeDataSet(List<Order> orderList){
        this.orderList = orderList;
    }



}
