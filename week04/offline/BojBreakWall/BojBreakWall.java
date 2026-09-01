// 백준 2206 · 벽 부수고 이동하기
// https://app.notion.com/p/027216e328d182e9b4e901fc4baea044?source=copy_link
import java.io.*;
import java.util.*;

public class BojBreakWall {

    static class Node {
        int x;
        int y;
        boolean destroy;

        Node(int x, int y, boolean destroy) {
            this.x = x;
            this.y = y;
            this.destroy = destroy;
        }

    }


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n, m;
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        int[][] arr = new int[n][m];
        int[][][] visited = new int[n][m][2];

        for (int i = 0; i < n; i++) {
            String s = br.readLine();
            for (int j = 0; j < m; j++) {
                arr[i][j] = s.charAt(j) - '0';
            }
        }

        Queue<Node> q = new ArrayDeque<>();
        q.offer(new Node(0, 0, false));
        visited[0][0][0] = 1;

        while (!q.isEmpty()) {
            Node cur = q.poll();

            int isDestroyed = cur.destroy ? 1 : 0;

            if (cur.x == n - 1 && cur.y == m - 1) { // 도착지에 끝.
                System.out.println(visited[cur.x][cur.y][isDestroyed]);
                return;
            }

            for (int i = 0; i < 4; i++) {
                int nx = cur.x + dx[i];
                int ny = cur.y + dy[i];
                if (nx < 0 || ny < 0 || nx >= n || ny >= m) continue;

                if (arr[nx][ny] == 1) { //벽이면
                    if (cur.destroy) continue;
                    if (visited[nx][ny][1] != 0) continue;

                    visited[nx][ny][1] = visited[cur.x][cur.y][0] + 1;
                    q.offer(new Node(nx, ny, true));

                } else { //0 이면
                    if (visited[nx][ny][isDestroyed] != 0) continue;
                    q.offer(new Node(nx, ny, cur.destroy));
                    visited[nx][ny][isDestroyed] = visited[cur.x][cur.y][isDestroyed] + 1;
                }
            }
        }
        System.out.println(-1);
    }
}
