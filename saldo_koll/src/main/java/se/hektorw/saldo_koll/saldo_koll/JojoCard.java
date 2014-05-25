package se.hektorw.saldo_koll.saldo_koll;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hektor on 2014-05-25.
 */
public class JojoCard implements Parcelable{
    public final static String JOJO_CARD = "jojo_card";
    public final static String CARD_NUMBER = "card_number";
    public final static String CARD_CVC = "card_cvc";
    public final static String CARD_ID = "card_id";

    private final static String PARTS_SPLIT_CHAR = ";";
    private final static String BYTE_SPLIT_CHAR = ",";

    public String CardNumber;
    public String CVC;
    public byte[] ID;

    public JojoCard(byte[] ID, String CardNumber, String CVC) {
        this.CardNumber = CardNumber;
        this.CVC = CVC;
        this.ID = ID;
    }

    public JojoCard(String savedStr) {
        String[] parts = savedStr.split(PARTS_SPLIT_CHAR);
        String[] byteParts = parts[0].split(BYTE_SPLIT_CHAR);
        this.ID = new byte[] {
            Byte.parseByte(byteParts[0]),
            Byte.parseByte(byteParts[1]),
            Byte.parseByte(byteParts[2]),
            Byte.parseByte(byteParts[3])
        };
        this.CardNumber = parts[1];
        this.CVC = parts[2];
    }


    public boolean compareID(byte[] other) {
        if (ID == null || ID.length != other.length)
            return  false;

        for (int i = 0; i < ID.length; ++i) {
            if (ID[i] != other[i])
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "[ { " + ID[0] + ", " + ID[1] + ", " + ID[2] + ", " + ID[3] + " }, " + this.CardNumber + ", " + this.CVC + " ]";
    }

    public String toWriteableString() {
        return "[" + ID[0] + BYTE_SPLIT_CHAR + ID[1] + BYTE_SPLIT_CHAR + ID[2] + BYTE_SPLIT_CHAR + ID[3] +"]" + PARTS_SPLIT_CHAR + this.CardNumber + PARTS_SPLIT_CHAR + this.CVC;
    }

    /**
     * Parcelable
     * @param parcel
     */
    public JojoCard(Parcel parcel) {
        String[] strData = new String[2];
        byte[] byteData = new byte[4];

        parcel.readStringArray(strData);
        parcel.readByteArray(byteData);

        this.CardNumber = strData[0];
        this.CVC = strData[1];
        this.ID = byteData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{ this.CardNumber, this.CVC });
        parcel.writeByteArray(this.ID);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public JojoCard createFromParcel(Parcel parcel) {
            return new JojoCard(parcel);
        }

        @Override
        public JojoCard[] newArray(int i) {
            return new JojoCard[i];
        }
    };
}
