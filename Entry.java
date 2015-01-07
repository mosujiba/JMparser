import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Austin Choi
 *
 */

public class Entry implements Comparable<Entry> {
	
	private String gloss;
	private List<String> kana;
	private List<String> kanji;
	
	public Entry()
	{
		
	}
	
	public Entry(String gloss)
	{
		this.gloss = gloss;
	}
	
	public boolean isComplete()
	{
		return ( (gloss != null) && (kana != null) && (kanji !=null));
	}
	
	/**
	 * If gloss already exists, overwrite it.
	 */
	public void addGloss(String s)
	{
		this.gloss = s;
	}
	
	public void addKana(String s)
	{
		if (kana == null) kana = new ArrayList<String>();
		if (!kana.contains(s)) kana.add(s);
	}
	
	public void addKanji(String s)
	{
		if (kanji == null) kanji = new ArrayList<String>();
		if (!kanji.contains(s)) kanji.add(s);
	}
	
	public String gloss()
	{
		return gloss;
	}
	
	public Iterable<String> kana()
	{
		return kana;
	}
	
	public Iterable<String> kanji()
	{
		return kanji;
	}
	
	/**
	 * Adds the kana & kanji of the other entry to this one.
	 * @param o Other entry
	 */
	public void merge(Entry o)
	{
		if (o.kana() != null)
		{
			for (String kana: o.kana())
				addKana(kana);
		}
		if (o.kanji() != null)
		{
			for (String kanji: o.kanji())
				addKanji(kanji);
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Entry && compareTo((Entry) o) == 0) return true;
		else return false;
	}
	
	@Override
	public int compareTo(Entry o) {
		if (gloss == null) return -1;
		else if (o == null) return 1;
		else return (gloss.compareTo(o.gloss())) ;
	}

}
