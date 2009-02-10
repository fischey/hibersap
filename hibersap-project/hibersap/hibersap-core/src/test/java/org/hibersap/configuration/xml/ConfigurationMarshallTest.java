package org.hibersap.configuration.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationMarshallTest
{

    private static final Log LOG = LogFactory.getLog( ConfigurationMarshallTest.class );

    private JAXBContext jaxbContext;

    @Before
    public void setup()
        throws JAXBException
    {
        jaxbContext = JAXBContext.newInstance( HibersapConfig.class, SessionFactoryConfig.class, Property.class );
    }

    @Test
    public void testParseOkConfiguration()
        throws Exception
    {
        final InputStream configurationAsStream = getClass().getResourceAsStream( "/xml-configurations/hibersapOK.xml" );
        assertNotNull( configurationAsStream );

        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        final Object unmarshalledObject = unmarshaller.unmarshal( configurationAsStream );
        final HibersapConfig hiberSapMetaData = (HibersapConfig) unmarshalledObject;

        final List<SessionFactoryConfig> sessionFactories = hiberSapMetaData.getSessionFactories();
        assertNotNull( sessionFactories );
        assertEquals( 2, sessionFactories.size() );

        assertEquals( "A12", sessionFactories.get( 0 ).getName() );
        assertEquals( "B34", sessionFactories.get( 1 ).getName() );
    }

    @Test
    public void testMarshalling()
        throws Exception
    {
        final Set<Property> properties = new HashSet<Property>();
        final Property jcoProperty = new Property( "name", "value" );
        properties.add( jcoProperty );
        final SessionFactoryConfig sessionFactoryMetaData = new SessionFactoryConfig( "session-name", "ContextClass",
                                                                                      properties );

        final Set<String> classes = new HashSet<String>();
        classes.add( "package.Class1" );
        classes.add( "package.Class2" );
        sessionFactoryMetaData.setClasses( classes );

        final HibersapConfig hiberSapMetaData = new HibersapConfig( sessionFactoryMetaData );

        final Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty( "jaxb.formatted.output", Boolean.TRUE );

        final StringWriter stringWriter = new StringWriter();
        marshaller.marshal( hiberSapMetaData, stringWriter );
        LOG.info( stringWriter.toString() );
    }
}