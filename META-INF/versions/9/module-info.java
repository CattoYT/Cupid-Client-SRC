module org.apache.logging.log4j {
  requires java.base;
  
  exports org.apache.logging.log4j;
  exports org.apache.logging.log4j.message;
  exports org.apache.logging.log4j.simple;
  exports org.apache.logging.log4j.spi;
  exports org.apache.logging.log4j.status;
  exports org.apache.logging.log4j.util;
  
  uses org.apache.logging.log4j.spi.Provider;
  uses org.apache.logging.log4j.util.PropertySource;
  uses org.apache.logging.log4j.message.ThreadDumpMessage$ThreadInfoFactory;
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar!\META-INF\versions\9\module-info.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */