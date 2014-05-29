package se.hektorw.saldo_koll.saldo_koll;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CheckBalanceActivity extends Activity {

    private final static String TAG = "se.hektorw.Saldo-Koll.CheckBalanceActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getAttributes().windowAnimations = R.style.Fade;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_check_balance);

        Bundle extras = getIntent().getExtras();

        JojoCard card = (JojoCard)extras.getParcelable(JojoCard.JOJO_CARD);


        if (card != null) {
            ((ProgressBar)findViewById(R.id.check_balance_progress_loading)).setVisibility(View.VISIBLE);
            new FetchPage().execute(card);
        }
    }


    /**
     * After page has been fetched
     */
    private void handlePostResponse(Element wrapper) {
        Log.d(TAG, "handlePostResponse");

        Elements labels = wrapper.getElementsByClass("first");
        Elements values = wrapper.getElementsByClass("right");

        Element totalElement = values.get(values.size() - 1);
        Element pendingElement = values.get(values.size() - 2);

        setContentView(R.layout.saldo_layout);
        ((TextView)findViewById(R.id.saldo_layout_tv_total)).setText(getInnermostChild(totalElement).html());
        ((TextView)findViewById(R.id.saldo_layout_tv_pending)).setText(getInnermostChild(pendingElement).html());

       /* LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < values.size(); i++) {
            String label = getInnermostChild(labels.get(i)).html();
            String value = getInnermostChild(values.get(i)).html();

            addBalanceValueToView(parent, label, value);
        }

        RelativeLayout container = (RelativeLayout)findViewById(R.id.check_balance_container);
        ((ProgressBar)findViewById(R.id.check_balance_progress_loading)).setVisibility(View.GONE);
        container.addView(parent);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)parent.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);*/
    }

    private Element getInnermostChild(Element elem) {
        while (elem.children().size() > 0) {
            elem = elem.child(0);
        }
        return elem;
    }

    private void addBalanceValueToView(LinearLayout parent, String label, String value) {
        LinearLayout layout = (LinearLayout)LinearLayout.inflate(this, R.layout.check_balance_value_tpl_layout, null);

        TextView tvLabel = (TextView)TextView.inflate(this, R.layout.check_balance_value_tpl_tv_label, null);
        tvLabel.setText(Html.fromHtml(label).toString());
        TextView tvValue = (TextView)TextView.inflate(this, R.layout.check_balance_value_tpl_tv_value, null);
        tvValue.setText(value);

        layout.addView(tvLabel);
        layout.addView(tvValue);

        parent.addView(layout);
    }



    /**
     * Async task for fetching balance page
     */
    private class FetchPage extends AsyncTask<JojoCard, Void, Element> {
        @Override
        protected Element doInBackground(JojoCard... jojoCards) {
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

                Document dom = Jsoup.parse(responseBody);
                Elements elems = dom.getElementsByClass("saldo_ok_wrapper");
                Element wrapper = elems.get(0);

                return wrapper;
            } catch (ClientProtocolException e) {
                Log.d(TAG, e.toString());
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Element element) {
            if (element == null) {
                Log.d(TAG, "error when fetching saldo");
            }

            handlePostResponse(element);
        }
    }

}
