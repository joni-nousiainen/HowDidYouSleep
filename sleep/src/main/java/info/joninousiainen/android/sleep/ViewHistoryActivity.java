package info.joninousiainen.android.sleep;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ViewHistoryActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_history);

        Map<Date, String> answersByDate = getAllAnswersInDescendingOrder();
        if (answersByDate.isEmpty()) {
            return;
        }

        addDatesWhichHaveNoAnswer(answersByDate);

        addLast30DaysStatistics(answersByDate);

        DateFormat dateFormat = new SimpleDateFormat(getText(R.string.history_date_format).toString());
        LinearLayout allAnswersLayout = (LinearLayout) findViewById(R.id.allAnswersLayout);
        Calendar calendar = Calendar.getInstance();

        for (Map.Entry<Date, String> entry : answersByDate.entrySet()) {
            Date date = entry.getKey();
            String answer = entry.getValue();

            TextView text = new TextView(this);
            text.setTextAppearance(android.R.style.TextAppearance_Medium);
            text.setText(dateFormat.format(date) + ": " + getLocalizedAnswer(answer));
            allAnswersLayout.addView(text);

            // Add ruler between weeks
            calendar.setTime(date);
            if (calendar.getFirstDayOfWeek() == calendar.get(Calendar.DAY_OF_WEEK)) {
                addRuler(allAnswersLayout);
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

                String answer = preferences.getString(key, SharedConstants.ANSWER_EMPTY);

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
                answersByDate.put(date, SharedConstants.ANSWER_EMPTY);
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

    private void addLast30DaysStatistics(Map<Date, String> answersByDate) {
        // TODO: Don't show this when oldest answer is less than 30 days old.

        Map<String, Integer> answerCounts = new LinkedHashMap<>();
        answerCounts.put(SharedConstants.ANSWER_YES, 0);
        answerCounts.put(SharedConstants.ANSWER_NO, 0);
        answerCounts.put(SharedConstants.ANSWER_EMPTY, 0);
        int counter = 0;
        final int maxDays = 30;
        for (Map.Entry<Date, String> entry : answersByDate.entrySet()) {
            String answer = entry.getValue();
            Integer count = answerCounts.get(answer);
            answerCounts.put(answer, count + 1);

            if (counter < (maxDays - 1)) {
                counter++;
            }
            else {
                break;
            }
        }
        LinearLayout last30DaysLayout = (LinearLayout) findViewById(R.id.last30DaysLayout);
        for (Map.Entry<String, Integer> entry : answerCounts.entrySet()) {
            StringBuilder answer = new StringBuilder(getLocalizedAnswer(entry.getKey()));
            answer.append(": ");
            Integer value = entry.getValue();
            answer.append(value);
            answer.append(" (");
            answer.append(BigDecimal.valueOf(100)
                    .multiply(BigDecimal.valueOf(entry.getValue()))
                    .divide(BigDecimal.valueOf(maxDays), 0, BigDecimal.ROUND_UP));
            answer.append(" %)");

            TextView text = new TextView(this);
            text.setTextAppearance(android.R.style.TextAppearance_Medium);
            text.setText(answer.toString());
            last30DaysLayout.addView(text);
        }
    }

    private CharSequence getLocalizedAnswer(String answer) {
        CharSequence localizedAnswer;
        switch (answer) {
            case SharedConstants.ANSWER_YES:
                localizedAnswer = getText(R.string.history_answer_yes);
                break;
            case SharedConstants.ANSWER_NO:
                localizedAnswer = getText(R.string.history_answer_no);
                break;
            default:
                localizedAnswer = getText(R.string.history_answer_empty);
        }
        return localizedAnswer;
    }

    private void addRuler(LinearLayout layout) {
        View ruler = new View(this);
        ruler.setBackgroundColor(0xFF999999);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        layoutParams.setMargins(0, 10, 0, 10);

        layout.addView(ruler, layoutParams);
    }
}
