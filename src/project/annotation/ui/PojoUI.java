package project.annotation.ui;


	

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;
import java.util.Map;

import project.pojo.Bpojo;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PojoUI  {
	static class PojoUIComparator implements Comparator<String> {
		
		private Map<String,PojoUI> me;

		

		public PojoUIComparator(Map<String, PojoUI> me) {
			super();
			this.me = me;
		}

		public Map<String,PojoUI>  getMe() {
			return me;
		}

		public void setMe(Map<String,PojoUI>  me) {
			this.me = me;
		}

		public PojoUIComparator() {
			super();
			
			// TODO Auto-generated constructor stub
		}

	
		public int compare(String o1, String o2) {
			PojoUI obj1 = (PojoUI) me.get(o1);
			PojoUI obj2 = (PojoUI) me.get(o2);
			int i1 = obj1.PojoUIOrder();
			int i2 = obj2.PojoUIOrder();
			return i1-i2;
		}

		
	}
	
    String PojoUIName();
    int PojoUIOrder();
    String PojoEditor() default"TextView";
    boolean CanBeNull() default true;
    Class<? extends Bpojo> SpinnerClass() default Bpojo.class;
    String SubSpinner() default "";
    String SubField() default"";
    Class<? extends Comparator<String>>Comaprator() default PojoUIComparator.class;
	String System()default"";
	int Span()default 1;
	boolean SingleRow() default false;
    
}


