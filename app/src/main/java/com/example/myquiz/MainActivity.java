package com.example.myquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.myquiz.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String userId = getIntent().getStringExtra("userUid"); // Retrieve userId from LoginActivity

        // Load default fragment
        currentFragment = createFragmentWithArgs(new HomeFragment(), userId);
        replaceFrag(currentFragment);

        // Handle BottomNavigationView item selection
        binding.bottomBar.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = createFragmentWithArgs(new HomeFragment(), userId);
                    break;
                case R.id.rank:
                    selectedFragment = createFragmentWithArgs(new LeaderboardFragment(), userId);
                    break;
                case R.id.profile:
                    selectedFragment = createFragmentWithArgs(new ProfileFragment(), userId);
                    break;
            }

            if (selectedFragment != null) {
                replaceFrag(selectedFragment);
            }
            return true;
        });
    }

    private Fragment createFragmentWithArgs(Fragment fragment, String userId) {
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    private void replaceFrag(Fragment fragment) {
        if (currentFragment == fragment) return; // Prevent unnecessary replacements

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
        currentFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.wallet) {
            Toast.makeText(this, "Wallet is clicked.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
