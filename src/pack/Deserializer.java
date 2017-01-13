package pack;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Deserializer
{
	public static TreeMap<String, Object> ObjectMap;
	public static Object deserialize(BufferedReader file_char_stream, String line){
		String newLine;
		String pattern5 = "sysId#(.*)";      //sysId#.. 
		String pattern4 = "\\[L(.*);";       //[L..;
		String pattern3 = "\\[[^\\]]\\]";    //[..]
		String pattern2 = ":(.*)";           //:..
		String pattern1 = "\"([^\"]*)\"";    //".."
		String pattern = "\\{(.*):";         //{..;
		
	    Pattern r = Pattern.compile(pattern);
	    Pattern r1 = Pattern.compile(pattern1);
	    Pattern r2 = Pattern.compile(pattern2);
	    Pattern r3 = Pattern.compile(pattern3);
	    Pattern r4 = Pattern.compile(pattern4);
	    Pattern r5 = Pattern.compile(pattern5);
	    String fieldName="", fieldValue="";
	    Number num=0;
	   
	    Field field;
	    
	    String sysId = "";
	    Matcher m5;
	    int i;
	    
	      
		try {
				//In cazul in care am primit o linie ca parametru incepem de la aceasta, altfel incepem de la urmatoarea
				if(line==null){
					newLine = file_char_stream.readLine();
				}
				else{
					newLine = line;
				}
				
				
				//Se cauta sysId-ul pentru obiectul curent
				 m5 = r5.matcher(newLine);
   			  	 if (m5.find( )) {
   			  		sysId = m5.group(0);
   				  	sysId = sysId.replace("sysId#", "");
   				  	sysId = sysId.replace("]", "");
   				  	
   			  
   				//In cazul in care in ObjectMap avem deja o inregistrare cu sysId-ul curent, atunci returnam direct obiectul 
   				//cu sysId-ul corespunzator din ObjectMap
   			  	 if(ObjectMap.containsKey(sysId)){
   			  		 return ObjectMap.get(sysId);
   			  	 }
   			  	 }
   			  	 
   			  	 //Se cauta numele obiectului. Acesta se afla intotdeauna intre {..:
   			     Matcher m = r.matcher(newLine);
				 if (m.find()) {
					
			    	  String objectName = m.group(0);
			    	  objectName = objectName.replace("{", "");
			    	  objectName = objectName.replace(":", "");
			    	  
			    	  
			    	  //Cream o instanta a obiectului detectat
			    	  Class<?> c = Class.forName(objectName) ;
			  	      Object o = c.newInstance() ;
			  	      
			  	      //Parcurgem fisierul pana cand intalnim }, ceea ce inseamna ca s-a terminat obiectul
			    	  while((newLine = file_char_stream.readLine())!=null && !newLine.contains("}")){    
			    		  
			    		  	  //Cautam numele campului, care este intotdeauna incadrat in ""
			    			  Matcher m1 = r1.matcher(newLine);
			    			  if (m1.find( )) {
			    				  fieldName = m1.group(0);
			    				  fieldName = fieldName.replace("\"", "");
			    				  
			    			  }
			    			  //Daca nu este un obiect mostenit atunci apelam direct getDeclaredField pentru a obtine obiectul
			    			  if(!fieldName.contains("super")){
	    						  
			    				  field = o.getClass().getDeclaredField(fieldName);
			    				  
			    			  }
			    			  //In cazul campurilor mostenite se numara de cate ori apare . si astfel stim de cate ori trebuie 
			    			  //apelata metoda getSuperclass pentru a ajunge la superclasa de la care am mostenit campul
			    			  else{
			    				  int count = fieldName.length() - fieldName.replace(".", "").length();
			    				  
			    				  
			    				  fieldName = fieldName.replace("super.", "");
			    				  Class<?> clasa = o.getClass();
			    				  
			    				  for (i=0;i<count;i++) {
			    					  clasa = clasa.getSuperclass();
			    				  }
			    				  
			    				  //Am ajuns in clasa de la care am mostenit campul si obtinem obiectul prin getDeclaredField
			    				  field = clasa.getDeclaredField(fieldName);
			    			  }
			    			  
			    			  //Daca obiectul are aceeasi referinta ca si un obiect anterior
			    			  if(newLine.contains("->")){
			    				  //Obtinem sysId-ul obiectului
			    				  m5 = r5.matcher(newLine);
				    			  if (m5.find( )) {
				    				  sysId = m5.group(0);
				    				  sysId = sysId.replace("sysId#", "");
				    				  //System.out.println(sysId + "=");
				    			  }
				    			  boolean wasAccessible = field.isAccessible();
				    			 
				    			  field.setAccessible(true);
				    			  
				    			  //Ii atribuim obiectului curent din camp valoarea gasita in ObjectMap corespunzatoare sysId-ului
				    			  field.set(o, ObjectMap.get(sysId));
				    			  
				    			  //Refacem accesibilitatea campului
				    			  field.setAccessible(wasAccessible); 
			    			  }
			    			  
			    			  //In cazul in care este obiect nou (obiectele au pe prima linie { deschisa)
			    			  else if(newLine.contains("{")){
			    				  
			    				  //Obtinem sysId-ul obiectului
			    				  m5 = r5.matcher(newLine);
				    			  if (m5.find( )) {
				    				  sysId = m5.group(0);
				    				  sysId = sysId.replace("sysId#", "");
				    				 
				    			  }
				    			  boolean wasAccessible = field.isAccessible();
					    			 
			    				  field.setAccessible(true);
			    				  
			    				  //Obtinem obiectul prin recursivitate
			    				  Object tmp = deserialize(file_char_stream, newLine);
			    				  
			    				  field.set(o, tmp);
			    				  
			    				  field.setAccessible(wasAccessible); 
			    				  	
			    				  
			    			}
			    			  //Campul este tablou de obiecte (nu am considerat cazul in care este tablou de primitive)
			    			  else if(newLine.contains("[L")){
			    				  String className2;
			    				  //Obtinem numele tabloului, acesta se afla intre [L..;
			    				  Matcher m4 = r4.matcher(newLine);
			    				  if(m4.find()){
				    				  className2 = m4.group(0);
				    				  className2 = className2.replace("[L", "");
				    				  className2 = className2.replace(";", "");
			    				  
			    				  //Obtinem sysId-ul
			    				  m5 = r5.matcher(newLine);
				    			  if (m5.find( )) {
				    				  sysId = m5.group(0);
				    				  sysId = sysId.replace("sysId#", "");
				    				  
				    			  }
			    				  
				    			  
			    				  Class<?> c1 = Class.forName(className2);
			    				  
			    				  //Obtinem lungimea tabloului, care se afla intre []
			    				  Matcher m3 = r3.matcher(newLine);
			    				  if (m3.find( )) {
			    					  String len = m3.group(0);
			    					  len = len.replace("[", "");
				    				  len = len.replace("]", "");
				    				  
				    				  //Se creeaza o instanta a tabloului de tip c1 cu lungimea length
			    					  int length = Integer.parseInt(len);
			    					  Object vector = Array.newInstance(c1,length) ;
			    					  
			    					  //Se creeaza N=length obiecte de tipul c1 si se apeleaza recursiv deserializarea pe fiecare
			    					  for(i=0;i<length;i++){
			    						  Object obj = c1.newInstance();
			    						  obj = deserialize(file_char_stream, file_char_stream.readLine());
			    						  
			    						 
			    						  //Obiectul returnat prin deserializarea recursiva va fi setat in campul cu indexul i din tabloul "vector"
			    						  Array.set(vector, i, obj);
			    					  }
			    					  
			    					  Object[] oo = (Object[])vector;
			    					  
			    					  boolean wasAccessible = field.isAccessible();
			    					  
			    					  field.setAccessible(true);
				    				  field.set(o, oo);
				    				  field.setAccessible(wasAccessible); 
				    				  
				    				  ObjectMap.put(sysId, oo);
				    				  	
			    				  }
			    			   }
				    	   }
			    			
			    			  //Campul este de tip primitiv
			    			  else{
			    			  //Obtinem valoarea campului primitiv, care se afla dupa :..  
			    			  Matcher m2 = r2.matcher(newLine);
			    			  if (m2.find( )) {
			    				  fieldValue = m2.group(0);
			    				  
			    				  fieldValue = fieldValue.replace(":", "");
			    				  
			    				  //Campul este de tip Number
			    				  if(!fieldValue.contains("\"")){
			    					  
			    					  String type = field.getType().getName();
			    					  if(type.contains("int") || type.contains("Integer"))
			    						  num = Integer.parseInt(fieldValue);
			    					  else if(type.contains("long") || type.contains("Long"))
			    						  num = Long.parseLong(fieldValue);
			    					  else if(type.contains("double") || type.contains("Double"))
			    						  num = Double.parseDouble(fieldValue);
			    					  else if(type.contains("short") || type.contains("Short"))
			    						  num = Short.parseShort(fieldValue);
			    					  else if(type.contains("float") || type.contains("Float"))
			    						  num = Float.parseFloat(fieldValue);
			    					  else if(type.contains("byte") || type.contains("Byte"))
			    						  num = Byte.parseByte(fieldValue);
			    					  
			    					  boolean wasAccessible = field.isAccessible();
			    					  field.setAccessible(true);
			    					  field.set(o, num);
			    					  field.setAccessible(wasAccessible);
			    				  }
			    				  //Campul este de tip String
			    				  else{
			    				  
			    				  fieldValue = fieldValue.replace("\"", "");
			    				  
			    				  boolean wasAccessible = field.isAccessible();
			    				  field.setAccessible(true);
			    				  field.set(o, fieldValue);
			    				  field.setAccessible(wasAccessible);
			    				  }
			    			  }			    	
			    			  }
			    	  }
				
			    	  //Daca gasim }, adica descrierea obiectului a luat sfarsit, atunci adaugam sysId-ul si obiectul in dictionar
			    	  //si returnam obiectul nou creat
			    	  if(newLine.contains("}")){
			    		  
			    		  ObjectMap.put(sysId, o);
			    		  return o;
			    	  }
			    	  }
				
				
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Eroare la operatiile I/O");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Nu s-a gasit clasa cu numele acesta");
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.out.println("Clasa nu poate fi instantiata");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.println("Nu aveti acces la acest camp!");
		}catch (NoSuchFieldException e) {
			System.out.println("Nu s-a gasit camp cu numele acesta");
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
    public static void main( String args[] ){
      
      try {
		BufferedReader file_char_stream =
				  new BufferedReader(new InputStreamReader(
				  new FileInputStream("out.txt")));
		ObjectMap = new TreeMap<String, Object>(); 
		System.out.println(deserialize(file_char_stream, null));
	
	} catch (FileNotFoundException e) {
		System.out.println("Eroare la operatiile de intrare-iesire!");
		System.exit(1);
	} catch (IOException e) {
		e.printStackTrace();
		System.out.println("Eroare la operatiile I/O");
	}
}
}