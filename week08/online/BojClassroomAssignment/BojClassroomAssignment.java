// 백준 11000 · 강의실 배정
import java.io.*;
import java.util.*;

public class BojClassroomAssignment {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    
        int N = Integer.parseInt(br.readLine());
        

        int[][] info = new int[N][2];
        StringTokenizer st = null;

        for(int i=0; i<N; i++){
            st = new StringTokenizer(br.readLine());
            info[i][0] = Integer.parseInt(st.nextToken());
            info[i][1] = Integer.parseInt(st.nextToken()); 
        }

        Arrays.sort(info, (i1,i2) -> (i1[0] == i2[0])? Integer.compare(i1[1],i2[1]) : Integer.compare(i1[0], i2[0]));
        PriorityQueue<Integer> q = new PriorityQueue<>(); //s,e
        q.add(info[0][1]);
        for(int i=1; i<N; i++){
            if(!q.isEmpty() && q.peek()<=info[i][0]){
                q.poll();
            }

            q.offer(info[i][1]);
        }


        System.out.print(q.size());
    }
}
