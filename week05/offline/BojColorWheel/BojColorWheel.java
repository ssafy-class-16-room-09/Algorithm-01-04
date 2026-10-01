// 백준 2482 · 색상환
import java.io.*;

public class BojColorWheel {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(br.readLine());
        int k = Integer.parseInt(br.readLine());

        int[][] memo = new int[n+1][n+1];

        for(int i=1; i<=n; i++){
            memo[i][1] = i;
            memo[i][0] = 1;
        }

        for(int i=3; i<=n; i++){
            for(int j=2; j<=(i+1)/2; j++){
                memo[i][j] = (memo[i-2][j-1] + memo[i-1][j]) % 1_000_000_003;
            }
        }
        



        System.out.println((memo[n-3][k-1] + memo[n-1][k]) % 1_000_000_003);
    }
}
