package se.hektorw.saldo_koll.saldo_koll;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hektor on 2014-05-25.
 */
public class CardManager {

    private final static String PREFERENCE_FILE_KEY = "preference_cards";

    private final static String KEY_CARDS = "key_cards";
    private final static String SPLIT_CHAR = "@";

    public static boolean writeCard(Context context, JojoCard card) {
        String cardStr = card.toWriteableString();

        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        String currentCards = sp.getString(KEY_CARDS, "");

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_CARDS, currentCards + (currentCards.length() > 0 ? SPLIT_CHAR : "") + cardStr);

        editor.commit();

        return true;
    }

    public static JojoCard[] readCards(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        String allCards = sp.getString(KEY_CARDS, "");

        if (allCards.length() == 0)
            return null;

        String[] split = allCards.indexOf(SPLIT_CHAR) == -1 ? new String[]{ allCards } : allCards.split(SPLIT_CHAR);

        JojoCard[] cards = new JojoCard[split.length];
        for (int i = 0; i < split.length; ++i) {
            cards[i] = new JojoCard(split[i]);
        }

        return cards;
    }

    public static void Clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_CARDS, "");
        editor.commit();
    }
}
