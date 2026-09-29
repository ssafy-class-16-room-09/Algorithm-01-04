// 백준 1520 · 내리막 길
import java.io.*;
import java.util.*;

public class BojDownhillPath {
    static int[][] map;
    static int[][] memo;
    static int n;
    static int m;
    

    static int dp(int i, int j){

        if(memo[i][j] == -1){
            memo[i][j] = 0;

            int[] di = {1,-1,0,0};
            int[] dj = {0,0,1,-1};

            for(int d=0; d<4; d++){
                int ni = i + di[d];
                int nj = j + dj[d];


                if(ni<0 || ni>=n || nj<0 || nj>= m) continue;
                if(map[i][j] >= map[ni][nj]) continue;
                memo[i][j] += dp(ni,nj);

            }
        }

        return memo[i][j];
    }


    static int solution1(){
        memo = new int[n][m];
        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){
                memo[i][j] = -1;
            }
        }

        memo[0][0] = 1;

        return dp(n-1,m-1);
    }

    static int solution2(){
        
        int[][] top = new int[n][m];
        int[][] ways = new int[n][m];
        int[] di = {1,-1,0,0};
        int[] dj = {0,0,1,-1};

        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){ 
                for(int d=0; d<4; d++){
                    int ni = i + di[d];
                    int nj = j + dj[d];

                    if(ni<0 || ni>=n || nj<0 || nj>= m) continue;
                    if(map[i][j] < map[ni][nj]) top[i][j] += 1;
                }   

            }
        }


        Queue<int[]> q = new ArrayDeque<>();

        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){ 
               if(top[i][j]==0) q.offer(new int[]{i,j});
            }
        }
        
        ways[0][0]=1;

        while(!q.isEmpty()){
            int[] ij = q.poll();
            for(int d=0; d<4; d++){
                int ni = ij[0] + di[d];
                int nj = ij[1] + dj[d];

                if(ni<0 || ni>=n || nj<0 || nj>= m) continue;
                if(map[ij[0]][ij[1]] <= map[ni][nj]) continue;

                ways[ni][nj] += ways[ij[0]][ij[1]];
                if(--top[ni][nj] == 0){
                    q.offer(new int[]{ni,nj});
                }

            }   
        }

        return ways[n-1][m-1];
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        map = new int[n][m];

        for(int i=0; i<n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<m; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        
        

        System.out.print(solution2());
    }
}
