package codingtest;

import java.io.*;
import java.util.*;

public class swea1225 {

	public static void main(String[] args) throws Exception{
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
		
		int num;
		Queue<Integer> q = new ArrayDeque<>();

		for(int i =0; i<10; i++) { // 10번 반복
			num = Integer.parseInt(br.readLine());
			StringTokenizer st = new StringTokenizer(br.readLine());

			for(int j=0; j<8; j++) {
				q.offer(Integer.parseInt(st.nextToken()));
			}
			
			method(q);
			
			System.out.print("#" + num);
			
			for(int a: q) {
				System.out.print(" " + a);
			}
			System.out.println();
			
			q.clear();
		}
		
		
		
	}
	
	public static void method(Queue<Integer> q) {
		int cur_num =1;
		while(cur_num>0) {
			for(int i=1; i<=5; i++) {
				cur_num = q.poll() - i;
				if(cur_num<=0) {
					q.offer(0);
					break;
				}
				else {
					q.offer(cur_num);
				}
			}	
		}
		
	}

}
