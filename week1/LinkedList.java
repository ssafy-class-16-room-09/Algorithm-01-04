import java.util.Objects;

import codingtest.codingtest;
import codingtest.codingtest.MyLinkedList;
import codingtest.codingtest.Node;

public class LinkedList {
	
	public class MyLinkedList<T>{
			
			Node<T> headNode;
			Node<T> tailNode;
			int size;
			
			public MyLinkedList() {}
			
	
	
			void addFirst(T val) {
				Node<T> newNode = new Node<>(val, headNode, null);
				if(headNode == null) {
					headNode = newNode;
					tailNode = newNode;
				}
				else {
					headNode.prev_node = newNode;
					headNode = newNode;
				}
				size++;
			}
			
			void addLast(T val) {
				Node<T> newNode = new Node<>(val, null, tailNode);
				if(tailNode == null) {
					headNode = newNode;
					tailNode = newNode;
				}
				else {
					tailNode.next_node = newNode;
			        newNode.prev_node = tailNode;
					tailNode = newNode;
				}
				size++;
			} 
			
			void insertAt(int idx, T val) {
				
				Node<T> curNode= headNode;
				
				for(int i =0; i<idx; i++) {
					curNode = curNode.next_node;
				}
				Node<T> newNode = new Node<>(val, curNode, curNode.prev_node);
				
				curNode.prev_node.next_node = newNode;
				curNode.prev_node = newNode;
				size++;
			}
			
			
			boolean removeByValue(T val) {
				Node<T> curNode= headNode;
				for(int i =0; i<this.size; i++) {
					if(curNode.val.equals(val)) {
						curNode.prev_node.next_node = curNode.next_node;
						curNode.next_node.prev_node = curNode.prev_node;	
						size--;
						return true;
					}
					curNode = curNode.next_node;
					
				}
				return false;
			}
			
			T removeAt(int idx) {
			    Node<T> curNode = headNode;
	
			    for (int i = 0; i < idx; i++) {
			        curNode = curNode.next_node;
			    }
	
			    if (curNode.prev_node == null) {
			    } else {
			        curNode.prev_node.next_node = curNode.next_node;
			    }
	
			    if (curNode.next_node == null) {
			        tailNode = curNode.prev_node;
			    } else {
			        curNode.next_node.prev_node = curNode.prev_node;
			    }
			    size--;
	
			    T removedValue = curNode.val;
			    return removedValue;
			}
			
			int indexOf(T val) {
				
				Node<T> curNode = headNode;
	
				for(int i =0; i<size; i++) {
					if(curNode.val==val) {
						return i;
					}
					curNode = curNode.next_node;
				}
				return -1;
			}
			
			T get(int idx) {
				
				Node<T> curNode = headNode;
				if(this.size()!=0) {
					for(int i =0; i<idx; i++) {
						curNode = curNode.next_node;
					}
				}
				return curNode.val;
			}
			
			int size() {
				return this.size;
			}
			
		}
		
		public class Node<T>{
			T val;
			Node<T> next_node;
			Node<T> prev_node;
		
			
			public Node() {}
			
			public Node(T val, Node<T> next_node, Node<T> prev_node) {
				this.val = val;
				this.next_node = next_node;
				this.prev_node = prev_node;
			
			}
		}
		
		
		public static void main(String[] args) { //ai가 짜준 테스트 코드
	
		    codingtest outer = new codingtest();
		    MyLinkedList<Integer> list = outer.new MyLinkedList<>();
	
		    // 1. 초기 상태 테스트
		    if (list.size() != 0) {
		        throw new AssertionError("초기 size는 0이어야 합니다.");
		    }
	
		    // 2. addFirst 테스트
		    list.addFirst(20);
		    list.addFirst(10);
	
		    if (list.size() != 2) {
		        throw new AssertionError("addFirst 후 size가 올바르지 않습니다.");
		    }
	
		    if (!Objects.equals(list.get(0), 10)) {
		        throw new AssertionError("첫 번째 값은 10이어야 합니다.");
		    }
	
		    if (!Objects.equals(list.get(1), 20)) {
		        throw new AssertionError("두 번째 값은 20이어야 합니다.");
		    }
	
		    // 3. addLast 테스트
		    list.addLast(40);
	
		    if (!Objects.equals(list.get(2), 40)) {
		        throw new AssertionError("addLast로 추가한 값은 40이어야 합니다.");
		    }
	
		    // 현재 상태: [10, 20, 40]
	
		    // 4. insertAt 테스트
		    list.insertAt(2, 30);
	
		    if (!Objects.equals(list.get(2), 30)) {
		        throw new AssertionError("인덱스 2의 값은 30이어야 합니다.");
		    }
	
		    if (!Objects.equals(list.get(3), 40)) {
		        throw new AssertionError("인덱스 3의 값은 40이어야 합니다.");
		    }
	
		    // 현재 상태: [10, 20, 30, 40]
	
		    // 5. indexOf 테스트
		    if (list.indexOf(30) != 2) {
		        throw new AssertionError("30의 인덱스는 2여야 합니다.");
		    }
	
		    if (list.indexOf(100) != -1) {
		        throw new AssertionError("존재하지 않는 값은 -1을 반환해야 합니다.");
		    }
	
		    // 6. removeByValue 테스트
		    boolean removed = list.removeByValue(20);
	
		    if (!removed) {
		        throw new AssertionError("20 삭제에 성공해야 합니다.");
		    }
	
		    if (list.indexOf(20) != -1) {
		        throw new AssertionError("삭제된 20은 리스트에 없어야 합니다.");
		    }
	
		    // 현재 상태: [10, 30, 40]
	
		    // 7. removeAt 테스트
		    int removedValue = list.removeAt(1);
	
		    if (removedValue != 30) {
		        throw new AssertionError("인덱스 1에서 삭제된 값은 30이어야 합니다.");
		    }
	
		    if (list.size() != 2) {
		        throw new AssertionError("삭제 후 size는 2여야 합니다.");
		    }
	
		    // 현재 상태: [10, 40]
	
		    if (!Objects.equals(list.get(0), 10)
		            || !Objects.equals(list.get(1), 40)) {
		        throw new AssertionError("최종 리스트는 [10, 40]이어야 합니다.");
		    }
	
		    System.out.println("모든 테스트 통과!");
		}
}
