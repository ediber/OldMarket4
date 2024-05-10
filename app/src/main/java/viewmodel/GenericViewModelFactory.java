package viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GenericViewModelFactory<T extends ViewModel> implements ViewModelProvider.Factory {
    private Application application;
    private ViewModelCreator<T> creator;

    public interface ViewModelCreator<T extends ViewModel> {
        T create(Application application);
    }

    public GenericViewModelFactory(Application application, ViewModelCreator<T> creator) {
        this.application = application;
        this.creator = creator;
    }

    /*
    public GenericViewModelFactory(){}

    public void setApplication(Application application){
        this.application = application;
    }

    public void setCreator(ViewModelCreator<T> creator){
        this.creator = creator;
    }
     */

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(modelClass)) {
            return (T) creator.create(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}