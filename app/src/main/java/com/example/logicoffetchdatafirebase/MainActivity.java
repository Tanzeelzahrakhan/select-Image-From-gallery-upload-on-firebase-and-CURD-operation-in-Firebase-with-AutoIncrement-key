package com.example.logicoffetchdatafirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.logicoffetchdatafirebase.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
   ActivityMainBinding binding;
    int maxId=0;
  Uri uri;
  String ImageUrl;
    String rollNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseDatabase.getInstance().getReference().child("Student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            maxId=(int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        binding.btnSend.setOnClickListener(view -> FirebaseStorage.getInstance().getReference().child("ImageFolder").child(String.valueOf(maxId+1)).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                ImageUrl=uriTask.getResult().toString();
                HashMap<String,Object> map=new HashMap<>();
                map.put("Name",binding.etName.getText().toString());
                map.put("Email",binding.etEmail.getText().toString());
                map.put("Pass",binding.etPassword.getText().toString());
                map.put("ImageUrl",ImageUrl);

                FirebaseDatabase.getInstance().getReference().child("Student").child(String.valueOf(maxId+1)).setValue(map);
                Toast.makeText(MainActivity.this, "send Data", Toast.LENGTH_SHORT).show();
            }
        }));

binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        HashMap<String, Object> map=new HashMap<String, Object>();
        binding.image.setImageURI(null);
        map.put("Name",binding.etName.getText().toString());
        map.put("Email",binding.etEmail.getText().toString());
        map.put("Pass",binding.etPassword.getText().toString());
       // map.put("Dept",binding.etDept.getText().toString());
        map.put("ImageUrl",ImageUrl);
        FirebaseDatabase.getInstance().getReference().child("Student").updateChildren(map);

        Toast.makeText(MainActivity.this, "Data Update", Toast.LENGTH_SHORT).show();
    }
});





        binding.btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rollNo =binding.etRoll.getText().toString();
                binding.etRoll.setText("");
                FirebaseDatabase.getInstance().getReference().child("Student").child(rollNo).addValueEventListener(new ValueEventListener() {
                    @Override

                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String,Object> Map=(java.util.Map<String, Object>) snapshot.getValue();
                        if (snapshot.exists()){
                            maxId=(int)snapshot.getChildrenCount();
                            String name=(String) Map.get("Name");
                            String email=(String)Map.get("Email");
                            String pass=(String) Map.get("Pass");
                            String rollNo=(String) Map.get("RollNo");
                            //String dept=(String) Map.get("Dept");
                            String ImgUrl=(String) Map.get("ImageUrl");
                            binding.tvName.setText(name);
                            binding.tvEmail.setText(email);
                            binding.tvPass.setText(pass);
                            binding.tvRollNo.setText(rollNo);
                            //binding.tvDept.setText(dept);
                            Glide.with(MainActivity.this).load(ImgUrl).into(binding.image);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Data not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });






        binding.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               FirebaseStorage.getInstance().getReference().child("ImageFolder").child(rollNo).delete();
                FirebaseDatabase.getInstance().getReference().child("Student").removeValue();
                binding.tvName.setText(null);
                binding.tvEmail.setText(null);
                binding.tvPass.setText(null);
                //binding.image.setImageURI(Uri.parse(""));
                binding.image.setImageResource(0);
                Toast.makeText(MainActivity.this, "Data Delete", Toast.LENGTH_SHORT).show();
            }
        });








       /*  binding.btnNext.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(MainActivity.this,MainActivity2.class);
        startActivity(intent);
    }
});*/
         binding.image.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent=new Intent(Intent.ACTION_PICK);
                 intent.setType("image/*");
                 startActivityForResult(intent,45);

             }
         });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            uri=data.getData();
            binding.image.setImageURI(uri);
        }
    }
}