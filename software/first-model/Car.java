public class Car
{
   private int lane;
   private int position;
   private int speed;
   
   public Car(int startLane, int startPosition, int startSpeed)
   {
      this.lane = startLane;
      this.position = startPosition;
      this.speed = startSpeed;
   }
   
   public int getLane()
   {
      return this.lane;
   }
   
   public int getPosition()
   {
      return this.position;
   }
   
   public int getSpeed()
   {
      return this.speed;
   }
   
   public void changeLane()
   {
      if (this.lane == 2)
      {
         this.lane = 1;
      }
      else
      {
         this.lane = 2;
      }
   }
   
   public void setLane(int newLane)
   {
      this.lane = newLane;
   }
   
   public void setPosition(int newPosition)
   {
      this.position = newPosition;
   }
   
   public void setSpeed(int newSpeed)
   {
      this.speed = newSpeed;
   }
   
   public String toString()
   {
      return 
         "[lane=" + this.lane + ", " +
         "position=" + this.position + ", " +
         "speed=" + this.speed + "]";
   }
   
   public static boolean isPositionOpen(int checkLane, int checkPosition, Car[] cars, int carCount)
   {
      boolean newPos = true;
      
      for (int i = 0; i < carCount; i++)
      {
         if (checkLane == cars[i].getLane())
         {
            if (checkPosition == cars[i].getPosition())
            {
               newPos = false;
               break;
            }
         }
      }
      
      return newPos;
   } 
   
   public static Car[] sort(Car[] cars, int carCount)
   {
      boolean swapped = true;
      Car tempCar;
      
      while (swapped)
      {
         swapped = false;
         
         for (int i = 0; i < (carCount - 1); i++)
         {
            if (cars[i].getLane() == cars[i+1].getLane())
            {
               if (cars[i].getPosition() < cars[i+1].getPosition())
               {
                  tempCar = cars[i];
                  cars[i] = cars[i+1];
                  cars[i+1] = tempCar;
                  swapped = true;
               }
            }
         }
      }
      
      return cars;
   }
   
   public static Car[] computeNext(Car[] cars, int carCount, int roadLength)
   {
      boolean newPos = false;
      int nextPos = 0;
      
      for (int i = (carCount - 1); i >= 0; i--)
      {         
         nextPos = cars[i].getPosition() + cars[i].getSpeed();
         
         if (cars[i].getLane() == 1)
         {
            if (Car.isPositionOpen(2, nextPos, cars, carCount))
            {
               cars[i].changeLane();
               cars[i].setPosition(nextPos);
            }
         }
         
         if (!Car.isPositionOpen(cars[i].getLane(), nextPos, cars, carCount))
         {
            cars[i].changeLane();
            
            if (!Car.isPositionOpen(cars[i].getLane(), cars[i].getPosition(), cars, carCount))
            {
               cars[i].changeLane();
            }
            else
            {
               cars[i].setPosition(nextPos);
            }   
         }
         else
         {
            cars[i].setPosition(nextPos);
         }
         
         if (cars[i].getPosition() > roadLength)
         {
            cars[i].setPosition(cars[i].getPosition() - roadLength);
         }
      }
      
      return cars;
   }
}