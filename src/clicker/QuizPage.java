package clicker;

import support.Question;
import support.UserSession;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iitbombay.clicker.R;

import datahandler.SubmitAnswerToWS;

public class QuizPage extends FragmentActivity {
	String classname = "QuizPage";

	TextView txtvw_username;
	TextView txtvw_timer;
	TextView txtvw_status;
	Button btn_submit;
	Button btn_exit;

	UserSession userSession;
	Question question;

	CountDownTimer countdowntimer;

	QuestionFragment  fragment_question;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_quiz);

		userSession = ApplicationContext.getThreadSafeUserSession();
		question = ApplicationContext.getThreadSafeQuestion();

		txtvw_username = (TextView) findViewById(R.id.txtvw_username);
		txtvw_timer = (TextView) findViewById(R.id.txtvw_timer);
		txtvw_status = (TextView) findViewById(R.id.txtvw_status);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_exit = (Button) findViewById(R.id.btn_exit);

		fragment_question = (QuestionFragment) getSupportFragmentManager().findFragmentById(R.id.frag_question);

		txtvw_username.setText(userSession.username);

		if(question.timed){
			txtvw_timer.setText(question.time+"");
			countdowntimer = new CountDownTimer(question.time*1000, 1000) {

				public void onTick(long millisUntilFinished) {
					long secs = millisUntilFinished / 1000;
					long minsleft = secs/60;
					long secsleft = secs % 60; 
					txtvw_timer.setText(minsleft+":"+secsleft);
				}

				public void onFinish() {
					txtvw_timer.setText("Up!");
					fragment_question.disableBtns();
					new SubmitAnswerToWS().execute(QuizPage.this);
				}
			}.start();
		}

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submitQuizDialog();
			}
		});

		btn_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitQuizDialog();
			}
		});

	}

	public void updateUI(String msg){
		txtvw_status.setText(msg);
	}

	public void disableBtns(){
		btn_submit.setEnabled(false);
		if(countdowntimer!=null) countdowntimer.cancel();
		fragment_question.disableBtns();
	}

	@Override
	protected void onStop() {
		super.onStop();
		disableBtns();
	}

	public void gotoLoginPage(){
		Intent intent = new Intent(this,LoginPage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void submitQuizDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Submit Quiz");
		builder.setMessage("Are you sure?");

		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// Do nothing but close the dialog
				dialog.dismiss();
				if(question.answers.length()!=0){
					new SubmitAnswerToWS().execute(QuizPage.this);
				}else{
					Toast.makeText(getBaseContext(), "Please answer...", Toast.LENGTH_SHORT).show();
				}
			}

		});

		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void exitQuizDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Exit quiz confirmation");
		builder.setMessage("Are you sure?");

		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// Do nothing but close the dialog
				dialog.dismiss();
				gotoLoginPage();
			}

		});

		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onBackPressed() {
		exitQuizDialog();
	}
}
