/*
 * Copyright 1999-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */
 
package org.apache.fop;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.fop.apps.Fop;
import org.apache.fop.cli.InputHandler;

/**
 * Basic runtime test for the old Fop class. It is used to verify that 
 * nothing obvious is broken after compiling.
 */
public class BasicDriverTestCase extends AbstractFOPTestCase {

    /**
     * @see junit.framework.TestCase#TestCase(String)
     */
    public BasicDriverTestCase(String name) {
        super(name);
    }

    /**
     * Tests Fop with JAXP and OutputStream generating PDF.
     * @throws Exception if anything fails
     */
    public void testFO2PDFWithJAXP() throws Exception {
        File foFile = new File(getBaseDir(), "test/xml/bugtests/block.fo");
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        Fop fop = new Fop(Fop.RENDER_PDF);
        fop.setOutputStream(baout);
        
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(); //Identity transf.
        Source src = new StreamSource(foFile);
        Result res = new SAXResult(fop.getDefaultHandler());
        transformer.transform(src, res);
        
        assertTrue("Generated PDF has zero length", baout.size() > 0);
    }

    /**
     * Tests Fop with JAXP and OutputStream generating PostScript.
     * @throws Exception if anything fails
     */
    public void testFO2PSWithJAXP() throws Exception {
        File foFile = new File(getBaseDir(), "test/xml/bugtests/block.fo");
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        Fop fop = new Fop(Fop.RENDER_PS);
        fop.setOutputStream(baout);
        
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(); //Identity transf.
        Source src = new StreamSource(foFile);
        Result res = new SAXResult(fop.getDefaultHandler());
        transformer.transform(src, res);
        
        assertTrue("Generated PostScript has zero length", baout.size() > 0);
    }

    /**
     * Tests Fop with JAXP and OutputStream generating RTF.
     * @throws Exception if anything fails
     */
    public void testFO2RTFWithJAXP() throws Exception {
        File foFile = new File(getBaseDir(), "test/xml/bugtests/block.fo");
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        Fop fop = new Fop(Fop.RENDER_RTF);
        fop.setOutputStream(baout);
        
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(); //Identity transf.
        Source src = new StreamSource(foFile);
        Result res = new SAXResult(fop.getDefaultHandler());
        transformer.transform(src, res);
        
        assertTrue("Generated RTF has zero length", baout.size() > 0);
    }

    /**
     * Tests Fop with XsltInputHandler and OutputStream.
     * @throws Exception if anything fails
     */
    public void testFO2PDFWithXSLTInputHandler() throws Exception {
        File xmlFile = new File(getBaseDir(), "test/xml/1.xml");
        File xsltFile = new File(getBaseDir(), "test/xsl/doc.xsl");
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        Fop fop = new Fop(Fop.RENDER_PDF);
        fop.setOutputStream(baout);
        
        InputHandler handler = new InputHandler(xmlFile, xsltFile, null);
        handler.render(fop);
        
        assertTrue("Generated PDF has zero length", baout.size() > 0);
    }

}