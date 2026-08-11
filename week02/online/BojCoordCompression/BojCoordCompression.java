// 백준 · 좌표 압축
// https://www.notion.so/3b8216e328d1808fa149d820442afb49?source=copy_link
import java.io.*;
import java.util.*;

public class BojCoordCompression {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(st.nextToken());
        int[] cors = new int[n];
        int[] ans = new int[n];

        st = new StringTokenizer(br.readLine());
        for(int i=0; i<n; i++){
            cors[i] = Integer.parseInt(st.nextToken());
            ans[i] = cors[i];
        }

        HashMap<Integer, Integer> corRank = new HashMap<>();
        Arrays.sort(cors);

        int rank = 0;

        for(int cor: cors){
            if(!corRank.containsKey(cor)){
                corRank.put(cor, rank++);
            }
        }

        for(int i=0; i<n; i++){
            System.out.print(corRank.get(ans[i]) + " ");
        }


        

    }
}
