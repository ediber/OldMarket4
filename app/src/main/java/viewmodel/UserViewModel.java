package viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import model.User;
import repository.UserRepository;


public class UserViewModel extends AndroidViewModel
{
    private MutableLiveData<Boolean>   successOperation;
    private Context context;
    private static UserRepository repository;
    private MutableLiveData<User> muteUser;


    public UserViewModel(Application application)
    {
        super(application);
        repository= new UserRepository(application);
        successOperation = new MutableLiveData<>();
        muteUser = new MutableLiveData<>();
    }

    public void add(User user)
    {
        repository.add(user)
                .addOnSuccessListener(aBoolean ->
                {successOperation.setValue(true);})
                .addOnFailureListener(e ->
                {successOperation.setValue(false);});
    }

    public void SignIn(String userName, String password){
        repository.signIn(userName, password)
                .addOnSuccessListener(new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        muteUser.setValue(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        muteUser.setValue(null);
                    }
                });
    }

    public LiveData<Boolean> getSuccessOperation(){
        return successOperation;
    }

    public MutableLiveData<User> getMuteUser(){
        return muteUser;
    }







}

