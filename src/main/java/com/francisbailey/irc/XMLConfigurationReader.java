package com.francisbailey.irc;


import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.File;


/**
 * Created by fbailey on 15/12/16.
 */
public class XMLConfigurationReader implements ConfigurationReader{


    private XMLConfiguration config;

    public XMLConfigurationReader(File xmlFile) throws ConfigurationException {

        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<XMLConfiguration> builder;
        builder = new FileBasedConfigurationBuilder<>(XMLConfiguration.class);
        builder.configure(params.xml().setFile(xmlFile));

        this.config = builder.getConfiguration();
    }


    public HierarchicalConfiguration getConfiguration() {

        return this.config;
    }


}
