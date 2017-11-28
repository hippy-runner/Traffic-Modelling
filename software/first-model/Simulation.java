import java.util.Scanner;
import java.util.Random;

public class Simulation
{
   public static void main(String[] args)
   {
      Scanner keyboard = new Scanner(System.in);
      Random rnd = new Random();
      
      boolean turnContinue = true;
      int carCount = 0;
      int roadLength = 0;
      Car[] cars;
      
      System.out.print("Length of road: ");
      roadLength = keyboard.nextInt();
      
      System.out.println();
      System.out.print("Number of cars: ");
      carCount = keyboard.nextInt();
      
      cars = new Car[carCount];
      
      for (int i = 0; i < carCount; i++)
      {
         cars[i] = new Car(2,0,0);
         
         switch (rnd.nextInt(4))
         {
            case 0:
               cars[i].setSpeed(1);
               break;
               
            case 1:
               cars[i].setSpeed(2);
               break;
            
            case 2:
               cars[i].setSpeed(2);
               break;
            
            case 3:
               cars[i].setSpeed(3);
               break;
            
            default:
               cars[i].setSpeed(2);
               break;   
         }
      }
      
      for (int i = 0; i < carCount; i++)
      {
         boolean newPos = false;
         int pos = 0;
         int count = 0;
         
         while (!newPos && count < 1000)
         {
            newPos = true;
            pos = rnd.nextInt(roadLength);
            
            newPos = Car.isPositionOpen(cars[i].getLane(), pos, cars, carCount);
            
            count++;        
         }
         
         cars[i].setPosition(pos);
      }
      
      while (turnContinue)
      {
         cars = Car.sort(cars, carCount);
         
         System.out.println();
         for (int i = 0; i < carCount; i++)
         {
            System.out.println(cars[i].toString());
         }
         
         System.out.println();
         System.out.println("Continue? (true/false): ");
         turnContinue = keyboard.nextBoolean();
                  
         cars = Car.computeNext(cars, carCount, roadLength);
      }
   }
}