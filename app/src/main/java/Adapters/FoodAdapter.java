package Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import MyObjects.Food;
import MyObjects.Order;
import com.example.shafi.digitalizedrestaurant.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder> {
    private List<Food> foodList;
    private List<String> keyList;
    private List<Order> orderList = new ArrayList<>();
    private List<Order> oldOrderList = new ArrayList<>();
    private Context context;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String uid, categoryKey, categoryName, userType;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtFoodName, txtPrice;

        public MyViewHolder(View view){
            super(view);
            txtFoodName = view.findViewById(R.id.food_name_txt);
            txtPrice = view.findViewById(R.id.price_txt);
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null)
                uid = user.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/menu/category");
        }

    }

    public FoodAdapter(List<Food> foodList, Context context, List<String> keyList, String userType){
        this.foodList = foodList;
        this.context = context;
        this.keyList = keyList;
        this.userType = userType;
    }

    @Override
    public FoodAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_row, parent, false);

        return new FoodAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
        final Food food = foodList.get(position);

          holder.txtFoodName.setText(food.getName());
          holder.txtPrice.setText("Rs " + String.valueOf(food.getPrice()));

          if (userType.equals("admin")) {
              holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                  @Override
                  public boolean onLongClick(View v) {

                      String[] options = {"Edit", "Delete"};

                      AlertDialog.Builder builder = new AlertDialog.Builder(context);
                      builder.setTitle("What do you want to do?");
                      builder.setItems(options, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {

                              //Codes for editing and deleting recyclerView items

                              if (which == 1) {

                                  final String key = keyList.get(position);
                                  android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(context);
                                  alert.setMessage("Are you sure you want to delete the item permanently?");
                                  alert.setTitle("Confirm Action");

                                  alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          mDatabase.child(categoryKey).child("food").child(key).removeValue();
                                      }

                                  });

                                  alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          dialog.dismiss();
                                      }
                                  });

                                  alert.show();
                                  //  String keyNew = mDatabse.child("users").child("menu").child("category").push().child(category.getCategoryId());

                                  Log.d("mylog", key);


                              } else {
                                  final String key = keyList.get(position);
                                  android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(context);
                                  LinearLayout linearLayout = new LinearLayout(context);
                                  linearLayout.setOrientation(LinearLayout.VERTICAL);

                                  final EditText txtFoodName = new EditText(context);
                                  final EditText txtPrice = new EditText(context);

                                  txtFoodName.setHint("Food Name");
                                  txtFoodName.setMaxLines(1);
                                  txtPrice.setHint("Food Price");
                                  txtPrice.setMaxLines(1);
                                  txtPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
                                  linearLayout.addView(txtFoodName);
                                  linearLayout.addView(txtPrice);

                                  alert.setMessage("Enter new Food Details");
                                  alert.setTitle("Edit");
                                  alert.setView(linearLayout);

                                  alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          if (txtFoodName.getText().toString().isEmpty()) {
                                              Toast.makeText(context, "Enter Food Name", Toast.LENGTH_LONG).show();
                                              return;
                                          }

                                          if (txtPrice.getText().toString().isEmpty()) {
                                              Toast.makeText(context, "Enter Food Price", Toast.LENGTH_LONG).show();
                                              return;
                                          }

                                          String name = txtFoodName.getText().toString();
                                          int price = Integer.parseInt(txtPrice.getText().toString());


                                          for (int i = 0; i < foodList.size(); i++) {
                                              if (name.equals(foodList.get(i).getName())) {
                                                  Toast.makeText(context, "Food already exist", Toast.LENGTH_LONG).show();
                                                  return;
                                              }

                                          }

                                          food.setName(name);
                                          food.setPrice(price);
                                          food.setCategory(categoryName);

                                          mDatabase.child(categoryKey).child("food").child(key).setValue(food);
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
          }else {
              holder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(context);

                      //Check for duplicate orders

                      SharedPreferences sharedPreferences = context.getSharedPreferences("BasketFile", Context.MODE_PRIVATE);
                      String oldOrderListAsString = sharedPreferences.getString("OrderDetails", null);
                      List<Order> preOrderList = new ArrayList<>();

                      Type listType = new TypeToken<List<Order>>(){}.getType();

                      Gson oldGson = new Gson();

                      if (oldOrderListAsString != null){
                          preOrderList = oldGson.fromJson(oldOrderListAsString, listType);

                          for(int i = 0; i < preOrderList.size(); i++){
                              if (foodList.get(position).getName().equals(preOrderList.get(i).getName())){
                                  Toast.makeText(context, "Food already added to basket\nYou can change the quantity", Toast.LENGTH_LONG).show();
                                  return;
                              }
                          }
                      }

                      final EditText editText = new EditText(context);
                      editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                      editText.setMaxLines(1);
                      alert.setMessage("Enter Quantity");
                      alert.setTitle("Add to Basket");
                      alert.setView(editText);

                      alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {

                              if(editText.getText().toString().isEmpty()){
                                  Toast.makeText(context, "Enter Quantity", Toast.LENGTH_LONG).show();
                                  return;
                              }

                              oldOrderList.clear();
                              orderList.clear();
                              int quantity = Integer.parseInt(editText.getText().toString());

                              final Order order = new Order();
                              final Food food = foodList.get(position);

                              order.setName(food.getName());
                              order.setPrice(food.getPrice());
                              order.setCategory(food.getCategory());
                              order.setQuantity(quantity);
                              orderList.add(order);

                              //Appending sharedprefernces

                              SharedPreferences sharedPreferences = context.getSharedPreferences("BasketFile", Context.MODE_PRIVATE);
                              String oldOrderListAsString = sharedPreferences.getString("OrderDetails", null);
                              //  sharedPreferences.edit().clear().commit();

                              Type listType = new TypeToken<List<Order>>(){}.getType();
                              Gson oldGson = new Gson();

                              if(oldOrderListAsString != null) {
                                  oldOrderList = oldGson.fromJson(oldOrderListAsString, listType);


                                  for (int i = 0; i < oldOrderList.size(); i++) {

                                      orderList.add(oldOrderList.get(i));
                                  }
                              }
                              Gson newGson = new Gson();
                              String orderListAsString = newGson.toJson(orderList);
                              SharedPreferences.Editor editor = sharedPreferences.edit();
                              editor.putString("OrderDetails", orderListAsString);
                              editor.commit();

                              Toast.makeText(context, "Food Added to Basket", Toast.LENGTH_LONG).show();


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
              });
          }

    }

    @Override
    public int getItemCount(){
        return foodList.size();
    }

    public void setKey(List<String> keyList, String categoryKey, String categoryName){
        this.keyList = keyList;
        this.categoryKey = categoryKey;
        this.categoryName = categoryName;
    }

}
