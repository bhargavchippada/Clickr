package clickr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iitbombay.clickr.R;

import java.util.logging.Logger;

import servercommunication.LoadQuizFromWS;
import support.UserSession;

/**
 * Home page of the user with option to start quiz
 *
 * @author bhargav
 */
public class HomePage extends Activity {
    private static String CLASSNAME = "HomePage";
    private final static Logger LOGGER = Logger.getLogger(CLASSNAME);

    TextView txtvw_roll_number;
    TextView txtvw_name;
    TextView txtvw_ipaddress;
    TextView txtvw_clsnm;
    TextView txtvw_status;

    Button btn_startquiz;
    ProgressBar pbar_startquiz;
    Button btn_exit;

    //To control the click event of the login button, essentially to stop spamming
    double lastTime = -2.0;
    int clickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);

        txtvw_roll_number = (TextView) findViewById(R.id.txtvw_roll_number);
        txtvw_name = (TextView) findViewById(R.id.txtvw_name);
        txtvw_ipaddress = (TextView) findViewById(R.id.txtvw_ipaddress);
        txtvw_clsnm = (TextView) findViewById(R.id.txtvw_clsnm);
        txtvw_status = (TextView) findViewById(R.id.txtvw_status);
        btn_exit = (Button) findViewById(R.id.btn_exit);

        if (!UserSession.isSessionValid()) {
            LOGGER.info("UserSession is invalid");
            gotoLoginPage();
        }

        txtvw_roll_number.setText(UserSession.username);
        txtvw_name.setText(UserSession.name);
        txtvw_clsnm.setText(UserSession.classname);
        txtvw_ipaddress.setText(UserSession.ip);

        btn_startquiz = (Button) findViewById(R.id.btn_startquiz);
        pbar_startquiz = (ProgressBar) findViewById(R.id.pbar_startquiz);

        btn_startquiz.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                double present_time = System.currentTimeMillis() / 1000;
                final int diff_time = (int) (present_time - lastTime);
                //This ensures a gap of 2 secs before reposting data
                if (diff_time < 2 && clickTime != diff_time) {
                    clickTime = diff_time;
                    LOGGER.info(clickTime + "");
                    Toast.makeText(getBaseContext(), "Wait before trying", Toast.LENGTH_SHORT).show();
                    return;
                } else if (diff_time < 2) {
                    return;
                }

                clickTime = 0;
                lastTime = present_time;
                new LoadQuizFromWS().execute(HomePage.this);
                pbar_startquiz.setVisibility(View.VISIBLE);
            }
        });

        btn_exit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoLoginPage();
            }
        });
    }

    /**
     * Update the status message textView
     *
     * @param msg
     */
    public void updateUI(String msg, int pbar_state) {
        txtvw_status.setText(msg);
        pbar_startquiz.setVisibility(pbar_state);
    }

    public void gotoQuizPage() {
        Intent intent = new Intent(this, QuizPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void gotoLoginPage() {
        Intent intent = new Intent(this, LoginPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtvw_status.setText("Click to start quiz");
    }
}
