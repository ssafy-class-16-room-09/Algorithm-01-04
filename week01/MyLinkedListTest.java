public class MyLinkedListTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        tc1_basicInsertGetSize();
        tc2_insertAtAllPositions();
        tc3_indexOf();
        tc4_removeByValue();
        tc5_removeAtWithReturnValue();
        tc6_emptyListRoundTrip();
        tc7_invalidIndex();

        System.out.println();
        System.out.println("결과: " + passed + " PASS / " + failed + " FAIL");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ── TC1: 기본 삽입 + get/size ─────────────────────────────
    static void tc1_basicInsertGetSize() {
        System.out.println("[TC1] 기본 삽입 + get/size");
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addFirst(1);
        list.addFirst(2);
        list.addLast(3);

        check("get(0)", 2, list.get(0));
        check("get(1)", 1, list.get(1));
        check("get(2)", 3, list.get(2));
        check("size()", 3, list.size());
        check("전체 상태", "[2, 1, 3]", contents(list));
    }

    // ── TC2: insertAt 세 위치 전부 ────────────────────────────
    static void tc2_insertAtAllPositions() {
        System.out.println("[TC2] insertAt 세 위치 전부");
        MyLinkedList<Integer> list = new MyLinkedList<>(2, 1, 3);

        list.insertAt(0, 5);                       // 맨 앞
        check("insertAt(0,5) 후", "[5, 2, 1, 3]", contents(list));

        list.insertAt(2, 9);                       // 중간
        check("insertAt(2,9) 후", "[5, 2, 9, 1, 3]", contents(list));

        list.insertAt(5, 7);                       // 맨 뒤 (idx == size)
        check("insertAt(5,7) 후", "[5, 2, 9, 1, 3, 7]", contents(list));

        check("size()", 6, list.size());
    }

    // ── TC3: 탐색 (indexOf) ──────────────────────────────────
    static void tc3_indexOf() {
        System.out.println("[TC3] indexOf");
        MyLinkedList<Integer> list = new MyLinkedList<>(5, 2, 9, 1, 3, 7);

        check("indexOf(5) 첫 원소", 0, list.indexOf(5));
        check("indexOf(9) 중간", 2, list.indexOf(9));
        check("indexOf(7) 마지막", 5, list.indexOf(7));
        check("indexOf(100) 없는 값", -1, list.indexOf(100));

        list.addLast(9);                           // 중복 값 추가
        check("중복 값은 앞쪽 인덱스", 2, list.indexOf(9));
        list.removeAt(6);                          // 원상복구
        check("복구 후 상태", "[5, 2, 9, 1, 3, 7]", contents(list));
    }

    // ── TC4: 값으로 삭제 (removeByValue) ─────────────────────
    static void tc4_removeByValue() {
        System.out.println("[TC4] removeByValue");
        MyLinkedList<Integer> list = new MyLinkedList<>(5, 2, 9, 1, 3, 7);

        check("removeByValue(5) 반환", true, list.removeByValue(5));
        check("첫 노드 삭제 후", "[2, 9, 1, 3, 7]", contents(list));

        check("removeByValue(1) 반환", true, list.removeByValue(1));
        check("중간 노드 삭제 후", "[2, 9, 3, 7]", contents(list));

        check("removeByValue(7) 반환", true, list.removeByValue(7));
        check("마지막 노드 삭제 후", "[2, 9, 3]", contents(list));

        check("removeByValue(100) 반환", false, list.removeByValue(100));
        check("없는 값 삭제 시 리스트 불변", "[2, 9, 3]", contents(list));
        check("size()", 3, list.size());
    }

    // ── TC5: 위치로 삭제 + 반환값 (removeAt) ─────────────────
    static void tc5_removeAtWithReturnValue() {
        System.out.println("[TC5] removeAt + 반환값");
        MyLinkedList<Integer> list = new MyLinkedList<>(2, 9, 3);

        check("removeAt(0) 반환", 2, list.removeAt(0));
        check("첫 인덱스 삭제 후", "[9, 3]", contents(list));

        check("removeAt(1) 반환", 3, list.removeAt(1));
        check("마지막 인덱스 삭제 후", "[9]", contents(list));

        check("size()", 1, list.size());
    }

    // ── TC6: 경계 — 빈 리스트 왕복 ───────────────────────────
    static void tc6_emptyListRoundTrip() {
        System.out.println("[TC6] 빈 리스트 왕복");
        MyLinkedList<Integer> list = new MyLinkedList<>(9);

        check("removeAt(0) 반환", 9, list.removeAt(0));
        check("비운 뒤 size()", 0, list.size());

        check("빈 리스트 removeByValue(5)", false, list.removeByValue(5));
        check("빈 리스트 indexOf(5)", -1, list.indexOf(5));

        // 비운 뒤 재사용 — 더미 노드/링크 복구 검증
        list.addLast(4);
        check("재사용 addLast(4) 후 get(0)", 4, list.get(0));
        check("재사용 후 상태", "[4]", contents(list));
    }

    // ── TC7: 잘못된 인덱스 처리 (예외 방식) ──────────────────
    static void tc7_invalidIndex() {
        System.out.println("[TC7] 잘못된 인덱스 (IndexOutOfBoundsException)");
        MyLinkedList<Integer> list = new MyLinkedList<>(4);

        checkThrows("get(-1)", () -> list.get(-1));
        checkThrows("get(1)", () -> list.get(1));
        checkThrows("removeAt(1)", () -> list.removeAt(1));
        checkThrows("insertAt(3, 0)", () -> list.insertAt(3, 0));

        // 예외가 났어도 리스트는 변하지 않아야 한다
        check("예외 후 리스트 불변", "[4]", contents(list));
        check("예외 후 size()", 1, list.size());
    }

    // ── 헬퍼 ─────────────────────────────────────────────────


    /** get(i)를 0..size-1 순회해 "[a, b, c]" 형태로 반환 */
    static String contents(MyLinkedList<Integer> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i));
        }
        return sb.append("]").toString();
    }

    static void check(String name, Object expected, Object actual) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name + "  기대: " + expected + ", 실제: " + actual);
        }
    }

    static void checkThrows(String name, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("  FAIL  " + name + "  기대: IndexOutOfBoundsException, 실제: 예외 없음");
        } catch (IndexOutOfBoundsException e) {
            passed++;
            System.out.println("  PASS  " + name);
        } catch (RuntimeException e) {
            failed++;
            System.out.println("  FAIL  " + name + "  기대: IndexOutOfBoundsException, 실제: " + e.getClass().getSimpleName());
        }
    }
}
