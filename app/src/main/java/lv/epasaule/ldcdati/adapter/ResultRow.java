package lv.epasaule.ldcdati.adapter;

public class ResultRow {

    public static final ResultRow EMPTY = new ResultRow("", "");

    private final CharSequence key;
    private final CharSequence value;

    public ResultRow(CharSequence key, CharSequence value) {
        this.key = key;
        this.value = value;
    }

    public CharSequence key() {
        return key;
    }

    public CharSequence value() {
        return value;
    }
}
