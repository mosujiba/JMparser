import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * Handles JMdict file & gloss file.
 * @author Austin Choi
 *
 */

public class GlossHandler extends Handler {
	
	private List<Entry> list;
	private Entry entry;
	private String element;

	public GlossHandler(Scanner glosses)
	{
		// Pre-populate list with gloss entries
		list = new ArrayList<Entry>();
		while (glosses.hasNextLine())
		{
			String line = glosses.nextLine();
			Entry gEntry = new Entry(line);
			if (!list.contains(gEntry)) list.add(gEntry);
		}
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
    	}
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException 
    {
    	if (qName.equalsIgnoreCase(ENTRY)) // @ end of each noun entry
    	{
        	if (list.contains(entry)) list.get(list.indexOf(entry)).merge(entry); // Merge entries with same definitions
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
        	default: break;
        }
    }

}
