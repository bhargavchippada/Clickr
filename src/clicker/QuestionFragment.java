package clicker;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import support.Question;
import support.Utils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.iitbombay.clicker.R;

public class QuestionFragment extends Fragment {

	private String classname = "QuestionFragment";

	RadioGroup rg_options;
	LinearLayout ll_checkboxes;
	LinearLayout ll_truefalse;
	Button btn_true;
	Button btn_false;

	EditText edtxt_textual;

	Question question;
	HashMap<Integer,Integer> optionIds = new HashMap<Integer,Integer>();

	LayoutInflater layoutinflater;

	FragmentActivity fragactivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layoutinflater = inflater;
		fragactivity = getActivity();
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_question, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		question = ApplicationContext.getThreadSafeQuestion();
		int type = question.type;

		if(type==-1) Toast.makeText(fragactivity, "Invalid Question!", Toast.LENGTH_SHORT).show();

		if(type==0) singleMCQInit();
		else if(type==1) multipleMCQinit();
		else if(type==2) truefalseInit();
		else if(type==3 || type==4) wordTextualInit();
	}

	void singleMCQInit(){
		rg_options = (RadioGroup) fragactivity.findViewById(R.id.rg_options);
		rg_options.setVisibility(View.VISIBLE);

		for(int i=0;i<question.options.length();i++){
			RadioButton row = (RadioButton) layoutinflater.inflate(R.layout.template_radiobtn, rg_options, false);
			try {
				row.setText(question.options.get(i).toString());
			} catch (JSONException e) {
				Utils.logv(classname,"Json Error while setting the options", e);
				e.printStackTrace();
			}
			rg_options.addView(row);
			optionIds.put(row.getId(), i);
			Utils.logv(classname, row.getId()+"");
		}

		rg_options.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Utils.logv(classname, checkedId+" is checked now");
				question.answers=new JSONArray();
				question.answers.put(optionIds.get(checkedId));
				Utils.logv(classname, "clicked: "+optionIds.get(checkedId));
			}
		});
	}

	void multipleMCQinit(){
		ll_checkboxes = (LinearLayout) fragactivity.findViewById(R.id.ll_checkboxes);
		ll_checkboxes.setVisibility(View.VISIBLE);

		for(int i=0;i<question.options.length();i++){
			CheckBox row = (CheckBox) layoutinflater.inflate(R.layout.template_checkbox, ll_checkboxes, false);
			try {
				row.setText(question.options.get(i).toString());
			} catch (JSONException e) {
				Utils.logv(classname,"Json Error while setting the options", e);
				e.printStackTrace();
			}
			ll_checkboxes.addView(row);
			row.setId(Utils.generateViewId());
			optionIds.put(row.getId(), i);

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					boolean checked = ((CheckBox) view).isChecked();

					int checkedid = optionIds.get(view.getId());

					try {
						question.answers.put(checkedid, checked);
					} catch (JSONException e) {
						e.printStackTrace();
						Utils.logv(classname, "Json oncheckboxclick error: "+checkedid,e);
					}

					Utils.logv(classname, "checklist: "+question.answers.toString());
				}
			});
			question.answers.put(false);
			Utils.logv(classname, row.getId()+"");
		}
	}

	void truefalseInit(){
		ll_truefalse = (LinearLayout) fragactivity.findViewById(R.id.ll_truefalse);
		ll_truefalse.setVisibility(View.VISIBLE);
		btn_true = (Button) fragactivity.findViewById(R.id.btn_true);
		btn_false = (Button) fragactivity.findViewById(R.id.btn_false);


		btn_true.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_true.setBackgroundResource(R.drawable.btn_green_style);
				btn_true.setTextColor(fragactivity.getResources().getColor(android.R.color.white));
				btn_false.setBackgroundResource(R.drawable.btn_grey_style);
				btn_false.setTextColor(fragactivity.getResources().getColor(android.R.color.white));

				try {
					question.answers.put(0,1);
				} catch (JSONException e) {
					e.printStackTrace();
					Utils.logv(classname, "Json error!",e);
				}

				Utils.logv(classname, "answer: "+question.answers.toString());
			}
		});


		btn_false.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_false.setBackgroundResource(R.drawable.btn_red_style);
				btn_false.setTextColor(fragactivity.getResources().getColor(android.R.color.white));
				btn_true.setBackgroundResource(R.drawable.btn_grey_style);
				btn_true.setTextColor(fragactivity.getResources().getColor(android.R.color.white));

				try {
					question.answers.put(0,0);
				} catch (JSONException e) {
					e.printStackTrace();
					Utils.logv(classname, "Json error!",e);
				}

				Utils.logv(classname, "answer: "+question.answers.toString());
			}
		});
	}

	void wordTextualInit(){
		edtxt_textual = (EditText) fragactivity.findViewById(R.id.edtxt_textual);

		edtxt_textual.setVisibility(View.VISIBLE);

		edtxt_textual.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String result;
				if(question.type==3) {
					result = s.toString().replaceAll(" ", "");
					if (!s.toString().equals(result)) {
						edtxt_textual.setText(result);
						edtxt_textual.setSelection(result.length());
					}
				}else if(question.type==4) result = s.toString();
				else {
					result="";
					Utils.logv(classname, "Textual with undefined type!");
				}

				try {
					question.answers.put(0, result.trim());
				} catch (JSONException e) {
					e.printStackTrace();
					Utils.logv(classname, "Json error!",e);
				}
				
				Utils.logv(classname, "answer: "+question.answers.toString());
			}
		});
	}
	
	public void disableBtns(){
		question = ApplicationContext.getThreadSafeQuestion();
		int type = question.type;
		
		if(type==0) {
			for (int i = 0; i < rg_options.getChildCount(); i++) {
				rg_options.getChildAt(i).setEnabled(false);
			}
		}else if(type==1){
			for (int i = 0; i < ll_checkboxes.getChildCount(); i++) {
				ll_checkboxes.getChildAt(i).setEnabled(false);
			}
		}else if(type==2){
			btn_true.setEnabled(false);
			btn_false.setEnabled(false);
		}else if(type==3 || type==4){
			edtxt_textual.setEnabled(false);
		}
	}

}