// 백준 7579 · 앱
import java.io.*;
import java.util.*;

public class BojApps {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        int sum = 0;

        int[][] app = new int[n+1][2]; //m , c

        for(int i=0; i<2; i++){
            st = new StringTokenizer(br.readLine());
            for(int j=1; j<=n; j++){
                app[j][i] = Integer.parseInt(st.nextToken());
                if(i == 1){
                    sum += app[j][i];
                }
            }
        }

        int[][] memo = new int[n+1][sum+1];

        for(int i=1; i<=n; i++){
            for(int j=0; j<=sum; j++){
                int memory = app[i][0];
                int cost = app[i][1];

                if(j < cost){
                    memo[i][j] = memo[i-1][j];
                }else{
                    memo[i][j] = Math.max(memo[i-1][j], memo[i-1][j-cost] + memory);
                }
            }
        }

        for(int i=0; i<=sum; i++){
            if(memo[n][i] >= m){
                System.out.print(i);
                return;
            }
        }

       System.out.print(0);
    }
}
