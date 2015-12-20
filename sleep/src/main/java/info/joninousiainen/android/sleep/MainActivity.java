package info.joninousiainen.android.sleep;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ToggleButton yesButton;
    private ToggleButton noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        yesButton = (ToggleButton) findViewById(R.id.button_yes);
        noButton = (ToggleButton) findViewById(R.id.button_no);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String date = DateFormat.format("yyyyMMdd", new Date()).toString();
        String key = "answer-" + date;
        String value = preferences.getString(key, null);
        if (value != null) {
            switch (value) {
                case "yes":
                    yesButton.setChecked(true);
                    noButton.setChecked(false);
                    break;
                case "no":
                    yesButton.setChecked(false);
                    noButton.setChecked(true);
                    break;
            }
        }

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeAnswer("yes");

                yesButton.setChecked(true);
                noButton.setChecked(false);

                showToast(getString(R.string.toast_answer_yes));
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeAnswer("no");

                yesButton.setChecked(false);
                noButton.setChecked(true);

                showToast(getString(R.string.toast_answer_no));
            }
        });
    }

    private void storeAnswer(String answer) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        String date = DateFormat.format("yyyyMMdd", new Date()).toString();
        edit.putString("answer-" + date, answer);
        edit.commit();
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
                .show();
    }
}
