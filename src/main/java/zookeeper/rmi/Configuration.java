package zookeeper.rmi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {


    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    public static String getZKConnectionString() {
        return String.format("%s:%d", ConfVars.ZK_HOST.getStringValue(), ConfVars.ZK_PORT.getIntValue());
    }
    public static String getProviderDir(){
        return String.format("/%s/%s/%s/%s", ConfVars.ZK_ROOTNODE.getStringValue(), ConfVars.ZK_SERVICE_PREFIX.getStringValue(), ConfVars.ZK_SERVICE_NAME.getStringValue(), ConfVars.ZK_SERVICE_PROVIDERDIR.getStringValue());
    }
    public static String getProviderPath() {
        return String.format("/%s/%s/%s/%s/%s", ConfVars.ZK_ROOTNODE.getStringValue(), ConfVars.ZK_SERVICE_PREFIX.getStringValue(), ConfVars.ZK_SERVICE_NAME.getStringValue(), ConfVars.ZK_SERVICE_PROVIDERDIR.getStringValue(), ConfVars.ZK_SERVICE_PROVIDER.getStringValue());
    }

    public static enum ConfVars {
        ZK_HOST("zookeeper.host", "sjnitapp16.sjn.its.paypalcorp.com"),
        ZK_PORT("zookeeper.port", 43210),
        ZK_SESSION_TIMEOUT("zookeeper.session.timeout", 5000),
        ZK_ROOTNODE("zookeeper.rootnode", "timetunnel"),
        ZK_SERVICE_PREFIX("zookeeper.service.prefix", "services"),
        ZK_SERVICE_NAME("zookeeper.service.name", "metaservice"),
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
                    LOG.error("Exception in ZeppelinConfiguration while isType", e);
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