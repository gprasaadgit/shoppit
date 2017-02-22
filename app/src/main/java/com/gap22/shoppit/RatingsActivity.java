package com.gap22.shoppit;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gap22.shoppit.ProductUPC.ProductInfo;
import com.gap22.shoppit.data.ProductAverageRating;
import com.gap22.shoppit.data.ProductRating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.gap22.shoppit.MainActivity.barCodeContent;

public class RatingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private double averageRating;
    private int numRatings;
    private String productName;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        TextView t = (TextView) findViewById(R.id.tvProductID);
        t.setText(getIntent().getStringExtra(barCodeContent));
        new RetrieveProductInfo(getIntent().getStringExtra(barCodeContent)).execute();
        //dbRef = database.getReference(getIntent().getStringExtra(barCodeContent));
        dbRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        //Toast.makeText(RatingsActivity.this, "CurrenUser." + mAuth.getCurrentUser().getEmail(),
        //        Toast.LENGTH_SHORT).show();
        Button saveButton = (Button) findViewById(R.id.button2);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveClick(v);
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("AUTH: ", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("AUTH: ", "onAuthStateChanged:signed_out");
                }
            }
        };

        Query loadAvgRatingQuery = dbRef.child("aggregateRating")
                //.orderByChild(getIntent().getStringExtra(barCodeContent))
                .orderByKey()
                .startAt(getIntent().getStringExtra(barCodeContent))
                .endAt(getIntent().getStringExtra(barCodeContent));
        Query loadRatingQuery = dbRef.child("rating")
                .child(mAuth.getCurrentUser().getUid())
                //.orderByChild(getIntent().getStringExtra(barCodeContent))
                .orderByKey()
                .startAt(getIntent().getStringExtra(barCodeContent))
                .endAt(getIntent().getStringExtra(barCodeContent));

        //authAnonymous();
        ValueEventListener ratingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    ProductRating storedRating = postSnapshot.getValue(ProductRating.class);
                        RatingBar rBar = (RatingBar) findViewById(R.id.ratingBar);
                        rBar.setRating((storedRating != null) ? storedRating.myRating : 0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("ERROR: ", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        ValueEventListener avgRatingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    numRatings = (postSnapshot.getValue(ProductAverageRating.class) != null)? postSnapshot.getValue(ProductAverageRating.class).numRatings : 0;
                    averageRating = (postSnapshot.getValue(ProductAverageRating.class) != null)? postSnapshot.getValue(ProductAverageRating.class).averageRating : 0.0;
                }
                    RatingBar rBar = (RatingBar) findViewById(R.id.ratingBar3);
                    rBar.setRating((float) averageRating);
                    TextView tvNumRatings = (TextView)findViewById(R.id.textView5);
                    tvNumRatings.setText(numRatings + " " + "other shoppers said:");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("ERROR: ", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        loadRatingQuery.addValueEventListener(ratingListener);
        loadAvgRatingQuery.addValueEventListener(avgRatingListener);
    }

    protected  void btnSaveClick(View v){
        getIntent().putExtra("userID", mAuth.getCurrentUser().getUid());
        getIntent().putExtra("productName", productName);
        ProductRating pr = new ProductRating(getIntent(), v.getRootView());
        ProductAverageRating par = new ProductAverageRating(numRatings, (float)averageRating, pr.myRating, productName);
        //dbRef.child("rating").child(mAuth.getCurrentUser().getUid()).setValue(pr);
        dbRef.child("rating").child(mAuth.getCurrentUser().getUid()).child(getIntent().getStringExtra(barCodeContent)).setValue(pr);
        dbRef.child("aggregateRating").child(getIntent().getStringExtra(barCodeContent)).setValue(par);
        Toast toast = Toast.makeText(getApplicationContext(),
        "Rating saved. Thank you!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            Intent scanIntent = new Intent(this, this.getClass());
            scanIntent.putExtra(barCodeContent, scanningResult.getContents());
            startActivity(scanIntent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected void btnScanClick(View v){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    private void authAnonymous() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("AUTH: ", "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("AUTH: ", "signInAnonymously", task.getException());
                            Toast.makeText(RatingsActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class RetrieveProductInfo extends AsyncTask<Void, Void, String> {

        private Exception exception;
        private String prodID;

        public RetrieveProductInfo(String prodCode){
            super();
            prodID = prodCode;
        }

        protected void onPreExecute() {
            //((TextView)findViewById(R.id.tvProductID)).setText(barCodeContent);
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url = new URL("http://api.upcdatabase.org/json/8cb97098bd8ade55db65079c83aab7c5/"+ prodID);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO", response);
            Gson gson = new Gson();
            ProductInfo p = gson.fromJson(response, ProductInfo.class);
            if(p!= null && (p.getItemname() != null)) {
                productName = p.getItemname();
                ((TextView) findViewById(R.id.tvProductID)).setText(p.getItemname());
            }
        }
    }


}

