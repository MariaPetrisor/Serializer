package pack;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class Test_Des {
	public static void main(String[] args) throws Exception {
	    
		int[] object = new int[2];
		System.out.println(object.getClass().getName());
		
		String className = "pack.Point";
		String fieldName1 = "x";
		String fieldName2 = "name";
		
		
		//Creare vector
	    Class c1 = Class.forName(className) ;
	    Object obj = Array.newInstance(c1,2) ;
	    Object o1 = c1.newInstance();
	    Object o2 = c1.newInstance();
	    Array.set(obj, 0, o1);
	    Array.set(obj, 1, o2);
	    Object[] oo = (Object[])obj;
	    int i;
	    for(i = 0;i<2;i++ )
	    	System.out.println(oo[i]);
	    
	    //Creare obiect obisnuit
	    
	    Class c = Class.forName(className) ;
	    Object o = c.newInstance() ;
	    Field field1 = o.getClass().getDeclaredField(fieldName1);
	    field1.setAccessible(true);
	    field1.set(o, 9);
	    
	    Field field2 = o.getClass().getField(fieldName2);
	    field2.setAccessible(true);
	    field2.set(o, "Andreea");
	    System.out.println(o);

	    
		
		
		
	}
}
