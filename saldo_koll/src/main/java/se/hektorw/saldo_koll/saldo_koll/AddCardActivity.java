package se.hektorw.saldo_koll.saldo_koll;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AddCardActivity extends Activity implements View.OnClickListener {

    private EditText mInputCardNumber;
    private EditText mInputCvc;
    private EditText mInputName;

    private byte[] mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        mInputCardNumber = (EditText)findViewById(R.id.input_card_nr);
        mInputCvc = (EditText)findViewById(R.id.input_cvc);
        mInputName = (EditText)findViewById(R.id.input_name);

        Button btnAdd = (Button)findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        mId = extras.getByteArray(JojoCard.CARD_ID);


        TextView tv = (TextView)findViewById(R.id.tv_id);
        if (mId == null) {
            tv.setText("No id passed");
        }
        tv.setText(mId.toString());

        mInputCardNumber.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void afterTextChanged(Editable editable) {
                String content = editable.toString();

                if (content.length() == 10) {
                    setInputFocus(mInputCvc);
                }
            }
        });
        mInputCvc.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void afterTextChanged(Editable editable) {
                String content = editable.toString();
                if (content.length() == 4) {
                    setInputFocus(mInputName);
                }
            }
        });

        // Set focus to first edit text
        setInputFocus(mInputCardNumber);
    }

    private void setInputFocus(EditText editText) {
        if (editText == null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } else {
            editText.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }



    @Override
    public void onClick(View view) {
        String cardNumber = mInputCardNumber.getText().toString();
        String cvc = mInputCvc.getText().toString();

        // validate input
        if (cardNumber.length() != 10 || cvc.length() != 4) {
            return;
        }

        CardManager.writeCard(this, new JojoCard(mId, cardNumber, cvc));
    }
}
