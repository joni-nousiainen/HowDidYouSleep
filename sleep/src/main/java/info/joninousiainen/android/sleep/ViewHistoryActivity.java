package info.joninousiainen.android.sleep;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;
import java.util.TreeMap;

public class ViewHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_history);

        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME, MODE_PRIVATE);
        Map<String, String> sortedDates = new TreeMap<>();
        for (String key : preferences.getAll().keySet()) {
            if (key.startsWith("answer-")) {
                String date = key.substring("answer-".length());
                String answer = preferences.getString(key, null);
                sortedDates.put(date, answer);
            }
        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.viewHistoryMainLayout);

        for (Map.Entry<String, String> entry : sortedDates.entrySet()) {
            TextView text = new TextView(this);
            // TODO: Get text from bundle.
            // TODO: Visualize yes/no better, use color etc.
            text.setText(entry.getKey() + ": " + entry.getValue());
            layout.addView(text);
        }
    }
}
