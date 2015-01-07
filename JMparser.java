import java.io.*;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * (Default) 	Takes a JMDict XML file and creates a trimmed-down XML containing only common nouns.
 * 				First argument = JMdict file (without .xml extension).
 * (Optional) 	Takes a JMDict XML file and a text file containing gloss words, and creates a trimmed-down
 * 				XML containing only those gloss words.
 * 				First argument = JMdict file (without .xml extension)
 * 				Second argument = Gloss file (without .txt extension)
 * @author Austin Choi
 *
 */

public class JMparser {
	
	public static void main(String args[]) {
		System.setProperty("jdk.xml.entityExpansionLimit", "0");
		String src = args[0];
		String dest = src + "_nouns";
		String gloss = args[1];
		File file = new File(src + ".xml");
		Handler handler = new Handler();
		if (!file.exists())
		{
			System.err.println(src + ".xml not found!");
			System.exit(1);
		}
		File glossFile = new File(gloss + ".txt");
		if (glossFile.exists())
		{
			try {
				handler = new GlossHandler(new Scanner(glossFile));
				dest = src + "_" + gloss;
			} catch (FileNotFoundException e) {
				System.err.println(gloss + ".txt not found!");
			}
		}
		else
		{
			System.out.println("No gloss file specified. Will scan XML for common nouns.");
		}
		try {
			System.out.println("Parsing XML... ");
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			saxParser.parse(file, handler);
			System.out.println("Done!");
			List<Entry> entries = handler.getList();
			writeXML(dest, entries);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeXML(String xmlName, List<Entry> entries) throws ParserConfigurationException, TransformerException
	{
		System.out.println("Writing XML...");
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();	
		Element root = doc.createElement(xmlName);
		doc.appendChild(root);
		int incomplete = 0;
		int created = 0;
		for (Entry entry: entries)
		{
			if (!entry.isComplete()) 
			{
				incomplete++;
				continue; // Write only complete entries
			}
			Element e = doc.createElement("entry");
			root.appendChild(e);
			
			Element gloss = doc.createElement("gloss");
			gloss.appendChild(doc.createTextNode(entry.gloss()));
			e.appendChild(gloss);
			
			for (String val: entry.kana())
			{
				Element kana = doc.createElement("kana");
				kana.appendChild(doc.createTextNode(val));
				e.appendChild(kana);
			}
			
			for (String val: entry.kanji())
			{
				Element kanji = doc.createElement("kanji");
				kanji.appendChild(doc.createTextNode(val));
				e.appendChild(kanji);
			}
			created++;
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT,"yes");
		DOMSource source = new DOMSource(doc);
		String filename = xmlName + ".xml";
		StreamResult result = new StreamResult(new File(filename));
		transformer.transform(source, result);
		System.out.println(incomplete + " entries incomplete.");
		System.out.println(created + " entries created.");
		System.out.println(filename + " saved!");
	}
	

}
