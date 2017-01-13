package pack;
import java.io.*;

abstract class Shape{
	protected String name;
}

abstract class ShapeWithPerimeter extends Shape{
    public abstract double perimeter();
    
}

abstract class ShapeWithoutPerimeter extends Shape{
}

interface Length{
public double getLength();
}

interface Area{
public double getArea();
}

abstract class AbstractPoint {
	public String name;
}

class Point extends AbstractPoint{
private int x;
private int y;

public Point(){
this.x = 10;
this.y = 15;
name = "punct";
}

public int getX(){
return x;
}

public int getY(){
return y;
}



public String toString(){
	return "Punct cu numele = " + name + " coordonatele: x = " + x + ", y = " + y + "\n";
}
}

class Point_v2{
	Point pct = new Point();
	int a = 10;
	int b = 20;
	
	public String toString(){
		return "Point_v2: a=" + a + " b=" + b + " si Point care contine: \n" + pct;
		
	}
}

class Line extends ShapeWithoutPerimeter implements Length{
private Point[] p = new Point[2];
private Point p1 = new Point();
private double l;

public Line()
{
	p[0] = new Point();
    p[1] = p[0];
    l = Math.sqrt((p[0].getX()-p[1].getX())*(p[0].getX()-p[1].getX()) + (p[0].getY()-p[1].getY())*(p[0].getY()-p[1].getY()));
    name = "linie";
}

public double getLength(){
return l;
}

public String toString(){
    String s = "Linie cu coordonatele: ";
    int i;
    for( i = 0; i < 2; i++)
        s = s + "(" + p[i].getX() + "," + p[i].getY() + ") " +  "(punct cu numele " + p[i].name + ")";
    s = s + "si de dimensiune = " + l + " cu numele: " + name + "\n" + p1 + "\n";
    return s;
}
}



class Angle extends ShapeWithoutPerimeter implements Length{
private Point[] p = new Point[3];
private int unghi;
private double l;
public Angle(Point[] p, int unghi)
{
    this.p = p;
    this.unghi = unghi;
    l = Math.sqrt((p[0].getX()-p[1].getX())*(p[0].getX()-p[1].getX()) + (p[0].getY()-p[1].getY())*(p[0].getY()-p[1].getY()));

}

public double getLength(){
return l;
}

public String toString(){
    String s = "Unghi cu coordonatele: ";
    int i;
    for( i = 0; i < 3; i++)
        s = s + "(" + p[i].getX() + "," + p[i].getY() + ") ";
    s = s + ", cu latura de dimensiune = " + l + " si cu unghiul = " + unghi + "\n";
    return s;
}
}

class Square extends ShapeWithPerimeter implements Area{
private Point[] p = new Point[4];
private double l;

public Square(Point[] p)
{
    this.p = p;
    l = Math.sqrt((p[0].getX()-p[1].getX())*(p[0].getX()-p[1].getX()) + (p[0].getY()-p[1].getY())*(p[0].getY()-p[1].getY()));

}

public double perimeter(){
    return 4 * l;
}

public double getArea(){
    return l * l;
}

public String toString(){
    String s = "Patrat cu coordonatele: ";
    int i;
    for( i = 0; i < 4; i++)
        s = s + "(" + p[i].getX() + "," + p[i].getY() + ") ";
    s = s + ", cu latura de dimensiune = " + l + ", cu perimetrul = " + this.perimeter() + " si cu aria = " + this.getArea() + "\n";
    return s;
}
}



class Circle extends ShapeWithPerimeter implements Area{
private Point[] p = new Point[2];
private double radius;

public Circle(Point[] p){
    this.p = p;
    radius = Math.sqrt((p[0].getX()-p[1].getX())*(p[0].getX()-p[1].getX()) + (p[0].getY()-p[1].getY())*(p[0].getY()-p[1].getY()));
}

//circumferinta
public double perimeter(){
    return 2 * Math.PI * radius;
}
public double getArea(){
    return Math.PI * radius * radius;
}

public String toString(){
    String s = "Cerc cu centrul in punctul: (" + p[0].getX() + "," + p[0].getY() + ")";
    s = s + ", cu raza de dimensiune = " + radius + ", cu circumferinta = " + this.perimeter() + " si cu aria = " + this.getArea() + "\n";
    return s;
}
}

class Triangle extends ShapeWithPerimeter implements Area{
private Point[] p = new Point[3];
private double l1;
private double l2;
private double l3;

public Triangle(Point[] p){
    this.p = p;
    l1 = Math.sqrt((p[0].getX()-p[1].getX())*(p[0].getX()-p[1].getX()) + (p[0].getY()-p[1].getY())*(p[0].getY()-p[1].getY()));

    l2 = Math.sqrt((p[1].getX()-p[2].getX())*(p[1].getX()-p[2].getX()) + (p[1].getY()-p[2].getY())*(p[1].getY()-p[2].getY()));

    l3 = Math.sqrt((p[0].getX()-p[2].getX())*(p[0].getX()-p[2].getX()) + (p[0].getY()-p[2].getY())*(p[0].getY()-p[2].getY()));

}

public double perimeter(){

    return l1 + l2 + l3;
}

public double getArea(){
    double p = this.perimeter()/2 ;
    return Math.sqrt(p * (p-l1)*(p-l2)*(p-l3));
}

public String toString(){
    String s = "Triunghi cu coordonatele: ";
    int i;
    for( i = 0; i < 3; i++)
        s = s + "(" + p[i].getX() + "," + p[i].getY() + ") ";
    s = s + ", cu laturile cu dimensiunile: " + l1 + ", " + l2 + ", " + l3 + ", cu perimetrul = " + this.perimeter() + " si cu aria = " + this.getArea() + "\n";
    return s;
}
}

class ShapeMain{
public static void main(String[] args){
Point[] p1 = new Point[2];
p1[0] = new Point();
p1[1] = new Point();
Shape l1  = new Line();
System.out.println(l1);

Point[] p2 = new Point[3];
p2[0] = new Point();
p2[1] = new Point();
p2[2] = new Point();
Shape a1  = new Angle(p2, 90);
System.out.println(a1);

Point[] p3 = new Point[3];
p3[0] = new Point();
p3[1] = new Point();
p3[2] = new Point();
Shape t1  = new Triangle(p3);
System.out.println(t1);

Point[] p4 = new Point[4];
p4[0] = new Point();
p4[1] = new Point();
p4[2] = new Point();
p4[3] = new Point();
Shape s1  = new Square(p4);
System.out.println(s1);


Point[] p5 = new Point[2];
p5[0] = new Point();
p5[1] = new Point();
Shape c1  = new Circle(p5);
System.out.println(c1);
}
}


