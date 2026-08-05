import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class 암호생성기 {

  static class Node{
    Node next;
    int val;
  }
  public static void main(String[] args) throws FileNotFoundException {
    System.setIn(new FileInputStream("./week01/input.txt"));

    
    Scanner sc = new Scanner(System.in);
    int T;
    T=10;
      
    for(int test_case = 1; test_case <= T; test_case++)
    {
      sc.nextInt();
      int n = 8;
      int[] mem = new int[n];
      
      
      for(int i=0; i<n; i++){
          mem[i] = sc.nextInt();
      }
      
      Node start = new Node();
      start.val = mem[0];
      Node cur = start;
      for(int i=1; i<n; i++){
          Node next = new Node();
          next.val = mem[i];
          cur.next = next;
          cur = next;
      }

      cur.next = start;
      cur = start;
      int cycle = 1;
      while(true){
          if(cycle > 5){
            cycle = 1;
          }
          cur.val -= cycle;
          if(cur.val <= 0){
            cur.val = 0;
            break;
          }

          
          cycle++;
          cur = cur.next;

      }

      cur = cur.next;
      System.out.print("#" + test_case + " ");
      for(int i =0; i <n; i++){
        System.out.print(cur.val + " ");
        cur = cur.next;
      }

      System.out.println();
    }
  }
}