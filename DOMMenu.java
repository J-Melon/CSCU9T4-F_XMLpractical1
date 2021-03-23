import java.io.*; //Input-output

import javax.xml.XMLConstants;
import javax.xml.parsers.*; //Parsers
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.*; //XPath
import javax.xml.validation.*; //Validators
import javax.xml.transform.*; //DOM source classes

//import com.sun.xml.internal.bind.marshaller.NioEscapeHandler;
import org.w3c.dom.*; //DOM
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 DOM handler to read XML information, to create this, and to print it
 @author CSCU9T4, University of Stirling
 @version 11/03/20
 */
public class DOMMenu
{
	/** Document builder */
	private static DocumentBuilder builder = null;
	
	/** XML document */
	private static Document document = null;
	
	/** XPath expression */
	private static XPath path = null;
	
	/** XML Schema for validation */
	private static Schema schema = null;
	
	/*----------------------------- General Methods ----------------------------*/
	
	/**
	 Main program to call DOM parser.
	 @param args command-line arguments
	 */
	public static void main(String[] args)
	{
		
		//Loads XML file into "document"
		loadDocument(args[0]); //small_menu.xml
		//Validation
		if (validateDocument(args[1])) //small_menu.xsd
		{
			//Prints staff.xml using DOM methods and XPath queries
			try { printNodes(); } catch (XPathException e) { System.err.println(e.getMessage()); }
		}
	}
	
	/**
	 Set global document by reading the given file.
	 @param filename XML file to read
	 */
	private static void loadDocument(String filename)
	{
		try
		{
			//Creates a document builder
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builder = builderFactory.newDocumentBuilder();
			
			//Creates an XPath expression
			XPathFactory xpathFactory = XPathFactory.newInstance();
			path = xpathFactory.newXPath();
			
			//Parses the document for later searching
			document = builder.parse(new File(filename));
		}
		catch (Exception exception)
		{
			System.err.println("could not load document " + exception);
		}
	}
	
	/*-------------------------- DOM and XPath Methods -------------------------*/
	/**
	 Validate the document given a schema file
	 @param filename XSD file to read
	 */
	private static Boolean validateDocument(String filename)
	{
		try
		{
			String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			SchemaFactory factory = SchemaFactory.newInstance(language);
			schema = factory.newSchema(new File(filename));
			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(document));
			return true;
		}
		catch (SAXParseException e)
		{
			System.err.println(e.getLocalizedMessage()); //Prints error message
			return false;
		}
		catch (SAXException | IOException e)
		{
			System.out.println("Could not load schema.");
			return false;
		}
	}
	
	/**
	 Print nodes using DOM methods and XPath queries.
	 */
	private static void printNodes() throws XPathExpressionException
	{
		NodeList menu = document.getElementsByTagName("*");
		
		for (int i = 1; i < Math.floorDiv(menu.getLength(), 4) + 1; i++)
		{
			String name = path.evaluate("/menu/item[" + i + "]/name", document);
			String price = path.evaluate("/menu/item[" + i + "]/price", document);
			String description = path.evaluate("/menu/item[" + i + "]/description", document);
			
			System.out.print(name);
			for (int j = 0; j < 15 - name.length(); j++) { System.out.print(" "); }
			
			System.out.print("£" + price);
			for (int j = 0; j < 6 - price.length(); j++) { System.out.print(" "); }
			
			System.out.println(description);
		}
	}
	
	/**
	 Get result of XPath query.
	 @param query XPath query
	 @return result of query
	 */
	private static String query(String query)
	{
		String result = "";
		try
		{
			result = path.evaluate(query, document);
		}
		catch (Exception exception)
		{
			System.err.println("could not perform query - " + exception);
		}
		
		return(result);
	}
}
