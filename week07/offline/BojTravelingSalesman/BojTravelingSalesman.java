// 백준 2098 · 외판원 순회
import java.io.*;
import java.util.*;

public class BojTravelingSalesman {

    // 0000 -> 1111 (N) 출발지 ~~~ 출발지
    // 이걸로 어떻게 만드냐?
    // dp ?
    // 목적지로 올수있는 아이들로 거슬러 올라가기?
    // 0000 -> 1,2,3,4

    static int[][] memo;
    static int[][] edges;
    static int N;
    static int MAX = 100_000_000;

    static int dp(int node, int flag){ 
        // 방문 O -> 1, 방문 X -> 0
        // 1번부터 시작
        // top-down -> 10000 시작
        if (flag == (1 << N) - 1) {
            return edges[node][0] == 0 ? MAX : edges[node][0];
        }
        
        if(memo[node][flag] == -1){
            memo[node][flag] = MAX;
            for(int i=1;i<N;i++){ 
                    if((flag & (1<<i)) != 0) continue; 
                    if(edges[node][i] == 0) continue;
                    
                    memo[node][flag] = Math.min(dp(i, flag | (1 << i)) + edges[node][i], memo[node][flag]);
                }
        }

        return memo[node][flag];
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = null;
        N = Integer.parseInt(br.readLine());

        edges = new int[N][N];

        for(int i=0; i<N; i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<N; j++){
                edges[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        memo = new int[N][1 << N];
        for(int i=0; i<N; i++){
            for(int j=0; j<1 << N; j++){
                memo[i][j] = -1;
            }
        }
        
        System.out.print(dp(0,1));
    }
}
