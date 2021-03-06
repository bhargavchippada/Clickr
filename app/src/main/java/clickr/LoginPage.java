package clickr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iitbombay.clickr.R;

import java.util.logging.Logger;

import servercommunication.Authentication;
import support.AppSettings;

/**
 * connection settings and login information page
 *
 * @author bhargav
 */
public class LoginPage extends Activity {

    protected final Logger LOGGER = Logger.getLogger(getClass().getName());

    EditText edtxt_ipaddress;
    EditText edtxt_port;
    EditText edtxt_username;
    EditText edtxt_password;
    EditText edtxt_servername;

    CheckBox cbox_savesettings;

    Button btn_connect;
    Button btn_exit;
    TextView txtvw_status;

    //To control the click event of the login button, essentially to stop spamming
    double lastTime = -2.0;
    int clickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_login);

        edtxt_ipaddress = (EditText) findViewById(R.id.edtxt_ipaddress);
        edtxt_port = (EditText) findViewById(R.id.edtxt_port);
        edtxt_username = (EditText) findViewById(R.id.edtxt_username);
        edtxt_password = (EditText) findViewById(R.id.edtxt_password);
        edtxt_servername = (EditText) findViewById(R.id.edtxt_servername);
        cbox_savesettings = (CheckBox) findViewById(R.id.cbox_savesettings);

        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_exit = (Button) findViewById(R.id.btn_exit);
        txtvw_status = (TextView) findViewById(R.id.txtvw_status);

        //initializes view values
        initializeViews();

        btn_connect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cbox_savesettings.isChecked()) {
                    updateSharedPref(1);
                } else {
                    updateSharedPref(0);
                }

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
                new Authentication().execute(LoginPage.this);
                LOGGER.info("Login button is pressed");
            }
        });

        btn_exit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //invalidate session and question
        ApplicationContext.invalidateSession();
    }

    /**
     * Update the status message textView
     *
     * @param msg
     */
    public void updateUI(String msg) {
        txtvw_status.setText(msg);
    }

    /**
     * bool == 0 means nothing is saved, bool == 1 means the settings are saved on the device
     *
     * @param bool
     */
    void updateSharedPref(int bool) {
        String ip = "", p = "";
        String user = "", pass = "", servername = "";
        boolean check = false;
        if (bool == 1) {
            ip = edtxt_ipaddress.getText().toString();
            p = edtxt_port.getText().toString();
            user = edtxt_username.getText().toString();
            pass = edtxt_password.getText().toString();
            servername = edtxt_servername.getText().toString();
            check = true;
        }
        SharedPreferences sharedPref = getBaseContext()
                .getSharedPreferences(AppSettings.preference_file_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_ipadress), ip);
        editor.putString(getString(R.string.saved_port), p);
        editor.putString(getString(R.string.saved_username), user);
        editor.putString(getString(R.string.saved_password), pass);
        editor.putString(getString(R.string.saved_servername), servername);
        editor.putBoolean(getString(R.string.saved_savesettings), check);
        editor.commit();
        AppSettings.updateUrl(edtxt_ipaddress.getText().toString(), edtxt_port.getText().toString());
    }

    void initializeViews() {
        SharedPreferences sharedPref = getBaseContext()
                .getSharedPreferences(AppSettings.preference_file_key, Context.MODE_PRIVATE);
        edtxt_ipaddress.setText(sharedPref.getString(getString(R.string.saved_ipadress), ""));
        edtxt_port.setText(sharedPref.getString(getString(R.string.saved_port), ""));
        edtxt_username.setText(sharedPref.getString(getString(R.string.saved_username), ""));
        edtxt_password.setText(sharedPref.getString(getString(R.string.saved_password), ""));
        edtxt_servername.setText(sharedPref.getString(getString(R.string.saved_servername), ""));
        cbox_savesettings.setChecked(sharedPref.getBoolean(getString(R.string.saved_savesettings), false));
    }

    public String getUsername() {
        return edtxt_username.getText().toString();
    }

    public String getPassword() {
        return edtxt_password.getText().toString();
    }

    public String getServerName() {
        return edtxt_servername.getText().toString();
    }

    public void gotoHomePage() {
        LOGGER.info("Open Home Page");
        Intent intent = new Intent(getApplication(), HomePage.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtvw_status.setText("Click to login");
    }
}
