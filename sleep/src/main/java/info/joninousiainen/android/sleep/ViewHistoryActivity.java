package info.joninousiainen.android.sleep;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
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

        Map<Date, String> answersByDate = getAllAnswersInDescendingOrder();
        if (answersByDate.isEmpty()) {
            return;
        }

        addDatesWhichHaveNoAnswer(answersByDate);

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(getText(R.string.history_date_format).toString());
        LinearLayout layout = (LinearLayout) findViewById(R.id.viewHistoryMainLayout);

        for (Map.Entry<Date, String> entry : answersByDate.entrySet()) {
            Date date = entry.getKey();

            CharSequence localizedValue;
            switch (entry.getValue()) {
                case "yes":
                    localizedValue = getText(R.string.history_answer_yes);
                    break;
                case "no":
                    localizedValue = getText(R.string.history_answer_no);
                    break;
                default:
                    localizedValue = getText(R.string.history_answer_empty);
            }

            TextView text = new TextView(this);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            text.setText(dateFormat.format(date) + ": " + localizedValue);
            layout.addView(text);

            // Add ruler between weeks
            calendar.setTime(date);
            if (calendar.getFirstDayOfWeek() == calendar.get(Calendar.DAY_OF_WEEK)) {
                addRuler(layout);
            }
        }
    }

    private Map<Date, String> getAllAnswersInDescendingOrder() {
        SharedPreferences preferences = getSharedPreferences(SharedConstants.PREFERENCES_FILE_NAME, MODE_PRIVATE);
        Map<Date, String> sortedDates = new TreeMap<>(Collections.reverseOrder());
        DateFormat dateFormat = new SimpleDateFormat(SharedConstants.DATE_FORMAT);
        for (String key : preferences.getAll().keySet()) {
            if (key.startsWith(SharedConstants.ANSWER_KEY_PREFIX)) {
                String dateString = key.substring(SharedConstants.ANSWER_KEY_PREFIX.length());
                Date date;
                try {
                    date = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    throw new RuntimeException("Date has been stored in wrong format: " + dateString, e);
                }

                String answer = preferences.getString(key, "");

                sortedDates.put(date, answer);
            }
        }
        return sortedDates;
    }

    private void addDatesWhichHaveNoAnswer(Map<Date, String> answersByDate) {
        Calendar calendar = getToday();
        Date firstAnswerDate = getFirstAnswerDate(answersByDate);
        while (!calendar.getTime().before(firstAnswerDate)) {
            Date date = calendar.getTime();
            String value = answersByDate.get(date);
            if (value == null) {
                answersByDate.put(date, "");
            }
            calendar.add(Calendar.DAY_OF_MONTH, -1);
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

    private Date getFirstAnswerDate(Map<Date, String> answersInDescendingOrder) {
        Iterator<Map.Entry<Date, String>> iterator = answersInDescendingOrder.entrySet().iterator();
        Map.Entry<Date, String > lastEntry = null;
        while (iterator.hasNext()) {
            lastEntry = iterator.next();
        }
        return lastEntry.getKey();
    }

    private void addRuler(LinearLayout layout) {
        View ruler = new View(this);
        ruler.setBackgroundColor(0xFF999999);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        layoutParams.setMargins(0, 10, 0, 10);

        layout.addView(ruler, layoutParams);
    }
}
