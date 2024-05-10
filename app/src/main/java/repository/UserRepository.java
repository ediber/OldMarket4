package repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import model.Users;

//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import model.User;  // Make sure this points to the correct User model class


public class UserRepository {
  //  public CollectionReference collection;
  //  private FirebaseFirestore db;
    private final MutableLiveData<Users> UserLiveData;
    public UserRepository(Context context)
    {
        try {
       //     db = FirebaseFirestore.getInstance();
        }
        catch (Exception e){
            FirebaseInstance instance = FirebaseInstance.instance(context);

    //        db = FirebaseFirestore.getInstance(FirebaseInstance.app);
        }

    //    collection = db.collection("Users");
        UserLiveData = new MutableLiveData<>();
    }
    public Task<Boolean> add (User user)
    {
/*        TaskCompletionSource<Boolean> taskCompletion = new TaskCompletionSource<Boolean>();
        DocumentReference document = collection.document();

       user.setIdFs(document.getId());
        document.set(user).addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    public void onSuccess(Void unused)
                    {
                        taskCompletion.setResult(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        taskCompletion.setResult(false);
                    }
                });
        return taskCompletion.getTask();*/
        return null;
    }

    public Task<User> signIn(String userName, String password){
/*        TaskCompletionSource<User> taskUser = new TaskCompletionSource<>();

        Users users = new Users();

        collection.whereEqualTo("email", userName).whereEqualTo("password", password).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty())
                        {
                            User user = null;
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                user = document.toObject(User.class);
                            }
                            taskUser.setResult(user);
                        }
                        else{
                            taskUser.setResult(null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        taskUser.setResult(null);
                    }
                });
        return taskUser.getTask();*/

        return null;
    }










}
