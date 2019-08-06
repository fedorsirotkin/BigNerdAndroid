package ru.fedorsirotkin.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class QuizActivity extends Activity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Метод onCreate(Bundle) вызван");

        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        // Регистрация сообщения с уровнем отладки "debug"
        Log.d(TAG, "Текущий индекс вопроса: " + mCurrentIndex);
        Question question;
        try {
            question = mQuestionBank[mCurrentIndex];
        } catch (ArrayIndexOutOfBoundsException ex) {
            // Регистрация сообщения с уровнем отладки "error"
            // вместе с трассировкой стека исключений
            Log.e(TAG, "Индекс вышел за пределы: ", ex);
        }

        // Получение ссылок на виджеты
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();

        mTrueButton = (Button) findViewById(R.id.true_button);
        // Назначение слушателя на кнопку "Да"
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        // Назначение слушателя на кнопку "Да"
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        // Назначение слушателя на кнопки "Назад"
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = mCurrentIndex - 1;
                if (mCurrentIndex == 0) {
                    mCurrentIndex = mQuestionBank.length - 1;
                }
                mIsCheater = false;
                updateQuestion();
            }
        });

        // Назначение слушателя на кнопку "Далее"
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        // Назначение слушателя на кнопку "Считерить!"
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Запуск CheatActivity
                // Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                // startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    // Переопределение методов жизненного цикла
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Метод onStart() вызван");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Метод onResume() вызван");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Метод onPause() вызван");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Метод onStop() вызван");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Метод onDestroy() вызван");
    }

    // Обновляет вопрос
    private void updateQuestion() {
        Log.d(TAG, "Обновление текста вопроса", new Exception());

        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    // Проверяет ответ и выводит уведомление
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        // Создание уведомления
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
                .show();
    }
}
