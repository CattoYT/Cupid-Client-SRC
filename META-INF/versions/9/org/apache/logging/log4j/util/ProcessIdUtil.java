package META-INF.versions.9.org.apache.logging.log4j.util;

public class ProcessIdUtil {
  public static final String DEFAULT_PROCESSID = "-";
  
  public static String getProcessId() {
    try {
      return Long.toString(ProcessHandle.current().pid());
    } catch (Exception ex) {
      return "-";
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar!\META-INF\versions\9\org\apache\logging\log4\\util\ProcessIdUtil.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */