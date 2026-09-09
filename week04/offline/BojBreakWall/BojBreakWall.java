// 백준 2206 · 벽 부수고 이동하기
// https://app.notion.com/p/027216e328d182e9b4e901fc4baea044?source=copy_link
import java.io.*;
import java.util.*;

public class BojBreakWall {
    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(bf.readLine());
        int h = Integer.parseInt(st.nextToken());
        int w = Integer.parseInt(st.nextToken());

        int[][] map = new int[h][w];
        boolean[][][] visit = new boolean[h][w][2];

        for(int i=0; i<h; i++){
            String s = bf.readLine();
            for(int j=0; j<w; j++){
                map[i][j] = Integer.valueOf(s.charAt(j) - '0');
            }
        }

        ArrayDeque<int[]> q = new ArrayDeque<>(); //i,j,가중치,벽 부순 횟수

        q.offer(new int[]{0,0,1,0});
        visit[0][0][0] = true;
        visit[0][0][1] = true;

        int[] di = {1,-1,0,0};
        int[] dj = {0,0,1,-1};
        int ans = -1;

        if(w == 1 && h == 1){
            ans = 1;
        }

        while(!q.isEmpty() && ans == -1){
            int[] info = q.poll();
            int i = info[0];
            int j = info[1];
            int v = info[2];
            int wall = info[3];

            for(int d=0; d<4; d++){
                int ni = i + di[d];
                int nj = j + dj[d];
                int nv = v + 1;

                if(ni < 0 || ni >= h || nj < 0 || nj >= w ) continue;
                if(visit[ni][nj][wall]) continue;
                if(wall > 0 && map[ni][nj] == 1) continue;

                if(ni == h-1 && nj == w-1){
                    ans = nv;
                    break;
                }

                if(map[ni][nj] == 0){
                    q.add(new int[]{ni,nj,nv,wall});
                    visit[ni][nj][wall] = true;
                }else{
                    q.add(new int[]{ni,nj,nv,1});
                    visit[ni][nj][1] = true;
                }

            }   
        }

        System.out.println(ans);

    }
}
