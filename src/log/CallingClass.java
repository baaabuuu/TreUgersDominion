package log;

public class CallingClass extends SecurityManager {
    public static final CallingClass INSTANCE = new CallingClass();

    @SuppressWarnings("rawtypes")
	public Class[] getCallingClasses() {
        return getClassContext();
    }
}