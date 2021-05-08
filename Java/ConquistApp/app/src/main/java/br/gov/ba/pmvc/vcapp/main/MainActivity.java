package br.gov.ba.pmvc.vcapp.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.datami.smi.SmiSdk;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import br.gov.ba.pmvc.vcapp.AboutFragment;
import br.gov.ba.pmvc.vcapp.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            String mySdkKey = getString(R.string.SDK_KEY); //Use the SDK API access key given by Datami.
            String myUserId = "";
            int sdIconId = R.drawable.ic_launcher_foreground;

//          List<String> exclusionDomains = new ArrayList<String>(2);
//          exclusionDomains.add("www.google.com");
//          exclusionDomains.add("www.google.com.br");
//          SmiSdk.initSponsoredData(mySdkKey, this, myUserId, sdIconId, false, exclusionDomains);
            SmiSdk.initSponsoredData(mySdkKey, this, myUserId, sdIconId, false);

            setContentView(R.layout.main_activity);
            loadFragment(new MainFragment());

            BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
            navigationView.setOnNavigationItemSelectedListener(this);
        } catch (Exception e) {
            Toast.makeText(this, "Error :" + e , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("PERMISSIONS", "Permission: " + permissions[0] + "was" + grantResults[0]);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.page_home:
                fragment = new MainFragment();
                break;

            case R.id.page_about:
                fragment = new AboutFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.rl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}



