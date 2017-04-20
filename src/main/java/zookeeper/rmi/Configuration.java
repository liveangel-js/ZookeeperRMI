package zookeeper.rmi;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;

public class Configuration extends XMLConfiguration {
    private static final long serialVersionUID = 47493058956938485L;
    private static Configuration conf;
    private static final String ZOOKEEPER_RMI_XML = "zookeeper-rmi.xml";
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
    private Configuration(){
        ConfVars[] vars = ConfVars.values();
        for (ConfVars v : vars) {
            if (v.getType() == ConfVars.VarType.BOOLEAN) {
                this.setProperty(v.getVarName(), v.getBooleanValue());
            } else if (v.getType() == ConfVars.VarType.LONG) {
                this.setProperty(v.getVarName(), v.getLongValue());
            } else if (v.getType() == ConfVars.VarType.INT) {
                this.setProperty(v.getVarName(), v.getIntValue());
            } else if (v.getType() == ConfVars.VarType.FLOAT) {
                this.setProperty(v.getVarName(), v.getFloatValue());
            } else if (v.getType() == ConfVars.VarType.STRING) {
                this.setProperty(v.getVarName(), v.getStringValue());
            } else {
                throw new RuntimeException("Unsupported VarType");
            }
        }
    }
    private Configuration(URL url) throws ConfigurationException {
        setDelimiterParsingDisabled(true);
        load(url);
    }

