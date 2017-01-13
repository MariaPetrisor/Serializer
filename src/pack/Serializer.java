package pack;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;




public class Serializer {

   
    public static String serialize(Object object) {
        return serialize(object, new IdentityHashMap<Object, Object>(), 0);
    }

  
    //Daca e de tip primitiv, ii returnam valoarea sub forma de String
    private static String serialize(Object object, IdentityHashMap<Object, Object> serializedObjectsMap, int tabCount) {
        
    	//Daca obiectul este primitiv, Number, String sau Boolean 
    	//atunci se returneaza valoarea acestuia sub forma de String
    	if (object == null ||
                object instanceof Number || object instanceof Character || object instanceof Boolean ||
                object.getClass().isPrimitive()) {
            return String.valueOf(object);
        }
    	
    	
        String serialString = "";
        
        //HashCode-ul obiectului
        int sysId = System.identityHashCode(object);
        
        //Daca obiectul este o secventa de caractere atunci va fi inconjurat de ""
        if (object instanceof CharSequence) {
            serialString += "\"" + object + "\"";
        }
        
        //In cazul in care obiectul exista deja in IdentityHashMap (adica a mai fost serializat anterior)
        //pe randul respectiv din fisier se va scrie doar sysID-ul corespunzator obiectului
        //Pentru obeicte cu aceeasi referinta
        else if (serializedObjectsMap.containsKey(object)) {
            serialString +=  "->sysId#" + sysId;
        }
        //Pentru obiecte care nu au fost deja serializate si nu sunt primitive
        else {
            serializedObjectsMap.put(object, object);
            
            //Se adauga tab-uri in functie de "adancimea" serializarii
            String tabNumber = "";
            for (int t = 0; t < tabCount; t++) {
                tabNumber += "\t";
            }
            //Obiectul este tablou
            if (object.getClass().isArray()) {
                
            	int length = Array.getLength(object);
            	
            	//Se construieste structura tablourilor
            	//Tablourile vor fi inconjurate de [] avand, de asemenea, lungimea notata pe prima linie
            	serialString += "[" + object.getClass().getName() + "[" + length + "]" + ":sysId#" + sysId;
                
                
            	//Se parcurge tabloul si fiecare element din tablou este serializat recursiv si adaugat in serialString
                for (int i = 0; i < length; i++) {
                    Object arrayObject = Array.get(object, i);
                    String str = serialize(arrayObject, serializedObjectsMap, tabCount + 1);
                    serialString += "\n\t" + tabNumber + "\"" + i + "\":" + str;
                }
                serialString += "]";
            }
            //Obiectul nu este tablou, ci un obiect normal
            else {
            	//TreeMap in care voi memora campurile obiectului astfel:
            	//Key: Numele campului
            	//Value: campul in sine
                TreeMap<String, Field> fieldMap = new TreeMap<String, Field>();  
                String prefix = "";
                
                //Se itereaza prin super-clase. La fiecare iteratie in adancime se adauga prefixul "super" la numele variabilei
                for (Class<?> clasa = object.getClass(); clasa != null && !clasa.equals(Object.class); clasa = clasa.getSuperclass()) 
                {
                    Field[] fields = clasa.getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        Field field = fields[i];
                        fieldMap.put(prefix + field.getName(), field);
                        
                    }
                    prefix += "super.";
                }
                
                //Se construieste structura obiectului serializat
                //Obiectele vor fi inconjurate de {}
                serialString += "{" + object.getClass().getName() + ":sysId#" + sysId;
                for (Entry<String, Field> entry : fieldMap.entrySet()) {
                    String name  = entry.getKey();
                    Field  field = entry.getValue();
                    String serializedObj;
                    try {
                    	//Se preia obiectul din camp (se pune accesibilitatea de true si apoi se reface la starea initiala)
                        boolean wasAccessible = field.isAccessible();
                        
                        field.setAccessible(true);
                        Object fieldObject = field.get(object);
                        field.setAccessible(wasAccessible);  
                        
                        //Se apeleaza recursiv serializarea pe obiectul de campul curent
                        serializedObj = serialize(fieldObject, serializedObjectsMap, tabCount + 1);
                    }
                    catch (Throwable e) {
                    	serializedObj = "!" + e.getClass().getName() + ":" + e.getMessage();
                    }
                    //Se afiseaza numele campului intre "" urmat de valoarea serializata
                    serialString += "\n\t" + tabNumber +"\"" + name + "\":" + serializedObj;
                }
                //In cazul in care filedMap este fol nu se va afisa nimic, altfel se vor pune \n si taburile
                serialString += (fieldMap.isEmpty() ? "" : "\n") + (fieldMap.isEmpty() ? "" : tabNumber) + "}";
            }
        }
        
        return serialString;
    }



	public static void main(String[] args) throws Exception {
	    
	    Line l = new Line();
	    Point p1 = new Point();
	    Point_v2 poi = new Point_v2();
	    
		try {
			PrintStream out_stream = new PrintStream(new FileOutputStream("out.txt"));
			out_stream.print(serialize(l));
		} catch (FileNotFoundException e) {
			System.out.println("Eroare la operatiile de intrare-iesire!");
			System.exit(1);
		}
		
		
		
	}
	
	
}