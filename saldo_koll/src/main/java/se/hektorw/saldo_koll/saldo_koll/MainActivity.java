package se.hektorw.saldo_koll.saldo_koll;

import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "se.hektorw.saldo_koll.MainActivity";

    private TextView mTextView;
    private NfcAdapter mNfcAdapter;

    private JojoCard[] mCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getAttributes().windowAnimations = R.style.Fade;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.start_layout);

        Log.d(TAG, "onCreate");

//        mTextView = (TextView)findViewById(R.id.textView_explanation);

        TextView tv = (TextView)findViewById(R.id.tv_jojo);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/coolvetica.ttf");
        tv.setTypeface(tf);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled fool");
        }
        mCards = CardManager.readCards(this);

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, action);

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] id = tag.getId();

            JojoCard card = getCard(id);

            if (card == null) {
                addCard(id);
            } else {
                handleCard(card);
            }
        }
    }

    private JojoCard getCard(byte[] id) {
        if (mCards != null) {
            for (int i = 0; i < mCards.length; ++i) {
                if (mCards[i].compareID(id))
                    return mCards[i];
            }
        }
        return null;
    }


    private void addCard(byte[] id) {
        Intent intent = new Intent(this, AddCardActivity.class);
        intent.putExtra(JojoCard.CARD_ID, id);

        startActivity(intent);
    }

    private void handleCard(JojoCard card) {
        Intent intent = new Intent(this, CheckBalanceActivity.class);
        intent.putExtra(JojoCard.JOJO_CARD, card);

        startActivity(intent);
    }





}
