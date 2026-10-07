// 백준 1715 · 카드 정렬하기
import java.io.*;
import java.util.*;

public class BojCardSort {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int N = Integer.parseInt(br.readLine());
        PriorityQueue<Integer> q = new PriorityQueue<>();

        for(int i=0; i<N; i++){
            q.offer(Integer.parseInt(br.readLine()));
        }

        int ans = 0;
        while(q.size() > 1){
            int newDeck = 0;
            newDeck += q.poll();
            newDeck += q.poll();

            q.offer(newDeck);
            ans += newDeck;
        }

        System.out.print(ans);
    }
}
