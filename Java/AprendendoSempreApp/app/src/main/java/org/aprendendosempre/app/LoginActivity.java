package org.aprendendosempre.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.aprendendosempre.app.main.MainActivity;
import org.aprendendosempre.app.main.MainFragment;

public class LoginActivity extends AppCompatActivity {

    public static String idTemp = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText loginTemp = findViewById(R.id.txtBoxLogin);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("Chave", ""+loginTemp.getText()); //aqui seria passado algum id para acessar a plataforma
                idTemp = ""+loginTemp.getText();
                startActivity(intent);
                finish();
            }
        });
    }
}