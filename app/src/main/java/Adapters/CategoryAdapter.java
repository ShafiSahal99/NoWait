package Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import MyObjects.Category;

import com.example.shafi.digitalizedrestaurant.FoodDisplayActivity;
import com.example.shafi.digitalizedrestaurant.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.view.View;
import android.widget.Toast;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private List<Category> categoryList;
    private List<String> keList;
    private List<String> keyList;
    private Context context;
    private DatabaseReference mDatabse;
    private FirebaseUser user;
    private String uid, userType;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtCategory;

        public  MyViewHolder(View view){
            super(view);

            txtCategory = view.findViewById(R.id.category_txt_view);

            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null){
                uid = user.getUid();
            }
            mDatabse = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/menu/category");

        }
    }

    public CategoryAdapter(List<Category> categoryList, Context context, List<String> keList, String userType){
        this.categoryList = categoryList;
        this.context = context;
        this.keList = keList;
        this.userType = userType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_row, parent, false);

        return new MyViewHolder(itemView);
    }

   @Override
   public void onBindViewHolder(MyViewHolder holder, final int position){
        final Category category = categoryList.get(position);

        holder.txtCategory.setText(category.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String key = keyList.get(position);

                Intent intent = new Intent(context, FoodDisplayActivity.class);
                intent.putExtra("categoryKey", key);
                intent.putExtra("categoryName", category.getName());
                intent.putExtra("userType", userType);
                context.startActivity(intent);
            }
        });

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
                                        mDatabse.child(key).removeValue();
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
                                final EditText editText = new EditText(context);
                                editText.setMaxLines(1);
                                alert.setMessage("Enter new Category Name");
                                alert.setTitle("Edit");
                                alert.setView(editText);

                                alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (editText.getText().toString().isEmpty()) {
                                            Toast.makeText(context, "Enter Category Name", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        String text = editText.getText().toString();

                                        for (int i = 0; i < categoryList.size(); i++) {
                                            if (text.equals(categoryList.get(i).getName())) {
                                                Toast.makeText(context, "Category already exist", Toast.LENGTH_LONG).show();
                                                return;
                                            }

                                        }

                                        category.setName(text);

                                        mDatabse.child(key).setValue(category);
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
        return categoryList.size();
    }

    public void setKey(List<String> keyList){
        this.keyList = keyList;

    }
}
