package osu.grading;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLTools {

    private XMLTools() {
    }

    public static Document getXMLDoc(Path p) throws IllegalArgumentException {
        Document document;
        try (InputStream is = new BufferedInputStream(new FileInputStream(p.toFile()))) {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            document = domBuilder.parse(is);

        } catch (IOException e) {
            throw new IllegalArgumentException("I/O Error reading .project file " + p,
                    e.getCause());
        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException("XML Error reading .project file " + p,
                    e.getCause());
        } catch (SAXException e) {
            System.err.println("XML SAX Error reading .project file " + p);
            throw new IllegalArgumentException("XML SAX Error reading .project file " + p,
                    e.getCause());
        }
        return document;
    }

    public static void writeXMLDoc(Document document, Path p)
            throws IllegalArgumentException {
        try (FileOutputStream os = new FileOutputStream(p.toFile())) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(os);
            transformer.transform(source, result);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(
                    "Error opening .project file to write " + p, e.getCause());
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "I/O exception writing to .project file " + p, e.getCause());
        } catch (TransformerException e) {
            throw new IllegalArgumentException("XML Error writing to .project file " + p,
                    e.getCause());

        }
    }

    /**
     * Updates an Eclipse .project file to change the name of the project to
     * start with a specified prefix.
     *
     * @param prefix
     *            the specified prefix
     * @param p
     *            path to .project file to be updated
     */
    public static void updateProjectFile(String prefix, Path p) {

        Document document = getXMLDoc(p);

        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            String expression = "/projectDescription/name";
            Node node = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
            String newName = prefix + "_" + node.getTextContent();
            node.setTextContent(newName);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(
                    "XML XPath Error reading .project file " + p, e.getCause());
        }

        writeXMLDoc(document, p);
    }

}
