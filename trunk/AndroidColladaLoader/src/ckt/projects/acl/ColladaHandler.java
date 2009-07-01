package ckt.projects.acl;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ColladaHandler extends DefaultHandler {
	private float[] vertices;
	private byte[] indices;
	
	private boolean inVertices = false;
	private boolean inTriangles = false;
	private boolean inP = false;
	
	public void startDocument() throws SAXException {
        super.startDocument();
    }
	
	public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        super.startElement(uri, localName, name, atts);
        if (localName.equalsIgnoreCase("float_array") && atts.getValue("id").contains("position")){
        	inVertices = true;
        } else if (localName.equalsIgnoreCase("triangles") && vertices!=null){
        	inTriangles = true;
        } else if (localName.equalsIgnoreCase("p") && inTriangles)
        	inP = true;
    }

    public void endElement(String uri, String localName, String name) throws SAXException {
        super.endElement(uri, localName, name);
        
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        String text = new String(ch, start, length);
        if (inVertices && vertices == null){
        	String[] temp = text.split(" ");
        	vertices = new float[temp.length];
        	for (int i=0; i< vertices.length; i++)
        		vertices[i] = Float.parseFloat(temp[i]);
        	
        	inVertices = false;
        } else if(inP && text.length()>1 && indices == null) {
        	String[] temp = text.split(" ");
        	indices = new byte[temp.length/2+1];
        	for (int i=0; i<temp.length; i+=2){
            	indices[i/2] = Byte.parseByte(temp[i]);
        	}
        		
        	inP = false;
        	inTriangles = false;
        }     
    }
 
    public ArrayList<ColladaObject> parseFile(InputStream input) {
        try {
            
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xr.setContentHandler(this);
            xr.parse(new InputSource(input));
           
        } catch (Exception e){
        	//fail
        }
        
        ArrayList<ColladaObject> result = new ArrayList<ColladaObject>();
        result.add(new ColladaObject(vertices, indices));

        return result;
    }

}