    /**
     * Load from resource.
     * url = Configuration.class.getResource(ZOOKEEPER_RMI_XML);
     * @throws ConfigurationException
     */
    public static synchronized Configuration getInstance() {
        if (conf != null) {
            return conf;
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url;

        url = Configuration.class.getResource(ZOOKEEPER_RMI_XML);
        if (url == null) {
            ClassLoader cl = Configuration.class.getClassLoader();
            if (cl != null) {
                url = cl.getResource(ZOOKEEPER_RMI_XML);
            }
        }
        if (url == null) {
            url = classLoader.getResource(ZOOKEEPER_RMI_XML);
        }

        if (url == null) {
            LOG.warn("Failed to load configuration, proceeding with a default");
            conf = new Configuration();
        } else {
            try {
                LOG.info("Load configuration from " + url);
                conf = new Configuration(url);
            } catch (ConfigurationException e) {
                LOG.warn("Failed to load configuration from " + url + " proceeding with a default", e);
                conf = new Configuration();
            }
        }

        LOG.info("Zookeeper address : " + conf.getZKConnectionString());

        return conf;
    }

    public String getZKConnectionString() {
        return String.format("%s:%d", getString(ConfVars.ZK_HOST), getInt(ConfVars.ZK_PORT));
    }
    public String getProviderDir(){
        return String.format("/%s/%s/%s/%s", getString(ConfVars.ZK_ROOTNODE), getString(ConfVars.ZK_SERVICE_PREFIX), getString(ConfVars.ZK_SERVICE_NAME), getString(ConfVars.ZK_SERVICE_PROVIDERDIR));
    }
    public String getProviderPath() {
        return String.format("/%s/%s/%s/%s/%s", getString(ConfVars.ZK_ROOTNODE), getString(ConfVars.ZK_SERVICE_PREFIX), getString(ConfVars.ZK_SERVICE_NAME), getString(ConfVars.ZK_SERVICE_PROVIDERDIR), getString(ConfVars.ZK_SERVICE_PROVIDER));
    }

    public int getZKSessionTimeout(){
        return getInt(ConfVars.ZK_SESSION_TIMEOUT);
    }
    public String getZKRootNode(){
        return getString(ConfVars.ZK_ROOTNODE);
    }

    public String getZKServicePREFIX(){
        return getString(ConfVars.ZK_SERVICE_PREFIX);
    }
    public String getZKServiceName(){
        return getString(ConfVars.ZK_SERVICE_NAME);
    }
    public String getZKServiceProviderDir(){
        return getString(ConfVars.ZK_SERVICE_PROVIDERDIR);
    }
    public String getZKServiceProviderPath(){
        return getString(ConfVars.ZK_SERVICE_PROVIDER);
    }
    public String getZKServiceConsumer(){
        return getString(ConfVars.ZK_SERVICE_CONSUMER);
    }

    public String getString(ConfVars c) {

        return getString(c.name(), c.getVarName(), c.getStringValue());
    }

    public String getString(String envName, String propertyName, String defaultValue) {
        if (System.getenv(envName) != null) {
            return System.getenv(envName);
        }
        if (System.getProperty(propertyName) != null) {
            return System.getProperty(propertyName);
        }

        return getStringValue(propertyName, defaultValue);
    }

    public int getInt(ConfVars c) {
        return getInt(c.name(), c.getVarName(), c.getIntValue());
    }

    public int getInt(String envName, String propertyName, int defaultValue) {
        if (System.getenv(envName) != null) {
            return Integer.parseInt(System.getenv(envName));
        }

        if (System.getProperty(propertyName) != null) {
            return Integer.parseInt(System.getProperty(propertyName));
        }
        return getIntValue(propertyName, defaultValue);
    }

    public long getLong(ConfVars c) {
        return getLong(c.name(), c.getVarName(), c.getLongValue());
    }

    public long getLong(String envName, String propertyName, long defaultValue) {
        if (System.getenv(envName) != null) {
            return Long.parseLong(System.getenv(envName));
        }

        if (System.getProperty(propertyName) != null) {
            return Long.parseLong(System.getProperty(propertyName));
        }
        return getLongValue(propertyName, defaultValue);
    }

    public float getFloat(ConfVars c) {
        return getFloat(c.name(), c.getVarName(), c.getFloatValue());
    }

    public float getFloat(String envName, String propertyName, float defaultValue) {
        if (System.getenv(envName) != null) {
            return Float.parseFloat(System.getenv(envName));
        }
        if (System.getProperty(propertyName) != null) {
            return Float.parseFloat(System.getProperty(propertyName));
        }
        return getFloatValue(propertyName, defaultValue);
    }

    public boolean getBoolean(ConfVars c) {
        return getBoolean(c.name(), c.getVarName(), c.getBooleanValue());
    }

    public boolean getBoolean(String envName, String propertyName, boolean defaultValue) {
        if (System.getenv(envName) != null) {
            return Boolean.parseBoolean(System.getenv(envName));
        }

        if (System.getProperty(propertyName) != null) {
            return Boolean.parseBoolean(System.getProperty(propertyName));
        }
        return getBooleanValue(propertyName, defaultValue);
    }

    private String getStringValue(String name, String d) {
        List<ConfigurationNode> properties = getRootNode().getChildren();
        if (properties == null || properties.isEmpty()) {
            return d;
        }
        for (ConfigurationNode p : properties) {
            if (p.getChildren("name") != null && !p.getChildren("name").isEmpty()
                    && name.equals(p.getChildren("name").get(0).getValue())) {
                return (String) p.getChildren("value").get(0).getValue();
            }
        }
        return d;
    }

    private int getIntValue(String name, int d) {
        List<ConfigurationNode> properties = getRootNode().getChildren();
        if (properties == null || properties.isEmpty()) {
            return d;
        }
        for (ConfigurationNode p : properties) {
            if (p.getChildren("name") != null && !p.getChildren("name").isEmpty()
                    && name.equals(p.getChildren("name").get(0).getValue())) {
                return Integer.parseInt((String) p.getChildren("value").get(0).getValue());
            }
        }
        return d;
    }

    private long getLongValue(String name, long d) {
        List<ConfigurationNode> properties = getRootNode().getChildren();
        if (properties == null || properties.isEmpty()) {
            return d;
        }
        for (ConfigurationNode p : properties) {
            if (p.getChildren("name") != null && !p.getChildren("name").isEmpty()
                    && name.equals(p.getChildren("name").get(0).getValue())) {
                return Long.parseLong((String) p.getChildren("value").get(0).getValue());
            }
        }
        return d;
    }

    private float getFloatValue(String name, float d) {
        List<ConfigurationNode> properties = getRootNode().getChildren();
        if (properties == null || properties.isEmpty()) {
            return d;
        }
        for (ConfigurationNode p : properties) {
            if (p.getChildren("name") != null && !p.getChildren("name").isEmpty()
                    && name.equals(p.getChildren("name").get(0).getValue())) {
                return Float.parseFloat((String) p.getChildren("value").get(0).getValue());
            }
        }
        return d;
    }

    private boolean getBooleanValue(String name, boolean d) {
        List<ConfigurationNode> properties = getRootNode().getChildren();
        if (properties == null || properties.isEmpty()) {
            return d;
        }
        for (ConfigurationNode p : properties) {
            if (p.getChildren("name") != null && !p.getChildren("name").isEmpty()
                    && name.equals(p.getChildren("name").get(0).getValue())) {
                return Boolean.parseBoolean((String) p.getChildren("value").get(0).getValue());
            }
        }
        return d;
    }


    public static enum ConfVars {
        ZK_HOST("zookeeper.host", ""),
        ZK_PORT("zookeeper.port", 2199),
        ZK_SESSION_TIMEOUT("zookeeper.session.timeout", 5000),
        ZK_ROOTNODE("zookeeper.rootnode", "project"),
        ZK_SERVICE_PREFIX("zookeeper.service.prefix", "service_type"),
        ZK_SERVICE_NAME("zookeeper.service.name", "service_name"),
        ZK_SERVICE_PROVIDERDIR("zookeeper.service.providers", "providers"),
        ZK_SERVICE_PROVIDER("zookeeper.service.provider", "provider"),
        ZK_SERVICE_CONSUMER("zookeeper.service.consumer", "consumer");
        private String varName;
        @SuppressWarnings("rawtypes")
        private Class varClass;
        private String stringValue;
        private VarType type;
        private int intValue;
        private float floatValue;
        private boolean booleanValue;
        private long longValue;


        ConfVars(String varName, String varValue) {
            this.varName = varName;
            this.varClass = String.class;
            this.stringValue = varValue;
            this.intValue = -1;
            this.floatValue = -1;
            this.longValue = -1;
            this.booleanValue = false;
            this.type = VarType.STRING;
        }

        ConfVars(String varName, int intValue) {
            this.varName = varName;
            this.varClass = Integer.class;
            this.stringValue = null;
            this.intValue = intValue;
            this.floatValue = -1;
            this.longValue = -1;
            this.booleanValue = false;
            this.type = VarType.INT;
        }

        ConfVars(String varName, long longValue) {
            this.varName = varName;
            this.varClass = Integer.class;
            this.stringValue = null;
            this.intValue = -1;
            this.floatValue = -1;
            this.longValue = longValue;
            this.booleanValue = false;
            this.type = VarType.LONG;
        }

        ConfVars(String varName, float floatValue) {
            this.varName = varName;
            this.varClass = Float.class;
            this.stringValue = null;
            this.intValue = -1;
            this.longValue = -1;
            this.floatValue = floatValue;
            this.booleanValue = false;
            this.type = VarType.FLOAT;
        }

        ConfVars(String varName, boolean booleanValue) {
            this.varName = varName;
            this.varClass = Boolean.class;
            this.stringValue = null;
            this.intValue = -1;
            this.longValue = -1;
            this.floatValue = -1;
            this.booleanValue = booleanValue;
            this.type = VarType.BOOLEAN;
        }

        public String getVarName() {
            return varName;
        }

        @SuppressWarnings("rawtypes")
        public Class getVarClass() {
            return varClass;
        }

        public int getIntValue() {
            return intValue;
        }

        public long getLongValue() {
            return longValue;
        }

        public float getFloatValue() {
            return floatValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        public boolean getBooleanValue() {
            return booleanValue;
        }

        public VarType getType() {
            return type;
        }

        enum VarType {
            STRING {
                @Override
                void checkType(String value) throws Exception {
                }
            },
            INT {
                @Override
                void checkType(String value) throws Exception {
                    Integer.valueOf(value);
                }
            },
            LONG {
                @Override
                void checkType(String value) throws Exception {
                    Long.valueOf(value);
                }
            },
            FLOAT {
                @Override
                void checkType(String value) throws Exception {
                    Float.valueOf(value);
                }
            },
            BOOLEAN {
                @Override
                void checkType(String value) throws Exception {
                    Boolean.valueOf(value);
                }
            };

            boolean isType(String value) {
                try {
                    checkType(value);
                } catch (Exception e) {
                    LOG.error("Exception in Configuration while isType", e);
                    return false;
                }
                return true;
            }

            String typeString() {
                return name().toUpperCase();
            }

            abstract void checkType(String value) throws Exception;
        }


    }
}