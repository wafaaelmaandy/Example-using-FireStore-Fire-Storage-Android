package com.wafaaelm3andy.firestore.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wafaaelm3andy.firestore.R;
import com.wafaaelm3andy.firestore.Model.item;


public class MainActivity extends AppCompatActivity  {
    RecyclerView recycler_items ;
    private String TAG ="fireLog ";
    FirebaseFirestore firebaseFirestore ;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycler_items=findViewById(R.id.rec_items);

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,additemsActivity.class));
          }
        });
        init();
        getItemsList();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                String id =  viewHolder.itemView.getTag().toString();

                removeitem(id);

                firestoreRecyclerAdapter.notifyDataSetChanged();
            }

        }).attachToRecyclerView(recycler_items);

    }

    private void removeitem(String  id) {
         //getDocandDeletephoto( id);
        firebaseFirestore.collection("items").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
public item getDocandDeletephoto(String id){
    final item[] item1 = new item[1];
    DocumentReference docRef = firebaseFirestore.collection("items").document(id);
    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
             item1[0] = documentSnapshot.toObject(item.class);
            assert item1[0] != null;
            String storageUrl = item1[0].getImgurl();
            deteteimage( storageUrl);

        }
    });
    return item1[0];
}


    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recycler_items.setLayoutManager(linearLayoutManager);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void getItemsList(){
        Query query = firebaseFirestore.collection("items");

        FirestoreRecyclerOptions<item> response = new FirestoreRecyclerOptions.Builder<item>()
                .setQuery(query, item.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<item, ItemHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull item model) {


                holder.itemTextView.setText(model.getText());
                Glide.with(getApplicationContext())
                        .load(model.getImgurl())
                        .into(holder.item_imageview);

                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                holder.itemView.setTag(snapshot.getId());



              /*  holder.itemView.setOnClickListener(v -> {
                    Snackbar.make(friendList, model.getName()+", "+model.getTitle()+" at "+model.getCompany(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }); */
            }

            @NonNull
            @Override
            public ItemHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);

                return new ItemHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        firestoreRecyclerAdapter.notifyDataSetChanged();
        recycler_items.setAdapter(firestoreRecyclerAdapter);
    }



    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView itemTextView ;
        ImageView item_imageview ;

        public ItemHolder(final View itemView){
            super(itemView);
            itemTextView =itemView.findViewById(R.id.item_tv );
            item_imageview=itemView.findViewById(R.id.item_img);

            //itemView.setOnClickListener(this);
            // bind focus listener
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // run scale animation and make it bigger
                        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_in_tv);
                        itemView.startAnimation(anim);
                        anim.setFillAfter(true);
                    } else {
                        // run scale animation and make it smaller
                        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_out_tv);
                        itemView.startAnimation(anim);
                        anim.setFillAfter(true);
                    }
                }
            });

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }
public  void  deteteimage(String storageUrl){
    StorageReference photoRef = storage.getReferenceFromUrl(storageUrl);
    //StorageReference photoRef = storageRef.child("images/i1.png");
//todo problem at delete from firebase storage
    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            // File deleted successfully
            Log.d(TAG, "onSuccess: deleted file");
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Uh-oh, an error occurred!
            Log.d(TAG, "onFailure: did not delete file");
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();

        }
    });
}
}
