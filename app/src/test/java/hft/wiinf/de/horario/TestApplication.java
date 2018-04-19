package hft.wiinf.de.horario;


import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;

public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }

    @Override
    public void onTerminate() {
        // Dispose temporary database on termination
        ActiveAndroid.dispose();
        super.onTerminate();
    }
}

