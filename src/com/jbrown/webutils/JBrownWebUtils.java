package com.jbrown.webutils;

/* Various useful methods used for querying, parsing, and
 downloading images from Web sites.

 The methods fall into 3 groups:
 * methods for querying the web
 * DOM methods for parsing and printing
 * methods for downloading and saving a PNG image

 */

import java.net.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class JBrownWebUtils {
	/**
	 * Contact the specified URL, and return the response as a string
	 * 
	 * @param urlStringing
	 * @return
	 */
	public static String webGetString(String urlString) {
		System.out.println("Contacting \"" + urlString + "\"");
		try {
			URL url = new URL(urlString);

			String line;
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			return sb.toString();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		return null;
	}

	/**
	 * contact the specified URL, and return the response stream
	 * 
	 * @param urlString
	 * @return
	 */
	public static InputStream webGet(String urlString) {
		System.out.println("Contacting \"" + urlString + "\"");
		try {
			URL url = new URL(urlString);
			return url.openStream();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		return null;
	}

	/**
	 * Save the string str into the file
	 * 
	 * @param fileName
	 * @param str
	 */
	public static void saveString(String fileName, String str) {
		try {
			FileWriter fw = new FileWriter(fileName);
			fw.write(str + "\n");
			fw.close();
			System.out.println("Saved to " + fileName);
		} catch (IOException e) {
			System.out.println("Could not write to " + fileName);
		}
	}

	/**
	 * Parse the XML file and create a Document
	 * 
	 * @param fileName
	 * @return
	 */
	public static Document parse(String fileName) {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true); // inportant

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new File(fileName));
			return document;
		} catch (SAXParseException spe) { // Error generated by the parser
			System.out.println("\n** Parsing error , line "
					+ spe.getLineNumber() + ", uri " + spe.getSystemId());
			System.out.println(" " + spe.getMessage());
			// Use the contained exception, if any
			Exception x = spe;
			if (spe.getException() != null) {
				x = spe.getException();
			}
			x.printStackTrace();
		} catch (SAXException sxe) { // Error generated during parsing
			Exception x = sxe;
			if (sxe.getException() != null) {
				x = sxe.getException();
			}
			x.printStackTrace();
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}

	/**
	 * Parse the XML input stream and create a Document
	 * 
	 * @param is
	 * @return
	 */
	public static Document parse(InputStream is) {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(is);
			return document;
		} catch (SAXParseException spe) { // Error generated by the parser
			System.out.println("\n** Parsing error , line "
					+ spe.getLineNumber() + ", uri " + spe.getSystemId());
			System.out.println(" " + spe.getMessage());
			// Use the contained exception, if any
			Exception x = spe;
			if (spe.getException() != null) {
				x = spe.getException();
			}
			x.printStackTrace();
		} catch (SAXException sxe) { // Error generated during parsing
			Exception x = sxe;
			if (sxe.getException() != null) {
				x = sxe.getException();
			}
			x.printStackTrace();
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}

	/**
	 * pretty print XML document to the file
	 * 
	 * @param doc
	 * @param fileName
	 */
	public static void saveDoc(Document doc, String fileName) {
		try {
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();

			// Setup indenting to "pretty print"
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");

			// Write document to the file
			System.out.println("Saving document in " + fileName);
			xformer.transform(new DOMSource(doc), new StreamResult(new File(
					fileName)));
		} catch (TransformerConfigurationException e) {
			System.out.println("TransformerConfigurationException: " + e);
		} catch (TransformerException e) {
			System.out.println("TransformerException: " + e);
		}
	}

	/**
	 * pretty print XML document to stdout
	 * 
	 * @param doc
	 */
	public static void listDoc(Document doc) // pretty print XML document to
												// stdout
	{
		try {
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();

			// Setup indenting to "pretty print"
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");

			// Write document to stdout
			System.out
					.println("------------------------------------------------");
			xformer.transform(new DOMSource(doc), new StreamResult(System.out));
			System.out
					.println("------------------------------------------------");

		} catch (TransformerConfigurationException e) {
			System.out.println("TransformerConfigurationException: " + e);
		} catch (TransformerException e) {
			System.out.println("TransformerException: " + e);
		}
	} // end of listDoc() for stdout

	/**
	 * Return the text of the first element with the name 'tagName' below
	 * element
	 * 
	 * @param elem
	 * @param tagName
	 * @return
	 */
	public static String getElemText(Element elem, String tagName) {
		NodeList nodeList = elem.getElementsByTagName(tagName);
		if (nodeList.getLength() > 0) {
			return nodeList.item(0).getFirstChild().getNodeValue();
		}
		return null;
	} // end of getElemText()

	/**
	 * Download the image at urlString, and save it as a PNG file
	 * 
	 * @param urlString
	 */
	public static void downloadImage(String urlString) {
		System.out.println("Downloading image at:\n\t" + urlString);
		URL url = null;
		BufferedImage image = null;
		try {
			url = new URL(urlString);
			image = ImageIO.read(url);
		} catch (IOException e) {
			System.out.println("Problem downloading");
		}

		if (image != null) {
			String fileName = urlString
					.substring(urlString.lastIndexOf("/") + 1);
			savePNG(fileName, image);
		}
	}

	private static void savePNG(String fileName, BufferedImage im) {
		int extPosn = fileName.lastIndexOf(".");
		String pngfileName;
		if (extPosn == -1) {
			pngfileName = "cover.png";
		} else {
			pngfileName = fileName.substring(0, extPosn) + "Cover.png";
		}
		System.out.println("Saving image to " + pngfileName);

		try {
			ImageIO.write(im, "png", new File(pngfileName));
		} catch (IOException e) {
			System.out.println("Could not save file");
		}
	} // end of savePNG()
}