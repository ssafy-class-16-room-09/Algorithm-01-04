// 백준 12865 · 평범한 배낭
import java.io.*;
import java.util.*;

public class BojOrdinaryKnapsack {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(st.nextToken());
        int k = Integer.parseInt(st.nextToken());
    
        int[][] pack = new int[n][k+1];
        int[][] item = new int[n][2]; // w,v

        for(int i=0; i<n; i++){
            st = new StringTokenizer(br.readLine());
            item[i][0] = Integer.parseInt(st.nextToken());
            item[i][1] = Integer.parseInt(st.nextToken());
        }

        for(int i=item[0][0]; i<=k; i++){
            pack[0][i] = item[0][1];
        }

        for(int i=1; i<n; i++){
            int w= item[i][0];
            int v= item[i][1];
            
            for(int j=1; j<=k; j++){
                if(j < w){
                    pack[i][j] = pack[i-1][j];
                }else{
                    pack[i][j] = Math.max(pack[i-1][j], pack[i-1][j-w] + v);
                }
            }
        }


        System.out.print(pack[n-1][k]);
    }
}
