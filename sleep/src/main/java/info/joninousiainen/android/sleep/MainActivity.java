package info.joninousiainen.android.sleep;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Date;

public class MainActivity extends Activity {
    private ToggleButton yesButton;
    private ToggleButton noButton;
    private Button viewHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        yesButton = (ToggleButton) findViewById(R.id.button_yes);
        noButton = (ToggleButton) findViewById(R.id.button_no);
        viewHistoryButton = (Button) findViewById(R.id.button_viewHistory);

        SharedPreferences preferences = getSharedPreferences(SharedConstants.PREFERENCES_FILE_NAME, MODE_PRIVATE);
        String date = DateFormat.format(SharedConstants.DATE_FORMAT, new Date()).toString();
        String key = SharedConstants.ANSWER_KEY_PREFIX + date;
        String value = preferences.getString(key, null);
        if (value != null) {
            switch (value) {
                case SharedConstants.ANSWER_YES:
                    yesButton.setChecked(true);
                    noButton.setChecked(false);
                    break;
                case SharedConstants.ANSWER_NO:
                    yesButton.setChecked(false);
                    noButton.setChecked(true);
                    break;
            }
        }

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeAnswer(SharedConstants.ANSWER_YES);

                yesButton.setChecked(true);
                noButton.setChecked(false);

                showToast(getString(R.string.toast_answer_yes));
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeAnswer(SharedConstants.ANSWER_NO);

                yesButton.setChecked(false);
                noButton.setChecked(true);

                showToast(getString(R.string.toast_answer_no));
            }
        });

        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void storeAnswer(String answer) {
        SharedPreferences preferences = getSharedPreferences(SharedConstants.PREFERENCES_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        String date = DateFormat.format(SharedConstants.DATE_FORMAT, new Date()).toString();
        edit.putString("answer-" + date, answer);
        edit.commit();
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
                .show();
    }
}
