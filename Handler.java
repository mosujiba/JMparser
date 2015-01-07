import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;

/**
 * Handles the JMDict XML file.
 * @author Austin Choi
 *
 */

public class Handler extends DefaultHandler {
	
	private List<Entry> list;
	private Entry entry;
	private boolean isNoun;
	private boolean isCommon;
	private String element;
	
	// XML element names
	static final String ENTRY = "entry";
	static final String GLOSS = "gloss";
	static final String KANJI = "keb";
	static final String KANA = "reb";
	static final String POS = "pos"; // part of speech
	static final String COMMON1 = "ke_pri";
	static final String COMMON2 = "re_pri";
	
	// XML values
	static final String NOUN = "noun (common) (futsuumeishi)"; // futsuumeishi
	
	public Handler() 
	{
		list = new ArrayList<Entry>();
	}
	
	public List<Entry> getList() 
	{
		return list;
	}

    public void startElement (String uri, String localName, String qName, Attributes attributes)
    		throws SAXException
    {
		element = qName;
    	// If @ new entry, reset all parameters
    	if (qName.equalsIgnoreCase(ENTRY)) {
    		entry = new Entry();
    		isNoun = false;
    		isCommon = false;
    	}
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException 
    {
    	if (qName.equalsIgnoreCase(ENTRY) && isNoun) // @ end of each noun entry
    	{
        	if (list.contains(entry)) list.get(list.indexOf(entry)).merge(entry); // Merge entries with same definitions
        	else if (entry.isComplete() && isCommon)
        		list.add(entry); // Add new entry under certain conditions
    	}
    }
    
    // Handle the element value
    public void characters(char[] ch, int start, int length) throws SAXException 
    {
    	String value = new String(ch, start, length).trim();
        if (value.length() == 0)
        {
            return; // ignore white space
        }
        switch (element)
        {
        	case GLOSS: if (entry.gloss() == null) entry.addGloss(value); // Stick with first definition
        				break;
        	case KANJI: entry.addKanji(value);
        				break;
        	case KANA:	entry.addKana(value);
        				break;
        	case POS:	isNoun = value.equals(NOUN);
        				break;
        	case COMMON1:
        	case COMMON2:
        				switch (value)
        				{
        					case "nf01":
        					case "nf02":
        					case "nf03":
        					case "nf04":
        						isCommon = true; 
        						break;
        					default: break;
        				}
        				break;
        	default: break;
        }
    }

}
