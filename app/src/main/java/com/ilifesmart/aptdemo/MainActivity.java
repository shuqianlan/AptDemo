package com.ilifesmart.aptdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ilifesmart.IdInjectUtil;
import com.wuzh.lib.AutoCreat;
import com.wuzh.lib.IdInject;
import com.wuzh.lib.InjectView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

@AutoCreat
public class MainActivity extends AppCompatActivity {

	@IdInject(R.id.check_box)
	CheckBox mCheckBox;

	@InjectView(R.id.check_box)
	CheckBox mCheckBox2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		ButterKnife.bind(this);

		IdInjectUtil.inject(this);
		mCheckBox.setText("BBBBBBBBBBB");
	}

//	@OnCheckedChanged(R.id.check_box)
//	void onCheckChanged(CompoundButton button, boolean isChecked) {
//	}
}
