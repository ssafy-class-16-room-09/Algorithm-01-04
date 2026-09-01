// 백준 2667 · 단지번호붙이기
// https://app.notion.com/p/363216e328d182569a490134208eaa68?source=copy_link
import java.io.*;
import java.util.*;

public class BojHousingComplex {

    static class Node{
        int x, y;
        Node(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int n =  Integer.parseInt(br.readLine());
        int ret = 0;
        List<Integer> list = new ArrayList<>();
        int [][] arr = new int [n][n];
        boolean [][] visited = new boolean [n][n];

        for(int i = 0; i < n; i++){
            String s =  br.readLine();
            for(int j=0; j<n; j++){
                arr[i][j] = s.charAt(j)-'0';
            }
        }

        Queue<Node> q= new ArrayDeque<>();

        int [] dx = {1,-1,0,0};
        int [] dy = {0, 0,-1,1};

        for(int i =0; i< n; i++){
            for(int j =0; j< n; j++){
                if(visited[i][j] || arr[i][j] == 0) continue;
                else{
                    q.offer(new Node(i,j));
                    visited[i][j] = true;
                    ret++;
                    int sum = 1;
                    while(!q.isEmpty()){
                        Node cur = q.poll();
                        for(int d=0; d<4; d++){
                            int nx =  cur.x + dx[d];
                            int ny =  cur.y + dy[d];

                            if(nx<0 || ny<0 || nx>=n || ny>=n) continue;
                            if(visited[nx][ny]) continue;
                            if(arr[nx][ny] == 1){
                                visited[nx][ny] = true;
                                q.offer(new Node(nx,ny));
                                sum++;
                            }
                        }
                    }
                    list.add(sum);
                }
            }
        }
        list.sort((a,b)->a-b);
        System.out.println(ret);
        for(int a: list){
            System.out.println(a);
        }
    }
}
