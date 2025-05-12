public class Main {
    static void myFunction(){
      int []cars ={ 20,34,12,34,56,54,32,79,5};
      int min =cars[0];
      for (int i=0; i<cars.length ;i++){
          if (cars[i]<min){
              min=cars[i];
          }
      }
      System.out.println(min);
      
    }
    public static void main(String[]args){
        myFunction();
    }
}
