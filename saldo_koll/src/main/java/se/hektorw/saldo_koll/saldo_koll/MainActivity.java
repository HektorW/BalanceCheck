package se.hektorw.saldo_koll.saldo_koll;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




public class MainActivity extends ActionBarActivity {

    private static final String TAG = "se.hektorw.saldo_koll.MainActivity";

    private TextView mTextView;
    private NfcAdapter mNfcAdapter;

    private JojoCard[] mCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        mTextView = (TextView)findViewById(R.id.textView_explanation);

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
