package META-INF.versions.9.org.apache.logging.log4j.util;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.util.PrivateSecurityManagerStackTraceUtil;

public class StackLocator {
  private static final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
  
  private static final StackWalker stackWalker = StackWalker.getInstance();
  
  private static final org.apache.logging.log4j.util.StackLocator INSTANCE = new org.apache.logging.log4j.util.StackLocator();
  
  public static org.apache.logging.log4j.util.StackLocator getInstance() {
    return INSTANCE;
  }
  
  public Class<?> getCallerClass(Class<?> sentinelClass, Predicate<Class<?>> callerPredicate) {
    if (sentinelClass == null)
      throw new IllegalArgumentException("sentinelClass cannot be null"); 
    if (callerPredicate == null)
      throw new IllegalArgumentException("callerPredicate cannot be null"); 
    return walker.<Class<?>>walk(s -> (Class)s.map(StackWalker.StackFrame::getDeclaringClass).dropWhile(()).dropWhile(()).findFirst().orElse(null));
  }
  
  public Class<?> getCallerClass(String fqcn) {
    return getCallerClass(fqcn, "");
  }
  
  public Class<?> getCallerClass(String fqcn, String pkg) {
    return ((Optional)walker.<Optional>walk(s -> s.dropWhile(()).dropWhile(()).dropWhile(()).findFirst()))
      
      .map(StackWalker.StackFrame::getDeclaringClass)
      .orElse(null);
  }
  
  public Class<?> getCallerClass(Class<?> anchor) {
    return ((Optional)walker.<Optional>walk(s -> s.dropWhile(()).dropWhile(()).findFirst()))
      
      .map(StackWalker.StackFrame::getDeclaringClass).orElse(null);
  }
  
  public Class<?> getCallerClass(int depth) {
    return ((Optional)walker.<Optional>walk(s -> s.skip(depth).findFirst())).map(StackWalker.StackFrame::getDeclaringClass).orElse(null);
  }
  
  public Stack<Class<?>> getCurrentStackTrace() {
    if (PrivateSecurityManagerStackTraceUtil.isEnabled())
      return PrivateSecurityManagerStackTraceUtil.getCurrentStackTrace(); 
    Stack<Class<?>> stack = new Stack<>();
    List<Class<?>> classes = walker.<List<Class<?>>>walk(s -> (List)s.map(()).collect(Collectors.toList()));
    stack.addAll(classes);
    return stack;
  }
  
  public StackTraceElement calcLocation(String fqcnOfLogger) {
    return ((Optional)stackWalker.<Optional>walk(s -> s.dropWhile(()).dropWhile(()).findFirst()))
      
      .map(StackWalker.StackFrame::toStackTraceElement).orElse(null);
  }
  
  public StackTraceElement getStackTraceElement(int depth) {
    return ((Optional)stackWalker.<Optional>walk(s -> s.skip(depth).findFirst()))
      .map(StackWalker.StackFrame::toStackTraceElement).orElse(null);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar!\META-INF\versions\9\org\apache\logging\log4\\util\StackLocator.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */