package se.hektorw.saldo_koll.saldo_koll;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AddCardActivity extends Activity implements View.OnClickListener{

    private EditText mInputCardNumber;
    private EditText mInputCvc;

    private byte[] mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        mInputCardNumber = (EditText)findViewById(R.id.input_card_nr);
        mInputCvc = (EditText)findViewById(R.id.input_cvc);

        Button btnAdd = (Button)findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        mId = extras.getByteArray(JojoCard.CARD_ID);


        TextView tv = (TextView)findViewById(R.id.tv_id);
        if (mId == null) {
            tv.setText("No id passed");
        }
        tv.setText(mId.toString());
    }

    @Override
    public void onClick(View view) {
        String cardNumber = mInputCardNumber.getText().toString();
        String cvc = mInputCvc.getText().toString();

        if (cardNumber.length() != 10 || cvc.length() != 4) {
            return;
        }

        CardManager.writeCard(this, new JojoCard(mId, cardNumber, cvc));
    }
}
