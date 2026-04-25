// Parent class Shape
class Shape {
    protected String color;
    protected boolean filled;
    
    Shape() {
        color = "red";
        filled = true;
    }
    
    Shape(String color, boolean filled) {
        this.color = color;
        this.filled = filled;
    }
    
    String getColor() {
        return color;
    }
    
    void setColor(String color) {
        this.color = color;
    }
    
    boolean isFilled() {
        return filled;
    }
    
    void setFilled(boolean filled) {
        this.filled = filled;
    }
    
    public String toString() {
        return "Shape[color=" + color + ",filled=" + filled + "]";
    }
}

// Child class Circle
class Circle extends Shape {
    protected double radius;
    
    Circle() {
        super();
        radius = 1.0;
    }
    
    Circle(double radius) {
        super();
        this.radius = radius;
    }
    
    Circle(double radius, String color, boolean filled) {
        super(color, filled);
        this.radius = radius;
    }
    
    double getRadius() {
        return radius;
    }
    
    void setRadius(double radius) {
        this.radius = radius;
    }
    
    double getArea() {
        return Math.PI * radius * radius;
    }
    
    double getPerimeter() {
        return 2 * Math.PI * radius;
    }
    
    public String toString() {
        return "Circle[radius=" + radius + "," + super.toString() + "]";
    }
}

// Child class Rectangle
class Rectangle extends Shape {
    protected double width;
    protected double length;
    
    Rectangle() {
        super();
        width = 1.0;
        length = 1.0;
    }
    
    Rectangle(double width, double length) {
        super();
        this.width = width;
        this.length = length;
    }
    
    Rectangle(double width, double length, String color, boolean filled) {
        super(color, filled);
        this.width = width;
        this.length = length;
    }
    
    double getWidth() {
        return width;
    }
    
    void setWidth(double width) {
        this.width = width;
    }
    
    double getLength() {
        return length;
    }
    
    void setLength(double length) {
        this.length = length;
    }
    
    double getArea() {
        return width * length;
    }
    
    double getPerimeter() {
        return 2 * (width + length);
    }
    
    public String toString() {
        return "Rectangle[width=" + width + ",length=" + length + "," + super.toString() + "]";
    }
}

// Child class Square (extends Rectangle)
class Square extends Rectangle {
    Square() {
        super();
    }
    
    Square(double side) {
        super(side, side);
    }
    
    Square(double side, String color, boolean filled) {
        super(side, side, color, filled);
    }
    
    double getSide() {
        return width;
    }
    
    void setSide(double side) {
        this.width = side;
        this.length = side;
    }
    
    void setWidth(double side) {
        this.width = side;
        this.length = side;
    }
    
    void setLength(double side) {
        this.width = side;
        this.length = side;
    }
    
    public String toString() {
        return "Square[side=" + width + "," + super.toString() + "]";
    }
}

// Main class untuk testing
public class ShapeDemo {
    public static void main(String[] args) {
        // Test Rectangle
        System.out.println("===== Rectangle =====");
        Rectangle rect = new Rectangle(5.0, 3.0, "Blue", true);
        
        System.out.println("Warna : " + rect.getColor());
        System.out.println("Tinggi : " + rect.getLength());
        System.out.println("Lebar : " + rect.getWidth());
        System.out.println("Luas : " + rect.getArea());
        System.out.println();
        
        // Test Circle
        System.out.println("===== Circle =====");
        Circle circle = new Circle(2.5, "Red", true);
        System.out.println("Warna : " + circle.getColor());
        System.out.println("Radius : " + circle.getRadius());
        System.out.println("Luas : " + circle.getArea());
        System.out.println("Keliling : " + circle.getPerimeter());
        System.out.println();
        
        // Test Square
        System.out.println("===== Square =====");
        Square square = new Square(4.0, "Green", true);
        System.out.println("Warna : " + square.getColor());
        System.out.println("Sisi : " + square.getSide());
        System.out.println("Luas : " + square.getArea());
        System.out.println("Keliling : " + square.getPerimeter());
    }
}
