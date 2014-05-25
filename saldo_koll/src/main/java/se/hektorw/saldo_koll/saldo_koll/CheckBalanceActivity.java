package se.hektorw.saldo_koll.saldo_koll;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CheckBalanceActivity extends Activity {

    private final static String TAG = "se.hektorw.Saldo-Koll.CheckBalanceActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);

        Bundle extras = getIntent().getExtras();

        JojoCard card = (JojoCard)extras.getParcelable(JojoCard.JOJO_CARD);

        if (card != null) {
            new FetchPage().execute(card);
        }
    }








    /**
     * After page has been fetched
     * @param responseBody
     */
    private void handlePostResponse(String responseBody) {
        Log.d(TAG, "handlePostResponse");

        Spanned result = Html.fromHtml(responseBody);

        Document dom = Jsoup.parse(responseBody);

        Elements elems = dom.getElementsByClass("saldo_ok_wrapper");
        Element wrapper = elems.get(0);

        Elements prices = wrapper.getElementsByClass("right");

        Element element_saldo = prices.get(0);
        Element element_pending = prices.get(1);

        String price_saldo = element_saldo.child(0).html();
        String price_pending = element_pending.child(0).html();

        setContentView(R.layout.activity_check_balance);
        TextView tv_saldo = (TextView)findViewById(R.id.saldo);
        TextView tv_pending = (TextView)findViewById(R.id.pending);

        tv_saldo.setText(price_saldo);
        tv_pending.setText(price_pending);
    }



    /**
     * Async task for fetching balance page
     */
    private class FetchPage extends AsyncTask<JojoCard, Void, String> {
        @Override
        protected String doInBackground(JojoCard... jojoCards) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.shop.skanetrafiken.se/kollasaldo.html");

            JojoCard card = jojoCards[0];

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("cardno", card.CardNumber));
                nameValuePairs.add(new BasicNameValuePair("backno", card.CVC));
                nameValuePairs.add(new BasicNameValuePair("ST_CHECK_SALDO", "Se saldo"));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                String responseBody = EntityUtils.toString(response.getEntity());

                return responseBody;
            } catch (ClientProtocolException e) {
                Log.d(TAG, e.toString());
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                Log.d(TAG, "error when fetching saldo");
            }

            handlePostResponse(responseBody);
        }
    }

}
