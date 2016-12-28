package info.joninousiainen.android.sleep;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ViewHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_history);

        Map<String, String> answersByDate = getAllAnswersInDescendingOrder();
        if (answersByDate.isEmpty()) {
            return;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date firstAnswerDate = getFirstAnswerDate(answersByDate, dateFormat);
        Calendar calendar = getToday();
        LinearLayout layout = (LinearLayout) findViewById(R.id.viewHistoryMainLayout);

        while (!calendar.getTime().before(firstAnswerDate)) {
            String key = dateFormat.format(calendar.getTime());
            String value = answersByDate.get(key);

            TextView text = new TextView(this);
            CharSequence localizedValue;
            if ("yes".equals(value)) {
                localizedValue = getText(R.string.history_answer_yes);
            }
            else if ("no".equals(value)) {
                localizedValue = getText(R.string.history_answer_no);
            }
            else {
                localizedValue = getText(R.string.history_answer_empty);
            }
            // TODO: Visualize yes/no better, use color etc.
            text.setText(key + ": " + localizedValue);
            layout.addView(text);

            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
    }

    private Map<String, String> getAllAnswersInDescendingOrder() {
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME, MODE_PRIVATE);
        Map<String, String> sortedDates = new TreeMap<>(Collections.reverseOrder());
        for (String key : preferences.getAll().keySet()) {
            if (key.startsWith(MainActivity.ANSWER_KEY_PREFIX)) {
                String date = key.substring(MainActivity.ANSWER_KEY_PREFIX.length());
                String answer = preferences.getString(key, null);
                sortedDates.put(date, answer);
            }
        }
        return sortedDates;
    }

    private Date getFirstAnswerDate(Map<String, String> answersInDescendingOrder, DateFormat dateFormat) {
        Iterator<Map.Entry<String, String>> iterator = answersInDescendingOrder.entrySet().iterator();
        Map.Entry<String, String > lastEntry = null;
        while (iterator.hasNext()) {
            lastEntry = iterator.next();
        }

        try {
            return dateFormat.parse(lastEntry.getKey());
        } catch (ParseException e) {
            throw new RuntimeException("Invalid answer date: " + lastEntry.getKey(), e);
        }
    }

    private Calendar getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return  calendar;
    }
}
