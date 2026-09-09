// 백준 2667 · 단지번호붙이기
// https://app.notion.com/p/363216e328d182569a490134208eaa68?source=copy_link
import java.io.*;
import java.util.*;

public class BojHousingComplex {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(st.nextToken());
        ArrayDeque<int[]> homes = new ArrayDeque<>();
        int[][] map = new int[n][n];
        boolean[][] visit = new boolean[n][n];


        for(int i=0; i<n; i++){
            String s = br.readLine();
            for(int j=0; j<n; j++){
                
                char c = s.charAt(j);
                map[i][j] = Integer.valueOf(c - '0');
                if(c == '1'){
                    homes.offer(new int[]{i,j});
                }
            }
        }
        
        ArrayList<Integer> ans = new ArrayList<>();

        while(!homes.isEmpty()){
            int [] cur = homes.poll();
            
            if(visit[cur[0]][cur[1]]) continue;
            
            int num = 0;
            
            ArrayDeque<int[]> q = new ArrayDeque<>();
            q.offer(cur);
            num++;
            visit[cur[0]][cur[1]] = true;
            
            int[] di = {1,-1,0,0};
            int[] dj = {0,0,1,-1};

            while(!q.isEmpty()){
                int[] ij = q.poll();

                for(int d=0; d<4; d++){
                    int ni = ij[0] + di[d];
                    int nj = ij[1] + dj[d];
                    

                    if(ni < 0 || ni >= n || nj < 0 || nj >= n) continue;
                    if(map[ni][nj] == 1 && !visit[ni][nj]){
                        num++;
                        visit[ni][nj] = true;
                        q.offer(new int[]{ni,nj});
                    }
                    

                }
            }

            ans.add(num);
        }


        ans.sort(Comparator.naturalOrder());

        System.out.println(ans.size());
        for(int i : ans){
            System.out.println(i);
        }
        
    }
}
