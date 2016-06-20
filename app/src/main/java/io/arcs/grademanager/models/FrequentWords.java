package io.arcs.grademanager.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import io.arcs.grademanager.utils.ObjectSerializer;

import java.util.*;

/**
 * Created by Patrick on 31.01.2015.
 */
public class FrequentWords {

    private static final String PREFERENCE_NAME = "frequentWords";

    private HashMap<String,Integer> mFrequentWords;
    private SharedPreferences mPrefs;

    public FrequentWords(Context mCtx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        loadLists();
    }

    public void useWord(String word) {
        Integer count = mFrequentWords.get(word);
        if (count == null) mFrequentWords.put(word, 1);
        else mFrequentWords.put(word, count + 1);
    }

    public Set<String> getTopWords() {
        return SortByValue(mFrequentWords).keySet();
    }

    public void removeWord(String word) {
        mFrequentWords.remove(word);
    }

    public void save() {
        mPrefs.edit().putString(PREFERENCE_NAME, ObjectSerializer.getSerializeDisregardException(mFrequentWords)).apply();
    }

    public void loadLists() {
        mFrequentWords = (HashMap<String, Integer>) ObjectSerializer.getDeserializeDisregardException(mPrefs.getString(PREFERENCE_NAME, null));
        if (null == mFrequentWords) mFrequentWords = new HashMap<String, Integer>();
    }

    public static TreeMap<String, Integer> SortByValue (HashMap<String, Integer> map) {
        ValueComparator vc =  new ValueComparator(map);
        TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(vc);
        sortedMap.putAll(map);
        return sortedMap;
    }

}
class ValueComparator implements Comparator<String> {

    Map<String, Integer> map;
    public ValueComparator(Map<String, Integer> base) {
        this.map = base;
    }

    public int compare(String a, String b) {
        if (map.get(a) >= map.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}