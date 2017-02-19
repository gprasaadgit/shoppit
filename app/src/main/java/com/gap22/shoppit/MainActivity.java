package com.gap22.shoppit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gap22.shoppit.data.ProductRating;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends Activity implements View.OnClickListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    public static final String barCodeContent = "EXTRA_Text";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button scanBtn;
    private FirebaseUser user;
    private FirebaseListAdapter mAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = (Button)findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        //Toast.makeText(MainActivity.this, "CurrenUser." + mAuth.getCurrentUser().getEmail(),
        //        Toast.LENGTH_SHORT).show();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("AUTH: ", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("AUTH: ", "onAuthStateChanged:signed_out");
                }
            }
        };
        //authAnonymous();
        dbRef = database.getReference().child("rating");
        Query loadRatingQuery = dbRef
                //.orderByChild("userID")
                //.startAt(mAuth.getCurrentUser().getUid())
                //.endAt(mAuth.getCurrentUser().getUid());
                .child(mAuth.getCurrentUser().getUid());
                //.child(user.getUid());
        ListView ratingsList = (ListView)findViewById(R.id.lstRatings);
        mAdapter = new FirebaseListAdapter<ProductRating>(this, ProductRating.class, R.layout.rating_layout, loadRatingQuery) {
            @Override
            protected void populateView(View v, ProductRating model, int position) {
                DatabaseReference itemRef = mAdapter.getRef(position);
                ((TextView)v.findViewById(R.id.itemCode)).setText(
                        (model.itemName != null)?
                                model.itemName
                                :itemRef.getKey().toString());
                //((TextView)v.findViewById(R.id.averageRating)).setText(String.valueOf(model.myRating));
                ((RatingBar)v.findViewById(R.id.rbMyRating)).setRating(model.myRating);
            }
        };
        ratingsList.setDivider(null);
        ratingsList.setAdapter(mAdapter);

    }


        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                Intent scanIntent = new Intent(this, RatingsActivity.class);
                scanIntent.putExtra(barCodeContent, scanningResult.getContents());
                startActivity(scanIntent);
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    private void authAnonymous() {
        //mAuth.signOut();
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
                            Toast.makeText(MainActivity.this, "Authentication failed.",
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
    public void onClick(View v) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
