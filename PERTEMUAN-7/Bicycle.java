public class Bicycle {
int speed=0;
int gear=0;
//method
void changeGear(int newValue) {
gear = gear + newValue;
System.err.println("Gear: " + gear);
}
void speedUp(int increment) {
speed = speed + increment;
System.err.println("Speed: " + speed);
}
}